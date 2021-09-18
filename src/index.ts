import { config } from 'dotenv'
config();

import { registerCommands, registerEvents } from './utils/registry'
import { Intents, MessageEmbed, TextChannel } from 'discord.js';

import DiscordClient from './client/client'
import TrackStartEvent from './events/music/TrackStartEvent';
import RawEvent from './events/music/RawEvent';
import logger from './utils/logger'

const client = new DiscordClient({
    intents: [ Intents.FLAGS.GUILDS, Intents.FLAGS.GUILD_MESSAGES, Intents.FLAGS.GUILD_VOICE_STATES ]
});

(async () => {
    await registerCommands(client, '../commands');
    await registerEvents(client, '../events');

    await client.addMusicManagerEvent(new TrackStartEvent());
    await client.addMusicManagerEvent(new RawEvent());

    await client.login("Mzg0MjYwNzI4Nzg3NDM1NTMw.Whp8sA.K8N0tOprY8PWFv8EhDd5C11wJak");
})();

process.on('SIGINT', () => {
    logger.info("Gracefully shutting down from SIGINT (Ctrl-C)" );
    client.destroy();
    process.exit(0);
});

process.on('uncaughtException', async error => {
    const guild = await client.guilds.fetch(process.env.BOT_GUILD);
    const channel = await guild.channels.fetch(process.env.BOT_ERROR_CHANNEL) as TextChannel;

    channel.send({
        embeds: [
            new MessageEmbed()
                .setAuthor("Error : " + error.message, client.user.avatarURL())
                .setDescription("```" + error.stack + "```")
                .setColor('RED')
                .setFooter((await client.getTranslator().getTranslation(guild.id, 'REDBOT_BY')), (await client.users.fetch(process.env.AUTHOR_ID)).avatarURL())
        ]
    })
})