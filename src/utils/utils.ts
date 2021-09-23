import { MessageEmbed } from "discord.js";
import DiscordClient from "../client/client";

export function humanFileSize(bytes, si = true, dp = 1) {
    const thresh = si ? 1000 : 1024;

    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }

    const units = si ?
        ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'] :
        ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    const r = 10 ** dp;

    do {
        bytes /= thresh;
        ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);


    return bytes.toFixed(dp) + ' ' + units[u];
}

export async function makeEmbed(client: DiscordClient, guildId: string, title: string, desc: string, thumbnail: boolean) : Promise<MessageEmbed> {
    const embed = new MessageEmbed()
    .setAuthor(title, client.user.avatarURL())
    .setDescription(desc)
    .setFooter((await client.getTranslator().getTranslation(guildId, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL());

    if(thumbnail) {
        embed.setThumbnail(client.user.avatarURL());
    }

    return embed;
}

export function getUserMention(mention: string) : string {
    const matches = mention.match(/^<@!?(\d+)>$/);
	if (!matches) return;

	return matches[1];
}

export function getChannelMention(mention: string) : string {
    const matches = mention.match(/^<#!?(\d+)>$/);
	if (!matches) return;

	return matches[1];
}