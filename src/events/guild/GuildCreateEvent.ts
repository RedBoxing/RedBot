import { Guild } from "discord.js";
import client from "../../client/client";
import GuildConfig from "../../database/models/GuildConfig";
import BaseEvent from "../../structures/base/BaseEvent";

export default class GuildCreateEvent extends BaseEvent {
    constructor() {
        super("guildCreate");
    }

    public async exec(client: client, guild: Guild): Promise<void> {
        let config = await GuildConfig.findOne({
            where: {
                guildId: guild.id
            }
        });

        if(!config) {
            await GuildConfig.create({
                guildId: guild.id
            });
        }
    }
}