import { Channel, GuildChannel } from "discord.js";
import DiscordClient from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";

export default class ChannelCreateEvent extends BaseEvent {
    constructor() {
        super("channelCreate");
    }

    public async exec(client: DiscordClient, ch: Channel): Promise<void> {
        const channel = ch as GuildChannel;
        const guild = channel.guild;

        let role = await guild.roles.resolve(await client.getConfig().getMutedRole(guild.id));
        if(!role) { 
            role = await guild.roles.create({
                data: {
                    name: "Muted",
                    permissions: []
                }
            })

            client.getConfig().setMutedRole(guild.id, role.id);
            channel.updateOverwrite(role, { SEND_MESSAGES: false });   
        }
    }
}