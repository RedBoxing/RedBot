import * as Discord from 'discord.js'
import logger from '../utils/logger'

import { Manager, NodeOptions } from 'erela.js';

import Spotify from 'erela.js-spotify'
import Deezer from 'erela.js-deezer'

import BaseCommand from '../structures/base/BaseCommand';
import BaseEvent from '../structures/base/BaseEvent';
import CommandHandler from '../structures/CommandHandler';
import BotConfigurable from '../structures/config/BotConfigurable';
import EventHandler from '../structures/EventHandler';
import Translator from '../utils/translator';

export default class DiscordClient extends Discord.Client {
    private commands : CommandHandler;
    private events : EventHandler;
    private config : BotConfigurable;
    private nodes : NodeOptions[];
    private translator : Translator;

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
            },
            plugins: [
                new Spotify({
                    clientID: process.env.SPOTIFY_CLIENT_ID,
                    clientSecret: process.env.SPOTIFY_CLIENT_SECRET
                }),
                new Deezer({})
            ],
            autoPlay: true
        });

        this.translator = new Translator(this);
    }

    public addCommand(command: BaseCommand): DiscordClient {
        logger.info(`Loaded command '${command.getName()}'`)
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
        const name = event.getName();
        this.events.set(name, event);
        logger.info(`Loaded event '${event.getName()}'`);
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

    public getTranslator() : Translator {
        return this.translator;
    }
}