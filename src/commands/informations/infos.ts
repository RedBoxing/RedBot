import { Message, MessageEmbed } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

import os from 'os';
import v8 from 'v8'

import { humanFileSize } from "../../utils/utils";

export default class EmbedCommand extends BaseCommand {
    constructor() {
        super("infos", "informations", [], []);
    }

    public async exec(client: DiscordClient, message: Message, args: any[]) : Promise<void> {
       const embed = new MessageEmbed();
       embed.setAuthor("RedBot", client.user.avatarURL());
       embed.setThumbnail(client.user.avatarURL());
       embed.setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());

       embed.addField("Version", "Node.js : `" + process.versions.node + "`\n Discord.js: `13.1.0`\n RedBot: `1.0.1`", true);
       embed.addField("Stats", "Utilisateurs : `" + client.users.cache.size + "`\n Commandes: `3`", true);
       
       console.log(v8.getHeapStatistics())
       const total = process.memoryUsage().heapTotal;
       const used = process.memoryUsage().heapUsed;
       embed.addField("Serveur", "CPU: `" + os.cpus()[0].model + "`\n Utilisation de la mémoire: `" + humanFileSize(used) + "/" + humanFileSize(total) + "`\n OS: `" + os.platform() + "`", false);

       message.channel.send(embed);
    }
}