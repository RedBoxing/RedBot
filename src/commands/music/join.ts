import { Message, MessageEmbed } from "discord.js";
import { SearchResult } from "erela.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class JoinCommand extends BaseCommand {
    constructor() {
        super("join", "music", [], []);
    }

    public async exec(client: client, message: Message, args: any[]): Promise<void> {
        if(!message.member.voice.channel) {
            message.channel.send(new MessageEmbed()
                .setDescription("You need to be in a voice channel to play music !")
                .setColor("#FF0000")
                .setAuthor("You are not in a voice channel !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        
        let player = client.manager.get(message.guild.id);
        if(!player) {
            player = client.manager.create({
                guild: message.guild.id,
                voiceChannel: message.member.voice.channel.id,
                textChannel: message.channel.id,
            });
        }

        if(player.state !== 'CONNECTED') player.connect();
    }
}