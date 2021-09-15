import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed, TextChannel } from "discord.js";
import client from "../../client/client";
import GuildModeration from "../../database/models/GuildModeration";
import BaseCommand from "../../structures/base/BaseCommand";

export default class WarsCommand extends BaseCommand {
    constructor() {
        super("warn", "Warn a user", "moderation", [], []);
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
        
        const reason = interaction.options.getString("reason")
        const date = new Date();
        
        const mod = await GuildModeration.create({
            guildId: interaction.guildId,
            userId: target.user.id,
            moderatorId: interaction.user.id,
            sanctionType: 'warn',
            reason: reason,
            expiration: new Date(date.setMonth(date.getMonth()+1)),
        });

        const channel = await interaction.guild.channels.resolve(await client.getConfig().getModerationChannel(interaction.guildId)) as TextChannel | null;
        
        if(channel) {
            channel.send({
                embeds: [
                    new MessageEmbed()
                .setAuthor("Warn | case #" + mod.id, target.user.avatarURL())
                .addField("User", target.user.tag, true)
                .addField("Moderation", interaction.user.tag, true)
                .addField("Issued", mod.sanctionDate.toLocaleDateString(), true)
                .addField("Reason", reason, true)
                .setColor('YELLOW')
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
        }

        interaction.reply({
            embeds: [
                new MessageEmbed()
                .setAuthor("User Warned !", client.user.avatarURL())
                .setDescription("User " + member.user + " Warned !")
            ]
        })
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addMentionableOption(option => option.setName("member").setDescription("The guild member to warn").setRequired(true));
        builder.addStringOption(option => option.setName("reason").setDescription("The reason of the warn").setRequired(false));
        return builder;
    }
}