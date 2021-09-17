import { MessageEmbed, CommandInteraction } from "discord.js";
import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";

export default class InteractionCreateEvent extends BaseEvent {
    constructor() {
        super("interactionCreate");
    }

    public async exec(client: client, interaction: CommandInteraction): Promise<void> {
        if(!interaction.isCommand()) return;

        if(client.getCommands().has(interaction.commandName)) {
            const command = client.getCommands().get(interaction.commandName);

            try {
                command.exec(client, interaction);
            } catch(err) {
                interaction.reply({
                    embeds: [
                        new MessageEmbed()
                            .setAuthor("Error !", client.user.avatarURL())
                            .setDescription(err)
                            .setColor("RED")
                            .setFooter("RedBot by RedBoxing", (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
                    ]
                });
            }
        }
    }
}