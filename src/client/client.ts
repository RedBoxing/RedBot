import * as Discord from 'discord.js'
import { Manager, NodeOptions } from 'erela.js';

import BaseCommand from '../structures/base/BaseCommand';
import BaseEvent from '../structures/base/BaseEvent';
import CommandHandler from '../structures/CommandHandler';
import BotConfigurable from '../structures/config/BotConfigurable';
import EventHandler from '../structures/EventHandler';

export default class DiscordClient extends Discord.Client {
    private commands : CommandHandler;
    private events : EventHandler;
    private config : BotConfigurable;
    private nodes : NodeOptions[];

    constructor(options?: Discord.ClientOptions) {
        super(options);

        this.commands = new CommandHandler();
        this.events = new EventHandler();
        this.config = new BotConfigurable();
        this.nodes = [
            {
                host: process.env.LAVALINK_HOST,
                port: parseInt(process.env.LAVALINK_PORT),
                password: process.env.LAVALINK_PASSWORD
            }
        ]
      
        this.manager = new Manager({
            nodes: this.nodes,
            send: (id, payload) => {    
                const guild = this.guilds.cache.get(id);
                if(guild) guild.shard.send(payload);
            }
        })
    }

    public addCommand(command: BaseCommand): DiscordClient {
        this.commands.set(command.getName(), command);
        return this;
    }

    public deleteCommand(command: string): DiscordClient | boolean {
        const cmd = this.searchCommand(command);
        if (cmd) {
            this.commands.delete(cmd.getName());
            return this;
        }
        return false;
    }
    
    public searchCommand(command: string) : BaseCommand | null {
        return this.commands.get(command) ? this.commands.get(command) : null;
    }

    public addEvent(event: BaseEvent): void {
        this.events.set(event.getName(), event);
        const name = event.getName();
        this.on(name, event.exec.bind(null, this));
    }

    public addMusicManagerEvent(event: BaseEvent): void {
        const name = event.getName();
        this.manager.on(name, event.exec.bind(null, this));
    }

    public getCommands() : CommandHandler {
        return this.commands;
    }

    public getEvents() : EventHandler {
        return this.events;
    }

    public getConfig() : BotConfigurable {
        return this.config;
    }
}