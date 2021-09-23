import { SlashCommandBuilder } from "@discordjs/builders";
import axios from "axios";
import { CommandInteraction, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class FMLCommand extends BaseCommand {
    constructor() {
        super("fml", "Tell you a random FML", "Fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const fml = (await axios("https://blague.xyz/api/vdm/random")).data.vdm.content;

        interaction.reply({
            embeds: [
                new MessageEmbed()
                    .setAuthor("FML", client.user.avatarURL())
                    .setDescription(fml)
                    .setColor('RANDOM')
                    .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}