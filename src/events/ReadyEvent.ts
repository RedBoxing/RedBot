import DiscordClient from "../client/client";
import BaseEvent from "../structures/base/BaseEvent";
import * as logger from '../utils/logger'

import { sequelize } from "../database";
import { REST } from '@discordjs/rest'
import { Routes } from 'discord-api-types/v9'
import { SlashCommandBuilder } from '@discordjs/builders'

export default class ReadyEvent extends BaseEvent {
    constructor() {
        super("ready");
    }

    public async exec(client: DiscordClient, args: any): Promise<void> {
        logger.info("Initializing database...");

        sequelize.authenticate().then(async () => {
            logger.success("Database successfully initialized !");

            try {
                await sequelize.sync();
            } catch(err) {
                logger.error(err.message);
                client.destroy();
                process.exit(0);
            }

            client.manager.init(client.user.id);
            logger.success("Bot Connected to " + client.guilds.cache.size + " guilds !");
            logger.info("Initializing slashes commandes...");

            const rest = new REST({ version: '9' }).setToken(process.env.BOT_TOKEN);

            await rest.put(
                Routes.applicationGuildCommands(client.user.id, "777281629407805520"),
                {
                    body: Array.from(client.getCommands()).map(([key, value]) => (value.build(new SlashCommandBuilder().setName(value.getName()).setDescription(value.getDescription())).toJSON()))
                }
            );

            logger.success("Loaded slashes commandes !");
        }).catch(err => {
            logger.error("Failed to connect to database : " + err);
        })
            
        const status = client.getConfig().getBotStatus();

        client.user.setActivity(status[Math.floor(Math.random() * (status.length - 1) + 1)], { type: 'WATCHING' });
        setInterval(() => {
            client.user.setActivity(status[Math.floor(Math.random() * (status.length - 1) + 1)], { type: 'WATCHING' });
        }, 10 * 1000);
    }
}