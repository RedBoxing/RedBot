"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const BaseEvent_1 = __importDefault(require("../../structures/base/BaseEvent"));
const discord_js_1 = require("discord.js");
class TrackStartEvent extends BaseEvent_1.default {
    constructor() {
        super("trackStart");
    }
    async exec(client, player, track) {
        const channel = client.channels.cache.get(player.textChannel);
        const author = track.requester;
        channel.send(new discord_js_1.MessageEmbed()
            .setTitle(track.title)
            .setColor("#04D3FF")
            .setAuthor("Now playing: ")
            .setFooter(`Added by ${author.tag}`, author.avatarURL())
            .setThumbnail(track.displayThumbnail())
            .setURL(track.uri));
    }
}
exports.default = TrackStartEvent;
