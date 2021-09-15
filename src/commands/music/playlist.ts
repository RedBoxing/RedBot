import { Message, MessageEmbed } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class PlayListCommand extends BaseCommand {
    constructor() {
        super("playlist", "music", ["pl"], []);
    }

    public async exec(client: DiscordClient, message: Message, args: any[]): Promise<void> {
        const player = client.manager.get(message.guild.id);
        if(player) {
            const embed = new MessageEmbed();
            embed.setTitle("Playlist of : " + message.guild.name);
            embed.setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());

            let str = "Current: " + player.queue.current.title + "\n\n";
            player.queue.forEach((track, index) =>  {
                str = str + index + ". " + track.title + "\n";
            });

            embed.setDescription(str);
            message.channel.send(embed);
        }
    }
}