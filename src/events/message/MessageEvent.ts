import { Message, MessageEmbed } from "discord.js";
import { checkPermission } from "../../utils/permissionsUtils";
import { reactor } from "../../utils/reactions/reactor";

import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";
import GuildMember from "../../database/models/GuildMember";

export default class MessageEvent extends BaseEvent {
    constructor() {
        super("message");
    }

    public async exec(client: client, message: Message): Promise<void> {
        if(message.author.bot || message.channel.type === 'dm') return;
        const prefix = await client.getConfig().getPrefix(message.guild.id);
        const content = message.content;

        let member = await GuildMember.findOne({
            where: {
                guildId: message.guild.id,
                userId: message.author.id
            }
        });

        if(!member) {
            member = await GuildMember.create({
                guildId: message.guild.id,
                userId: message.author.id,
                experience: 0,
                last_experience_increase: 0,
                join_date: Date.now()
            });
        }

        if(!content.startsWith(prefix)) {
            const cooldown = parseInt(process.env.EXPERIENCE_COOLDOWN);
            if(content.length > 4 && (Date.now() > (member.last_experience_increase + cooldown))) {
                member.experience = member.experience + (Math.floor(Math.random() * 5));
                member.last_experience_increase = Date.now();
                await member.save();
            }

            return;
        }

        const args = message.content.split(' ');
        const cmd = args[0].substring(1);
        args.shift();

        if(client.getCommands().has(cmd)) {
            const command = client.getCommands().get(cmd);

            if(!checkPermission(message.member, command.getPermissions())) {
                await message.reply(new MessageEmbed()
                .setAuthor("Command Denied !", client.user.avatarURL())
                .setColor("#FF0000")
                .setDescription("You do not have the permission to use this command !"));
            } else {
                command.exec(client, message, args).then(() => reactor.success(message)).catch(err => {
                    reactor.failure(message);
                    message.reply(new MessageEmbed()
                    .setAuthor("Error !", client.user.avatarURL())
                    .setDescription(err)
                    .setColor("#FF0000"));
                })
            }
        }
    }
}