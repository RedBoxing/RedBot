import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class StopCommand extends BaseCommand {
    constructor() {
        super("stop", "Stop the music", "music", ["leave"], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const player = client.manager.get(interaction.guild.id);
        if(player) {
            player.destroy();
        }

        interaction.reply({
            embeds: [
                new MessageEmbed()
                .setAuthor("Stopped", client.user.avatarURL())
                .setDescription("Stopped music !")
                .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                .setColor('RANDOM')
            ]
        })
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}