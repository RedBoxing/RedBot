import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
import { SearchResult } from "erela.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class PlayCommand extends BaseCommand {
    constructor() {
        super("play", "Play a music", "music", [], []);
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
                textChannel: interaction.channelId
            });
        }

        if(player.state !== 'CONNECTED') player.connect();

        const search = interaction.options.getString("music");
        let res : SearchResult;

        try {
            res = await client.manager.search(search, member);
            if (res.loadType === "LOAD_FAILED") {
                if (!player.queue.current) player.destroy();
                throw res.exception;
            }
        } catch (err) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("Sorry, I was unnable to find that song !")
                .setColor("#FF0000")
                .setAuthor("Music not found !", client.user.avatarURL())
                .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
            return;
        }
      
        switch(res.loadType) {
            case 'NO_MATCHES':
                if (!player.queue.current) player.destroy();
                interaction.reply({
                    embeds: [
                        new MessageEmbed()
                    .setDescription("Sorry, I was unnable to find that song !")
                    .setColor("#FF0000")
                    .setAuthor("Music not found !", client.user.avatarURL())
                    .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                    ]
                });
                break;
            case 'SEARCH_RESULT':
            case 'TRACK_LOADED':
                player.queue.add(res.tracks[0]);
                if (!player.playing && !player.paused && !player.queue.size) player.play();
                interaction.reply("**" + res.tracks[0].title + "** added to queue !");
                break;
            case 'PLAYLIST_LOADED':
                player.queue.add(res.tracks);
                if (!player.playing && !player.paused && player.queue.totalSize === res.tracks.length) player.play();
                interaction.reply(`Playlist **${res.playlist.name}** with **${res.tracks.length}** tracks added to queue !`);
                break;
        }
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addStringOption(option => option.setName("music").setDescription("URL or Name of a youtube video").setRequired(true));
        return builder;
    }
}