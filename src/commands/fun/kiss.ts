import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";
import { options } from "node-os-utils";

export default class KissCommand extends BaseCommand {
    constructor() {
        super("kiss", "kiss your friends", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const friend = interaction.options.getUser("friend");

        const res = await axios('https://nekos.life/api/kiss');
        const img = res.data.url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(`Owo, ${interaction.user.username} just kissed ${friend.username} !`, client.user.avatarURL())
                    .setImage(img)
                    .setColor('RANDOM')
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addUserOption(option => option.setName("friend").setDescription("The friend you want to kiss").setRequired(true));
        return builder;
    }
}