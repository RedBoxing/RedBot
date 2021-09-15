import { Message, MessageEmbed } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

import os from 'os';

export default class EmbedCommand extends BaseCommand {
    constructor() {
        super("infos", "informations", [], []);
    }

    public async exec(client: DiscordClient, message: Message, args: any[]) {
        console.log("a");
       const embed = new MessageEmbed();
       embed.setAuthor("RedBot", client.user.avatar);
       embed.setThumbnail(client.user.avatar);
       embed.setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());

       embed.addField("Version", "Node.js : `16.6.1`\n Discord.js: 13.1.0\n RedBot: 1.0.1", true);
       embed.addField("Stats", "Utilisateurs : `" + client.users.cache.size + "`\n Commandes: `3`", true);
       
       const total = os.totalmem();
       const free = os.freemem();
       embed.addField("Serveur", "CPU: `" + os.cpus()[0].model + "`\n Utilisation de la mémoire: `" + (total - free) + "/" + total + "`\n OS: `" + os.arch() + "`", false);

       message.channel.send(embed);
    }
}