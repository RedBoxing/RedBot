import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
import { SearchResult } from "erela.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class JoinCommand extends BaseCommand {
    constructor() {
        super("join", "Make the bot join your voice channel", "music", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const member = await interaction.guild.members.fetch(interaction.user.id);

        if(!member.voice.channel) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("You need to be in a voice channel to play music !")
                .setColor("#FF0000")
                .setAuthor("You are not in a voice channel !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
            return;
        }
        
        let player = client.manager.get(interaction.guildId);
        if(!player) {
            player = client.manager.create({
                guild: interaction.guildId,
                voiceChannel: member.voice.channel.id,
                textChannel: interaction.channelId,
            });
        }

        if(player.state !== 'CONNECTED') player.connect();
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}