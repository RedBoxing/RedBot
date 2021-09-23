import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed, TextChannel } from "discord.js";
import axios from 'axios'

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class NekoCommand extends BaseCommand {
    constructor() {
        super("lewd", "Random NSFW content", "Fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        if(interaction.channel.isThread()) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                    .setAuthor("Error !")
                    .setColor("RED")
                    .setDescription("This command is not allowed here !")
                    .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });

            return;
        }

        const channel = interaction.channel as TextChannel;
        if(!channel.nsfw) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                    .setAuthor("Error !")
                    .setColor("RED")
                    .setDescription("This command is not allowed here !")
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });

            return;
        }

        const res = await axios('https://nekos.life/api/v2/img/lewd');
        const img = res.data.url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(`Hot !`, client.user.avatarURL())
                    .setImage(img)
                    .setColor('RANDOM')
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}