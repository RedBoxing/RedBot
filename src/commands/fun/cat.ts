import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";

import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class CatCommand extends BaseCommand {
    constructor() {
        super("cat", "meow", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const res = await fetch('https://api.thecatapi.com/v1/images/search', { headers: { 'x-api-key': process.env.CAT_API_KEY }});
        const img = (await res.json())[0].url;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor(":cat: Meow ! :cat:", client.user.avatarURL())
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