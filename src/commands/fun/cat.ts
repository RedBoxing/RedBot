import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class CatCommand extends BaseCommand {
    constructor() {
        super("cat", "meow", "Fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const res = await axios('https://api.thecatapi.com/v1/images/search', { headers: { 'x-api-key': process.env.CAT_API_KEY }});
        const img = res.data[0].url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor("😺 Meow ! 😺", client.user.avatarURL())
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