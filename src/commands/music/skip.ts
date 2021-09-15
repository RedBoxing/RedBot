import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class SkipCommand extends BaseCommand {
    constructor() {
        super("skip", "Skip the current music in the playlist", "music", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const player = client.manager.get(interaction.guild.id);
        if(player) {
            if (!player.queue.current) {
                interaction.reply({
                    embeds: [
                        new MessageEmbed()
                    .setDescription("The bot is not playing music !")
                    .setColor("#FF0000")
                    .setAuthor("The bot is not playing music !", client.user.avatarURL())
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                    ]
                });
                return;
            }

            player.stop();
        } else {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("The bot is not playing music !")
                .setColor("#FF0000")
                .setAuthor("The bot is not playing music !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
        }
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}