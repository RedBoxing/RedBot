import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction } from "discord.js";
import { makeEmbed } from "../../utils/utils"

import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class HelpCommand extends BaseCommand {
    constructor() {
        super("help", "Show the help command", "Miscs", [], []);
    }

    public async exec(client: DiscordClient, interaction: CommandInteraction): Promise < void > {
        const option = interaction.options.getString("command", false);
        if(option !== undefined && client.getCommands().has(option)) {
            const cmd = client.getCommands().get(option);
            const embed = await makeEmbed(client, interaction.guildId, "Help: " + option, "", true);

            embed.addField("Name:", option, true);
            embed.addField("Description:", cmd.getDescription(), true);
            embed.addField("Category:", cmd.getCategory());

            interaction.reply({
                embeds: [embed]
            });
        } else {
            const embed = await makeEmbed(client, interaction.guildId, "RedBot: Help", "", true);
            const cmds = {};
            
            client.getCommands().forEach((cmd, name, map) => {
                if(!cmds[cmd.getCategory()]) {
                    cmds[cmd.getCategory()] = [];
                }

                cmds[cmd.getCategory()].push(name);
            })

            for(const [category, commands] of Object.entries(cmds)) {
                //@ts-expect-error
                embed.addField(category, commands.map(cmd => "`" + cmd + "`").join(", "))
            }

            interaction.reply({
                embeds: [ embed ]
            });
        }
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addStringOption(option => option.setName("command").setDescription("The command you want to get help on").setRequired(false));

        return builder;
    }
}