import { Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class UnmuteCommand extends BaseCommand {
    constructor() {
        super("unmute", "moderation", [], []);
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
        
        
        let role = await message.guild.roles.resolve(await client.getConfig().getMutedRole(message.guild.id));
        if(!role) { 
            role = await message.guild.roles.create({
                data: {
                    name: "Muted",
                    permissions: []
                }
            })

            await client.getConfig().setMutedRole(message.guild.id, role.id);

            message.guild.channels.cache.forEach(ch => {
                ch.updateOverwrite(role, { SEND_MESSAGES: false });
            })
        }

        target.roles.remove(role);
        message.channel.send(new MessageEmbed()
            .setAuthor(target.user.username + " was unmuted", target.user.avatarURL())
            .setDescription(target.user.tag + " was unmuted by " + message.author.tag)
            .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
    }
}