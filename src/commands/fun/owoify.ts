import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class OwoifyCommand extends BaseCommand {
    constructor() {
        super("owoify", "OwO-ify a message", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const message = interaction.options.getString("message");

        const res = await axios('https://nekos.life/api/v2/owoify?text=' + message.replace(' ', '%20'));
        const msg = res.data.owo;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(`Your message have been Owo-ified !`, client.user.avatarURL())
                    .setDescription(msg)
                    .setColor('RANDOM')
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addStringOption(option => option.setName("message").setDescription("The message you want to Owo-ify").setRequired(true));
        return builder;
    }
}