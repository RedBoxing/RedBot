import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
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
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}