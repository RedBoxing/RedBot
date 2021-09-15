import { Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class StopCommand extends BaseCommand {
    constructor() {
        super("stop", "music", ["leave"], []);
    }

    public async exec(client: client, message: Message, args: any[]): Promise<void> {
        const player = client.manager.get(message.guild.id);
        if(player) {
            player.destroy();
        }
    }
}