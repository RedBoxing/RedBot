import { Message, MessageEmbed, TextChannel } from "discord.js";
import client from "../../client/client";
import GuildModeration from "../../database/models/GuildModeration";
import BaseCommand from "../../structures/base/BaseCommand";

export default class WarsCommand extends BaseCommand {
    constructor() {
        super("warn", "moderation", [], []);
    }

    public async exec(client: client, message: Message, args: any[]): Promise<void> {
        if(!message.member.hasPermission("ADMINISTRATOR")) {
            message.channel.send(new MessageEmbed()
                .setDescription("You do not have the permission to do this !")
                .setColor("#FF0000")
                .setAuthor("You need to be administrator to do this !", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }

        if(!args.length) {
            message.channel.send(new MessageEmbed()
                .setDescription("No user specified ! ")
                .setColor("#FF0000")
                .setAuthor("Syntax: `.mute <user>`", client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }

        const target = message.mentions.members.first();
        if(!target) {
            message.channel.send(new MessageEmbed()
                .setDescription("User not found ! ")
                .setColor("#FF0000")
                .setAuthor(`User \`${args[0]}\` not found !`, client.user.avatarURL())
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            return;
        }
        
        args.shift();
        const reason = args.join(' ');
        const date = new Date();
        
        const mod = await GuildModeration.create({
            guildId: message.guild.id,
            userId: target.user.id,
            moderatorId: message.author.id,
            sanctionType: 'warn',
            reason: reason,
            expiration: new Date(date.setMonth(date.getMonth()+1)),
        });

        const channel = await message.guild.channels.resolve(await client.getConfig().getModerationChannel(message.guild.id)) as TextChannel | null;
        
        if(channel) {
            channel.send(new MessageEmbed()
                .setAuthor("Warn | case #" + mod.id, target.user.avatarURL())
                .addField("User", target.user.tag, true)
                .addField("Moderation", message.author.tag, true)
                .addField("Issued", mod.sanctionDate.toLocaleDateString(), true)
                .addField("Reason", reason, true)
                .setColor('YELLOW')
                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
        }
    }
}