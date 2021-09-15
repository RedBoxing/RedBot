"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class PlayCommand extends BaseCommand_1.default {
    constructor() {
        super("play", "music", [], []);
    }
    async exec(client, message, args) {
        if (!message.member.voice.channel) {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("You need to be in a voice channel to play music !")
                .setColor("#FF0000")
                .setAuthor("You are not in a voice channel !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        if (!args.length) {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("You need to give me the url or the name of the music you want to play !")
                .setColor("#FF0000")
                .setAuthor("No song given !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        let player = client.manager.get(message.guild.id);
        if (!player) {
            player = client.manager.create({
                guild: message.guild.id,
                voiceChannel: message.member.voice.channel.id,
                textChannel: message.channel.id,
            });
        }
        if (player.state !== 'CONNECTED')
            player.connect();
        const search = args.join(" ");
        let res;
        try {
            res = await client.manager.search(search, message.author);
            if (res.loadType === "LOAD_FAILED") {
                if (!player.queue.current)
                    player.destroy();
                throw res.exception;
            }
        }
        catch (err) {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("Sorry, I was unnable to find that song !")
                .setColor("#FF0000")
                .setAuthor("Music not found !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        switch (res.loadType) {
            case 'NO_MATCHES':
                if (!player.queue.current)
                    player.destroy();
                message.channel.send(new discord_js_1.MessageEmbed()
                    .setDescription("Sorry, I was unnable to find that song !")
                    .setColor("#FF0000")
                    .setAuthor("Music not found !", client.user.avatarURL())
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
                break;
            case 'SEARCH_RESULT':
            case 'TRACK_LOADED':
                player.queue.add(res.tracks[0]);
                if (!player.playing && !player.paused && !player.queue.size)
                    player.play();
                message.channel.send("**" + res.tracks[0].title + "** added to queue !");
                break;
            case 'PLAYLIST_LOADED':
                player.queue.add(res.tracks);
                if (!player.playing && !player.paused && player.queue.totalSize === res.tracks.length)
                    player.play();
                message.channel.send(`Playlist **${res.playlist.name}** with **${res.tracks.length}** tracks added to queue !`);
                break;
        }
    }
}
exports.default = PlayCommand;
