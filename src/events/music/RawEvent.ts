import client from "../../client/client";
import BaseEvent from "../../structures/base/BaseEvent";

import { VoicePacket } from "erela.js";

export default class RawEvent extends BaseEvent {
    constructor() {
        super("raw");
    }

    public async exec(client: client, d: VoicePacket): Promise<void> {
        client.manager.updateVoiceState(d);
    }
}