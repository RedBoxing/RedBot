import { CommandInteraction, MessageEmbed } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

import fs from 'fs'

import { humanFileSize } from "../../utils/utils";
import { SlashCommandBuilder } from "@discordjs/builders";
import { mem, cpu, os } from 'node-os-utils'

const packageJson = JSON.parse(fs.readFileSync('./package.json', 'utf-8'));

export default class EmbedCommand extends BaseCommand {
    constructor() {
        super("infos", "Show informations about the bot", "informations", [], []);
    }

    public async exec(client: DiscordClient, interaction: CommandInteraction): Promise < void > {
        const embed = new MessageEmbed();
        embed.setAuthor("RedBot", client.user.avatarURL());
        embed.setThumbnail(client.user.avatarURL());
        embed.setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());

        embed.addField("Version", "Node.js : `" + process.versions.node + "`\n Discord.js: `" + packageJson["dependencies"]["discord.js"] + "`\n RedBot: `" + packageJson["version"] + "`\n MariaDB: `10.5.9`", true);
        embed.addField("Stats", "Utilisateurs : `" + client.users.cache.size + "`\n Commandes: `" + client.getCommands().size + "`\n Ping: `" + Math.round(client.ws.ping) + "ms`", true);

        const { totalMemMb, usedMemMb } = await mem.info();
        embed.addField("Serveur", "CPU: `" + cpu.model() + "`\n Utilisation de la mémoire: `" + usedMemMb + "/" + totalMemMb + "`\n OS: `" + await os.oos() + "`", false);

        interaction.reply({
            embeds: [embed]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}