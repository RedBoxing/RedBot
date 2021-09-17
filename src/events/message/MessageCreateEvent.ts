import { Message, MessageEmbed } from "discord.js";

import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";
import GuildMember from "../../database/models/GuildMember";
import { reactor } from "../../utils/reactions/reactor";
import { config } from "dotenv";

export default class MessageEvent extends BaseEvent {
    constructor() {
        super("messageCreate");
    }

    public async exec(client: client, message: Message): Promise<void> {
        if(message.author.bot || message.channel.type === 'DM') return;
        const content = message.content;

        const countingChannelId = await client.getConfig().getConfig(message.guildId, 'countingChannel');
        if(countingChannelId !== undefined && message.channelId === countingChannelId) {
            const currentCount : number = parseInt(await client.getConfig().getConfig(message.guildId, 'counting', 0));
            const lastCounter = await client.getConfig().getConfig(message.guildId, 'lastCounter');

            const count : number = parseInt(content);
            if(count === undefined) {
                reactor.failure(message);
                message.reply({
                    embeds: [
                        new MessageEmbed()
                            .setAuthor(`Invalid Number !`, client.user.avatarURL())
                            .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                            .setDescription(`\`${content}\` isn't a valid number !`)
                            .setColor('RED')
                    ]
                })

                return;
            }


            if(lastCounter !== undefined && message.author.id === lastCounter) {
                reactor.failure(message);
                    message.reply({
                        embeds: [
                            new MessageEmbed()
                                .setAuthor(`You can't count do that !`, client.user.avatarURL())
                                .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                                .setDescription(`You can't count 2 time in a row !`)
                                .setColor('RED')
                        ]
                    })
            } else if(count === currentCount + 1) {
                reactor.success(message);
                client.getConfig().setConfig(message.guildId, 'counting', currentCount + 1)
                client.getConfig().setConfig(message.guildId, 'lastCounter', message.author.id);
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