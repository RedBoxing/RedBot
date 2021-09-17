import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class HugCommand extends BaseCommand {
    constructor() {
        super("hug", "hug your friends", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const friend = interaction.options.getUser("friend");

        const res = await axios('https://nekos.life/api/v2/img/hug');
        const img = res.data.url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(`Owo, ${interaction.user.username} just hugged ${friend.username} !`, client.user.avatarURL())
                    .setImage(img)
                    .setColor('RANDOM')
                    .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addUserOption(option => option.setName("friend").setDescription("The friend you want to hug").setRequired(true));
        return builder;
    }
}