import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class SlapCommand extends BaseCommand {
    constructor() {
        super("slap", "Slap your friends", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const friend = interaction.options.getUser("friend");

        const res = await axios('https://nekos.life/api/v2/img/slap');
        const img = res.data.url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(`Ouch ! ${interaction.user.username} just slapped ${friend.username} !`, client.user.avatarURL())
                    .setImage(img)
                    .setColor('RANDOM')
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addUserOption(option => option.setName("friend").setDescription("The friend you want to slap").setRequired(true));
        return builder;
    }
}