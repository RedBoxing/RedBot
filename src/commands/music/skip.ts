import { Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class SkipCommand extends BaseCommand {
    constructor() {
        super("skip", "music", [], []);
    }

    public async exec(client: client, message: Message, args: any[]): Promise<void> {
        const player = client.manager.get(message.guild.id);
        if(player) {
            if (!player.queue.current) {
                message.channel.send(new MessageEmbed()
                    .setDescription("The bot is not playing music !")
                    .setColor("#FF0000")
                    .setAuthor("The bot is not playing music !", client.user.avatarURL())
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
                return;
            }

            player.stop();
        } else {
            message.channel.send(new MessageEmbed()
                .setDescription("The bot is not playing music !")
                .setColor("#FF0000")
                .setAuthor("The bot is not playing music !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
        }
    }
}