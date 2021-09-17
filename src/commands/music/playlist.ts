import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class PlayListCommand extends BaseCommand {
    constructor() {
        super("playlist", "Add a music to the playlist", "music", ["pl"], []);
    }

    public async exec(client: DiscordClient, interaction: CommandInteraction): Promise<void> {
        const player = client.manager.get(interaction.guild.id);
        if(player) {
            const embed = new MessageEmbed();
            embed.setTitle("Playlist of : " + interaction.guild.name);
            embed.setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());

            let str = "Current: " + player.queue.current.title + "\n\n";
            player.queue.forEach((track, index) =>  {
                str = str + index + ". " + track.title + "\n";
            });

            embed.setDescription(str);
            interaction.reply({
                embeds: [embed]
            });
        }
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}