"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class EmbedCommand extends BaseCommand_1.default {
    constructor() {
        super("embed", "informations", [], []);
    }
    exec(client, message, args) {
        return new Promise(async (resolve, reject) => {
            if (!message.member.hasPermission("ADMINISTRATOR")) {
                message.channel.send(new discord_js_1.MessageEmbed()
                    .setDescription("You do not have the permission to do this !")
                    .setColor("#FF0000")
                    .setAuthor("You need to be administrator to do this !", client.user.avatarURL())
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
                resolve();
            }
            const embed = new discord_js_1.MessageEmbed();
            embed.setFooter("Annonce par " + message.author.tag, message.author.avatarURL());
            let filter = m => m.author.id === message.author.id;
            let reactionFilter = (reaction, user) => ['❌'].includes(reaction.emoji.name) && user.id === message.author.id;
            ;
            let res;
            let msg;
            msg = await message.channel.send(makeEmbed("Embed Generator : Title (1/6)", "Enter Title", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if (res.content !== "null")
                embed.setTitle(res.content);
            await res.delete();
            msg = await msg.edit(makeEmbed("Embed Generator : Description (2/6)", "Enter Description", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if (res.content !== "null")
                embed.setDescription(res.content);
            await res.delete();
            msg = await msg.edit(makeEmbed("Embed Generator : Color (3/6)", "Enter Color", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if (res.content !== "null")
                embed.setColor(res.content.toUpperCase());
            await res.delete();
            msg = await msg.edit(makeEmbed("Embed Generator : Image (4/6)", "Enter Image URL (optional)", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if (res.content !== "null")
                embed.setImage(res.content);
            await res.delete();
            msg = await msg.edit(makeEmbed("Embed Generator : Thumbnail (5/6)", "Enter Thumbnail URL (optional)", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if (res.content !== "null")
                embed.setThumbnail(res.content);
            await res.delete();
            msg = await msg.edit(makeEmbed("Embed Generator : URL (5/6)", "Enter URL (optional)", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if (res.content !== "null")
                embed.setURL(res.content);
            await res.delete();
            await msg.delete();
            let channel = await message.guild.channels.resolve(await client.getConfig().getAnnouncementChannel(message.guild.id));
            if (!channel) {
                await message.channel.send(new discord_js_1.MessageEmbed().setAuthor("No Announcement channel set !", client.user.avatarURL()).setDescription("You need to set a announcement channel !").setColor("#FF0000").setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            }
            else {
                await channel.send(embed);
            }
            resolve();
        });
    }
}
exports.default = EmbedCommand;
function makeEmbed(title, description, color, authorIcon, footerIcon) {
    return new discord_js_1.MessageEmbed().setAuthor(title, authorIcon).setDescription(description).setFooter("React with ❌ to cancel").setColor(color);
}
