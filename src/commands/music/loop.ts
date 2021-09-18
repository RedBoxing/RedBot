import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class LoopCommand extends BaseCommand {
    constructor() {
        super("loop", "Loop the current music in the playlist", "music", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const player = client.manager.get(interaction.guildId);
        if(player) {
            if (!player.queue.current) {
                interaction.reply({
                    embeds: [
                        new MessageEmbed()
                    .setDescription("The bot is not playing music !")
                    .setColor("#FF0000")
                    .setAuthor("The bot is not playing music !", client.user.avatarURL())
                    .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                    ]
                });
                return;
            }

            if (player.queueRepeat) {
                player.setQueueRepeat(false);
                interaction.reply({
                    embeds: [
                        new MessageEmbed()
                            .setDescription("Player is no longer on repeat.")
                            .setColor('RANDOM')
                            .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                    ]
                })
              } else {
                player.setQueueRepeat(true);
                interaction.reply({
                    embeds: [
                        new MessageEmbed()
                            .setDescription("Player is now on repeat.")
                            .setColor('RANDOM')
                            .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                    ]
                })
              }
        } else {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("The bot is not playing music !")
                .setColor("#FF0000")
                .setAuthor("The bot is not playing music !", client.user.avatarURL())
                .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
        }
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}