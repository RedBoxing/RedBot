"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const discord_js_1 = require("discord.js");
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
const os_1 = __importDefault(require("os"));
class EmbedCommand extends BaseCommand_1.default {
    constructor() {
        super("infos", "informations", [], []);
    }
    async exec(client, message, args) {
        const embed = new discord_js_1.MessageEmbed();
        embed.setAuthor("RedBot", client.user.avatar);
        embed.setThumbnail(client.user.avatar);
        embed.setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());
        embed.addField("Version", "Node.js : `16.6.1`\n Discord.js: 13.1.0\n RedBot: 1.0.1", true);
        embed.addField("Stats", "Utilisateurs : `" + client.users.cache.size + "`\n Commandes: `3`", true);
        const total = os_1.default.totalmem();
        const free = os_1.default.freemem();
        embed.addField("Serveur", "CPU: `" + os_1.default.cpus()[0].model + "`\n Utilisation de la mémoire: `" + (total - free) + "/" + total + "`\n OS: `" + os_1.default.arch() + "`", false);
        message.channel.send(embed);
    }
}
exports.default = EmbedCommand;
