"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const BaseEvent_1 = __importDefault(require("../../structures/base/BaseEvent"));
class TrackEndEvent extends BaseEvent_1.default {
    constructor() {
        super("trackEnd");
    }
    async exec(client, player, track) {
        player.destroy();
    }
}
exports.default = TrackEndEvent;
