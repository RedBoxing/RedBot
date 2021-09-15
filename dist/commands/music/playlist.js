"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class PlayListCommand extends BaseCommand_1.default {
    constructor() {
        super("playlist", "music", ["pl"], []);
    }
    async exec(client, message, args) {
        const player = client.manager.get(message.guild.id);
        if (player) {
            const embed = new discord_js_1.MessageEmbed();
            embed.setTitle("Playlist of : " + message.guild.name);
            embed.setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());
            let str = "Current: " + player.queue.current.title + "\n\n";
            player.queue.forEach((track, index) => {
                str = str + index + ". " + track.title + "\n";
            });
            embed.setDescription(str);
            message.channel.send(embed);
        }
    }
}
exports.default = PlayListCommand;
