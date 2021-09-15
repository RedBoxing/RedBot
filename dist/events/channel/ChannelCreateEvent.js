"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const BaseEvent_1 = __importDefault(require("../../structures/base/BaseEvent"));
class ChannelCreateEvent extends BaseEvent_1.default {
    constructor() {
        super("channelCreate");
    }
    async exec(client, ch) {
        const channel = ch;
        const guild = channel.guild;
        let role = await guild.roles.resolve(await client.getConfig().getMutedRole(guild.id));
        if (!role) {
            role = await guild.roles.create({
                data: {
                    name: "Muted",
                    permissions: []
                }
            });
            client.getConfig().setMutedRole(guild.id, role.id);
            channel.updateOverwrite(role, { SEND_MESSAGES: false });
        }
    }
}
exports.default = ChannelCreateEvent;
