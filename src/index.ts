import { config } from 'dotenv'
config();

import { registerCommands, registerEvents } from './utils/registry'

import DiscordClient from './client/client'
import TrackStartEvent from './events/music/TrackStartEvent';
import TrackEndEvent from './events/music/TrackEndEvent';
import RawEvent from './events/music/RawEvent';

import * as logger from './utils/logger'

const client = new DiscordClient();

(async () => {
    await registerCommands(client, '../commands');
    await registerEvents(client, '../events');

    await client.addMusicManagerEvent(new TrackStartEvent());
    await client.addMusicManagerEvent(new TrackEndEvent());
    await client.addMusicManagerEvent(new RawEvent());

    await client.login("Mzg0MjYwNzI4Nzg3NDM1NTMw.Whp8sA.K8N0tOprY8PWFv8EhDd5C11wJak");
})();

process.on('SIGINT', () => {
    logger.info("Gracefully shutting down from SIGINT (Ctrl-C)" );
    client.destroy();
    process.exit(0);
});