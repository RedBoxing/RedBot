import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import { availableOptions } from "../../structures/config/BotConfigurable";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class ConfigCommand extends BaseCommand {
    constructor() {
        super("config", "Configure the bot", "administrations", [], []);
    }

    public async exec(client: DiscordClient, interaction: CommandInteraction): Promise < void > {
        const option = interaction.options.getString("option");
        const value = interaction.options.getString("value");

        const before = client.getConfig().getConfig(interaction.guildId, option);
        client.getConfig().setConfig(interaction.guildId, option, value);

        interaction.reply({
            embeds: [
                new MessageEmbed()
                .setAuthor(`Configured ${option} !`, client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                .setDescription(`Changed option \`${option}\` from \`${before}\` to \`${value}\``)
                .setColor('GREEN')
            ]
        })
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addStringOption(option => {
            option.setName("option");
            option.setDescription("The option to configure");
            option.setRequired(true);

            Object.keys(availableOptions).forEach(key => {
                option.addChoice(availableOptions[key], key);
            });

            return option;
        });

        builder.addStringOption(option => option.setName("value").setDescription("The value to configure").setRequired(true));

        return builder;
    }
}