import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageComponentInteraction, MessageEmbed, TextChannel } from "discord.js";
import DiscordClient from "../../client/client";
import GuildGiveaways from "../../database/models/GuildGiveaways";
import BaseCommand from "../../structures/base/BaseCommand";
import { getChannelMention } from "../../utils/utils";

export default class GiveawayCommand extends BaseCommand {
    constructor() {
        super("giveaway", "Create a giveaway", "Miscs", [], ["ADMINISTRATOR"]);
    }

    public async exec(client: DiscordClient, interaction: CommandInteraction): Promise < void > {
        const channel = interaction.channel;

        await interaction.reply({
            embeds: [
                await this.makeEmbed(client, interaction, "Giveaway setup (1/4)", "Please enter the channel the giveaway will be in", "", null)
            ]
        });

        let collected = await (await channel.awaitMessages({ time: 15 * 1000, max: 1, filter: m => m.author.id === interaction.user.id })).first().content;
        if(collected === "cancel") {
            return;
        }

        const ch = await interaction.guild.channels.fetch(getChannelMention(collected)) as TextChannel;
        if(!ch) {
            channel.send({
                embeds: [
                    await (await this.makeEmbed(client, interaction, "Giveaway setup : Error", "`" + collected + "` is not a valid channel !", "", null)).setColor('RED')
                ]
            })
        }

        if(!ch.isText()) {
            channel.send({
                embeds: [
                    await (await this.makeEmbed(client, interaction, "Giveaway setup : Error", "channel must be a text channel !", "", null)).setColor('RED')
                ]
            })
        }

        channel.send({
            embeds: [
                await this.makeEmbed(client, interaction, "Giveaway setup (2/4)", "Please enter the duration of the giveaway", "", null)
            ],
        });

        collected = await (await channel.awaitMessages({ time: 15 * 1000, max: 1, filter: m => m.author.id === interaction.user.id })).first().content;
        if(collected === "cancel") {
            return;
        }

        const duration = collected;

        channel.send({
            embeds: [
                await this.makeEmbed(client, interaction, "Giveaway setup (3/4)", "Please enter the number of winners of the giveaway", "", null)
            ],
        });

        collected = await (await channel.awaitMessages({ time: 15 * 1000, max: 1, filter: m => m.author.id === interaction.user.id })).first().content;
        if(collected === "cancel") {
            return;
        }

        const winners = parseInt(collected);

        channel.send({
            embeds: [
                await this.makeEmbed(client, interaction, "Giveaway setup (4/4)", "Please enter the prize of the giveaway", "", null)
            ],
        });

        collected = await (await channel.awaitMessages({ time: 15 * 1000, max: 1, filter: m => m.author.id === interaction.user.id })).first().content;
        if(collected === "cancel") {
            return;
        }

        const prize = collected;

        const msg = (await ch.send({
            embeds: [
                await this.makeEmbed(client, interaction, prize, "Hosted by " + interaction.user, "End at ", new Date())
            ]
        }));

        msg.react(":tata:")

        GuildGiveaways.create({
            guildId: interaction.guildId,
            messageId: msg.id,
            channelId: ch.id,
            hostId: interaction.user.id,
            winners,
            prize,
            end: new Date()
        })
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