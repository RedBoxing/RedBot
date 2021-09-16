import { Message, MessageEmbed, Interaction, BaseCommandInteraction, CommandInteraction } from "discord.js";
import { checkPermission } from "../../utils/permissionsUtils";
import { reactor } from "../../utils/reactions/reactor";

import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";
import GuildMember from "../../database/models/GuildMember";

export default class InteractionCreateEvent extends BaseEvent {
    constructor() {
        super("interactionCreate");
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        if(!interaction.isCommand()) return;

        if(client.getCommands().has(interaction.commandName)) {
            const command = client.getCommands().get(interaction.commandName);
            command.exec(client, interaction).catch(err => {
                try {
                    interaction.reply({
                        embeds: [
                            new MessageEmbed()
                                .setAuthor("Error !", client.user.avatarURL())
                                .setDescription(err)
                                .setColor("#FF0000")
                        ]
                     });
                } catch(error) {
                    console.log(error);
                }
            })
        }
    }
}