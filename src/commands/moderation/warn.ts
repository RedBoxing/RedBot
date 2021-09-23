import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, Message, MessageEmbed, TextChannel } from "discord.js";
import client from "../../client/client";
import GuildModeration from "../../database/models/GuildModeration";
import BaseCommand from "../../structures/base/BaseCommand";
import { getChannelMention } from "../../utils/utils";

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
                .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
            return;
        }

        const target = await interaction.guild.members.fetch(interaction.options.getUser("member").id);
        if(!target) {
            interaction.reply({
                embeds: [
                    new MessageEmbed()
                .setDescription("Member not found ! ")
                .setColor("#FF0000")
                .setAuthor(`Member not found !`, client.user.avatarURL())
                .setFooter((await client.getTranslator().getTranslation(interaction.guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                ]
            });
            return;
        }
        
        const reason = interaction.options.getString("reason")
        const date = new Date();
        
        const mod = (await GuildModeration.create({
            guildId: interaction.guildId,
            userId: target.user.id,
            moderatorId: interaction.user.id,
            sanctionType: 'warn',
            reason: reason,
            sanctionDate: date.toUTCString(),
            expiration: new Date(date.setMonth(date.getMonth()+1))
        })).get();

        const channel = await interaction.guild.channels.resolve(getChannelMention(await client.getConfig().getConfig(interaction.guildId, "moderationChannel"))) as TextChannel | null;
        
        if(channel) {
            channel.send({
                embeds: [
                    new MessageEmbed()
                        .setAuthor("Warn | case #" + mod.id, target.user.avatarURL())
                        .addField("Member", target.user.tag, true)
                        .addField("Reason", reason, true)
                        .setColor('YELLOW')
                        .setTimestamp(mod.sanctionDate)
                        .setFooter("Warned by " + interaction.user.tag + " on", interaction.user.avatarURL())
                ]
            });
        }

        interaction.reply({
            embeds: [
                new MessageEmbed()
                .setAuthor("Member Warned !", client.user.avatarURL())
                .setDescription("Member " + member.user.tag + " Warned !")
            ]
        })
    }

    public build(builder: SlashCommandBuilder): SlashCommandBuilder {
        builder.addUserOption(option => option.setName("member").setDescription("The guild member to warn").setRequired(true));
        builder.addStringOption(option => option.setName("reason").setDescription("The reason of the warn").setRequired(false));
        return builder;
    }
}