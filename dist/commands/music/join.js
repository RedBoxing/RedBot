"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class JoinCommand extends BaseCommand_1.default {
    constructor() {
        super("join", "music", [], []);
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
    }
}
exports.default = JoinCommand;
