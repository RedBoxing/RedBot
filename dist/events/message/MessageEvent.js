"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const permissionsUtils_1 = require("../../utils/permissionsUtils");
const reactor_1 = require("../../utils/reactions/reactor");
const BaseEvent_1 = __importDefault(require("../../structures/base/BaseEvent"));
const GuildMember_1 = __importDefault(require("../../database/models/GuildMember"));
class MessageEvent extends BaseEvent_1.default {
    constructor() {
        super("message");
    }
    async exec(client, message) {
        if (message.author.bot || message.channel.type === 'dm')
            return;
        const prefix = await client.getConfig().getPrefix(message.guild.id);
        const content = message.content;
        let member = await GuildMember_1.default.findOne({
            where: {
                guildId: message.guild.id,
                userId: message.author.id
            }
        });
        if (!member) {
            member = await GuildMember_1.default.create({
                guildId: message.guild.id,
                userId: message.author.id,
            });
        }
        if (!content.startsWith(prefix)) {
            const cooldown = parseInt(process.env.EXPERIENCE_COOLDOWN);
            if (content.length > 4 && (Date.now() > (member.last_experience_increase + cooldown))) {
                member.experience = member.experience + (Math.floor(Math.random() * 5));
                member.last_experience_increase = Date.now();
                await member.save();
            }
            return;
        }
        const args = message.content.split(' ');
        const cmd = args[0].substring(1);
        args.shift();
        if (client.getCommands().has(cmd)) {
            const command = client.getCommands().get(cmd);
            if (!(0, permissionsUtils_1.checkPermission)(message.member, command.getPermissions())) {
                await message.reply(new discord_js_1.MessageEmbed()
                    .setAuthor("Command Denied !", client.user.avatarURL())
                    .setColor("#FF0000")
                    .setDescription("You do not have the permission to use this command !"));
            }
            else {
                command.exec(client, message, args).then(() => reactor_1.reactor.success(message)).catch(err => {
                    reactor_1.reactor.failure(message);
                    message.reply(new discord_js_1.MessageEmbed()
                        .setAuthor("Error !", client.user.avatarURL())
                        .setDescription(err)
                        .setColor("#FF0000"));
                });
            }
        }
    }
}
exports.default = MessageEvent;
