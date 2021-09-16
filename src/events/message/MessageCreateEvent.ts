import { Message, MessageEmbed } from "discord.js";

import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";
import GuildMember from "../../database/models/GuildMember";

export default class MessageEvent extends BaseEvent {
    constructor() {
        super("messageCreate");
    }

    public async exec(client: client, message: Message): Promise<void> {
        if(message.author.bot || message.channel.type === 'DM') return;
        const prefix = await client.getConfig().getPrefix(message.guild.id);
        const content = message.content;

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

        if(!content.startsWith(prefix)) {
            const cooldown = parseInt(process.env.EXPERIENCE_COOLDOWN);
            if(content.length > 4 && (Date.now() > (member.last_experience_increase + cooldown))) {
                member.experience = member.experience + (Math.floor(Math.random() * 5));
                member.last_experience_increase = Date.now();
                member.save();
            }
        }
    }
}