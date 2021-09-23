import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageComponentInteraction, MessageEmbed } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class GiveawayCommand extends BaseCommand {
    constructor() {
        super("giveaway", "Create a giveaway", "Miscs", [], ["ADMINISTRATOR"]);
    }

    public async exec(client: DiscordClient, interaction: CommandInteraction): Promise < void > {
        const channel = interaction.channel;

        const msg = (await interaction.reply({
            embeds: [
                await this.makeEmbed(client, interaction, "Giveaway setup (1/6)", "Please enter the channel the giveaway will be in", "", null)
            ],
            fetchReply: true
        })) as Message;

        msg.createMessageComponentCollector({ time: 10 * 1000 }).on('collect', (interaction: MessageComponentInteraction) => {
            channel.send("test");
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }

    private async makeEmbed(client: DiscordClient, interaction: CommandInteraction, title: string, desc: string, footer: string, timestamp : Date) : Promise<MessageEmbed> {
        return new MessageEmbed()
            .setAuthor(title, client.user.avatarURL())
            .setDescription(desc)
            .setFooter(footer)
            .setTimestamp(timestamp)
    }
}