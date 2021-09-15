import { Message, MessageEmbed, TextChannel } from "discord.js";
import DiscordClient from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class EmbedCommand extends BaseCommand {
    constructor() {
        super("embed", "informations", [], []);
    }

    public exec(client: DiscordClient, message: Message, args: any[]) {
        return new Promise<void>(async (resolve, reject) => {
            if(!message.member.hasPermission("ADMINISTRATOR")) {
                message.channel.send(new MessageEmbed()
                    .setDescription("You do not have the permission to do this !")
                    .setColor("#FF0000")
                    .setAuthor("You need to be administrator to do this !", client.user.avatarURL())
                    .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
                    resolve();
            }

            const embed = new MessageEmbed();
            embed.setFooter("Annonce par " + message.author.tag, message.author.avatarURL());

            let filter = m => m.author.id === message.author.id
            let reactionFilter = (reaction, user) =>  ['❌'].includes(reaction.emoji.name) && user.id === message.author.id;;
            let res : Message;
            let msg : Message;
            
            msg = await message.channel.send(makeEmbed("Embed Generator : Title (1/6)", "Enter Title", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if(res.content !== "null") embed.setTitle(res.content);
            await res.delete();

            msg = await msg.edit(makeEmbed("Embed Generator : Description (2/6)", "Enter Description", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if(res.content !== "null") embed.setDescription(res.content);
            await res.delete();

            msg = await msg.edit(makeEmbed("Embed Generator : Color (3/6)", "Enter Color", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if(res.content !== "null") embed.setColor(res.content.toUpperCase());
            await res.delete();

            msg = await msg.edit(makeEmbed("Embed Generator : Image (4/6)", "Enter Image URL (optional)", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if(res.content !== "null") embed.setImage(res.content);
            await res.delete();

            msg = await msg.edit(makeEmbed("Embed Generator : Thumbnail (5/6)", "Enter Thumbnail URL (optional)", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if(res.content !== "null") embed.setThumbnail(res.content);
            await res.delete();

            msg = await msg.edit(makeEmbed("Embed Generator : URL (5/6)", "Enter URL (optional)", "BLUE", client.user.avatarURL()));
            await msg.react('❌');
            res = (await message.channel.awaitMessages(filter, { max: 1, time: 30000, errors: ['time'] })).first();
            if(res.content !== "null") embed.setURL(res.content);
            await res.delete();
            await msg.delete();

            let channel = await message.guild.channels.resolve(await client.getConfig().getAnnouncementChannel(message.guild.id)) as TextChannel;
            if(!channel) {
                await message.channel.send(new MessageEmbed().setAuthor("No Announcement channel set !", client.user.avatarURL()).setDescription("You need to set a announcement channel !").setColor("#FF0000").setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL()));
            } else {
                await channel.send(embed);
            }

            resolve();
        });
    }
}

function makeEmbed(title?: string | null, description?: string | null, color?: string | null, authorIcon?: string | null, footerIcon?: string | null) : MessageEmbed {
    return new MessageEmbed().setAuthor(title, authorIcon).setDescription(description).setFooter("React with ❌ to cancel").setColor(color);
}