import { Message, MessageEmbed } from "discord.js";
import client from "../../client/client";
import BaseCommand from "../../structures/base/BaseCommand";

export default class FortniteCommand extends BaseCommand {
    constructor() {
        super("fortnite", "fun", [], []);
    }

    public async exec(client: client, message: Message, args: any[]): Promise<void> {
        const target = message.member;
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

        target.roles.add(role);
        message.channel.send(new MessageEmbed()
            .setAuthor(target.user.username + " was punished", target.user.avatarURL())
            .setDescription(target.user.tag + " was punished for saying the F word")
            .setFooter("You will be unpunished at ", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
            .setTimestamp(Date.now() + 666 * 1000));
        setTimeout(() => {
            target.roles.remove(role);
        }, 666 * 1000);
    }
}