import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class FortniteCommand extends BaseCommand {
    constructor() {
        super("fortnite", "You are not supposed to use this command...", "fun", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const guild = interaction.guild;
        const target = await guild.members.fetch(interaction.user.id);

        let role = await guild.roles.resolve(await client.getConfig().getMutedRole(guild.id));
        if(!role) { 
            role = await guild.roles.create({
                name: "Muted",
                permissions: []
            })

            await client.getConfig().setMutedRole(guild.id, role.id);

            guild.channels.cache.forEach(ch => {
                if(!ch.isThread()) {
                    ch.permissionOverwrites.edit(role, { SEND_MESSAGES: false });
                }
            })
        }

        target.roles.add(role);
        interaction.reply({
            embeds: [
                new MessageEmbed()
            .setAuthor(target.user.username + " was punished", target.user.avatarURL())
            .setDescription(target.user.tag + " was punished for saying the F word")
            .setFooter("You will be unpunished at ", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            .setTimestamp(Date.now() + 666 * 1000)
            ]
        });
        setTimeout(() => {
            target.roles.remove(role);
        }, 666 * 1000);
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        return builder;
    }
}