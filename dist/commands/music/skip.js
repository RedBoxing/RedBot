"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class SkipCommand extends BaseCommand_1.default {
    constructor() {
        super("skip", "music", [], []);
    }
    async exec(client, message, args) {
        const player = client.manager.get(message.guild.id);
        if (player) {
            if (!player.queue.current) {
                message.channel.send(new discord_js_1.MessageEmbed()
                    .setDescription("The bot is not playing music !")
                    .setColor("#FF0000")
                    .setAuthor("The bot is not playing music !", client.user.avatarURL())
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
                return;
            }
            player.stop();
        }
        else {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("The bot is not playing music !")
                .setColor("#FF0000")
                .setAuthor("The bot is not playing music !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
        }
    }
}
exports.default = SkipCommand;
