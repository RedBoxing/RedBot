import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";

import { Player, Track } from "erela.js";
import { GuildMember, MessageEmbed, TextChannel, User } from "discord.js";

export default class TrackStartEvent extends BaseEvent {
    constructor() {
        super("trackStart");
    }

    public async exec(client: client, player: Player, track: Track): Promise<void> {
        const channel = client.channels.cache.get(player.textChannel) as TextChannel;
        const author = track.requester as GuildMember;

        channel.send({
            embeds: [
                new MessageEmbed()
            .setTitle(track.title)
            .setColor("#04D3FF")
            .setAuthor("Now playing: ")
            .setFooter(`Added by ${author.user.tag}`, author.user.avatarURL())
            .setThumbnail(track.displayThumbnail())
            .setURL(track.uri)
            ]
        });
    }
}