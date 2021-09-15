import DiscordClient from "../client/client";
import BaseEvent from "../structures/base/BaseEvent";
import * as logger from '../utils/logger'

import { sequelize } from "../database";

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