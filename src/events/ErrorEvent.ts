import { MessageEmbed, TextChannel } from "discord.js";
import DiscordClient from "../client/client";
import BaseEvent from "../structures/base/BaseEvent";

export default class ReadyEvent extends BaseEvent {
    constructor() {
        super("error");
    }

    public async exec(client: DiscordClient, error : Error): Promise<void> {
        const guild = await client.guilds.fetch(process.env.BOT_GUILD);
        const channel = await guild.channels.fetch(process.env.BOT_ERROR_CHANNEL) as TextChannel;

        channel.send({
            embeds: [
                new MessageEmbed()
                    .setAuthor("Error !", client.user.avatarURL())
                    .setDescription(error.message + "\n" + error.stack)
                    .setColor('RED')
                    .setFooter((await client.getTranslator().getTranslation(guild.id, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        })
    }
}