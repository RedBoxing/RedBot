import { Message, MessageEmbed } from "discord.js";

import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";
import GuildMember from "../../database/models/GuildMember";
import { reactor } from "../../utils/reactions/reactor";

export default class MessageEvent extends BaseEvent {
    constructor() {
        super("messageCreate");
    }

    public async exec(client: client, message: Message): Promise<void> {
        if(message.author.bot || message.channel.type === 'DM') return;
        const content = message.content;

        const countingChannelId = await client.getConfig().getConfig(message.guildId, 'countingChannel');
        if(countingChannelId !== undefined && message.channelId === countingChannelId) {
            const currentCount = await client.getConfig().getConfig(message.guildId, 'counting', 0);

            try {
                const count = parseInt(content);
                if(count === currentCount + 1) {
                    reactor.success(message);
                } else {
                    reactor.failure(message);
                    message.reply({
                        embeds: [
                            new MessageEmbed()
                                .setAuthor(`Wrong Number !`, client.user.avatarURL())
                                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                                .setDescription(`Number \`${count}\` isn't the correct number !`)
                                .setColor('RED')
                        ]
                    })
                }
            } catch(ignored) {}

            return;
        }

        let member : GuildMember = (await GuildMember.findOne({
            where: {
                guildId: message.guild.id,
                userId: message.author.id
            }
        })).get();

        if(!member) {
            member = (await GuildMember.create({
                guildId: message.guild.id,
                userId: message.author.id,
                experience: 0,
                last_experience_increase: 0,
                join_date: Date.now()
            })).get();
        }

        const cooldown = parseInt(process.env.EXPERIENCE_COOLDOWN);
        if(content.length > 4 && (Date.now() > (member.last_experience_increase + cooldown))) {
            member.experience = member.experience + (Math.floor(Math.random() * 5));
            member.last_experience_increase = Date.now();
                
            GuildMember.update({
                experience: member.experience + (Math.floor(Math.random() * 5)),
                last_experience_increase: Date.now()
            }, {
                where: {
                    guildId: message.guild.id,
                    userId: message.author.id
                }
            });
        }
    }
}