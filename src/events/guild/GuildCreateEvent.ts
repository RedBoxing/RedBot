import { SlashCommandBuilder } from "@discordjs/builders";
import { REST } from "@discordjs/rest";
import { Routes } from "discord-api-types/v9";
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

        const rest = new REST({ version: '9' }).setToken(process.env.BOT_TOKEN);

        await rest.put(
            Routes.applicationGuildCommands(client.user.id, guild.id),
            {
                body: Array.from(client.getCommands()).map(([key, value]) => (value.build(new SlashCommandBuilder().setName(value.getName()).setDescription(value.getDescription())).toJSON()))
            }
        );
    }
}