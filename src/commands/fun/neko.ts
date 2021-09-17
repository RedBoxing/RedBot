import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class NekoCommand extends BaseCommand {
    constructor() {
        super("neko", "Neko-Chan <3", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const res = await axios('https://nekos.life/api/v2/img/neko');
        const img = res.data.url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(`Neko-Chan <3 !`, client.user.avatarURL())
                    .setImage(img)
                    .setColor('RANDOM')
                    .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}