"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class FortniteCommand extends BaseCommand_1.default {
    constructor() {
        super("fortnite", "fun", [], []);
    }
    async exec(client, message, args) {
        const target = message.member;
        let role = await message.guild.roles.resolve(await client.getConfig().getMutedRole(message.guild.id));
        if (!role) {
            role = await message.guild.roles.create({
                data: {
                    name: "Muted",
                    permissions: []
                }
            });
            await client.getConfig().setMutedRole(message.guild.id, role.id);
            message.guild.channels.cache.forEach(ch => {
                ch.updateOverwrite(role, { SEND_MESSAGES: false });
            });
        }
        target.roles.add(role);
        message.channel.send(new discord_js_1.MessageEmbed()
            .setAuthor(target.user.username + " was punished", target.user.avatarURL())
            .setDescription(target.user.tag + " was punished for saying the F word")
            .setFooter("You will be unpunished at ", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            .setTimestamp(Date.now() + 666 * 1000));
        setTimeout(() => {
            target.roles.remove(role);
        }, 666 * 1000);
    }
}
exports.default = FortniteCommand;
