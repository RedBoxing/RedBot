import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class UnmuteCommand extends BaseCommand {
    constructor() {
        super("unmute", "Unmute a member", "moderation", [], []);
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        const member = await interaction.guild.members.fetch(interaction.user.id);

        if(!member.permissions.has("ADMINISTRATOR")) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("You do not have the permission to do this !")
                .setColor("#FF0000")
                .setAuthor("You need to be administrator to do this !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
            return;
        }

        const target = await interaction.guild.members.fetch(interaction.options.getUser("member").id);
        if(!target) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("User not found ! ")
                .setColor("#FF0000")
                .setAuthor(`User not found !`, client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
            return;
        }
        
        
        let role = await interaction.guild.roles.resolve(await client.getConfig().getMutedRole(interaction.guildId));
        if(!role) { 
            role = await interaction.guild.roles.create({
                name: "Muted",
                permissions: []
            })

            await client.getConfig().setMutedRole(interaction.guild.id, role.id);

            interaction.guild.channels.cache.forEach(ch => {
                if(!ch.isThread()) {
                    ch.permissionOverwrites.edit(role, { SEND_MESSAGES: false });
                }
            })
        }

        target.roles.remove(role);
        interaction.channel.send({
            embeds: [
                new MessageEmbed()
            .setAuthor(target.user.username + " was unmuted", target.user.avatarURL())
            .setDescription(target.user.tag + " was unmuted by " + interaction.user.tag)
            .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            ]
        });
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addMentionableOption(option => option.setName("member").setDescription("The guild member to unmute").setRequired(true));
        return builder;
    }
}