import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";

import { Player, Track } from "erela.js";

export default class TrackEndEvent extends BaseEvent {
    constructor() {
        super("trackEnd");
    }

    public async exec(client: client, player: Player, track: Track): Promise<void> {
        player.destroy();
    }
}