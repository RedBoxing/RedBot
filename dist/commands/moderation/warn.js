"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const GuildModeration_1 = __importDefault(require("../../database/models/GuildModeration"));
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class WarsCommand extends BaseCommand_1.default {
    constructor() {
        super("warn", "moderation", [], []);
    }
    async exec(client, message, args) {
        if (!message.member.hasPermission("ADMINISTRATOR")) {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("You do not have the permission to do this !")
                .setColor("#FF0000")
                .setAuthor("You need to be administrator to do this !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        if (!args.length) {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("No user specified ! ")
                .setColor("#FF0000")
                .setAuthor("Syntax: `.mute <user>`", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        const target = message.mentions.members.first();
        if (!target) {
            message.channel.send(new discord_js_1.MessageEmbed()
                .setDescription("User not found ! ")
                .setColor("#FF0000")
                .setAuthor(`User \`${args[0]}\` not found !`, client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        args.shift();
        const reason = args.join(' ');
        const date = new Date();
        const mod = await GuildModeration_1.default.create({
            guildId: message.guild.id,
            userId: target.user.id,
            moderatorId: message.author.id,
            sanctionType: 'warn',
            reason: reason,
            expiration: new Date(date.setMonth(date.getMonth() + 1)),
        });
        const channel = await message.guild.channels.resolve(await client.getConfig().getModerationChannel(message.guild.id));
        if (channel) {
            channel.send(new discord_js_1.MessageEmbed()
                .setAuthor("Warn | case #" + mod.id, target.user.avatarURL())
                .addField("User", target.user.tag, true)
                .addField("Moderation", message.author.tag, true)
                .addField("Issued", mod.sanctionDate.toLocaleDateString(), true)
                .addField("Reason", reason, true)
                .setColor('YELLOW')
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
        }
    }
}
exports.default = WarsCommand;
