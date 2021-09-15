"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const BaseCommand_1 = __importDefault(require("../../structures/base/BaseCommand"));
class StopCommand extends BaseCommand_1.default {
    constructor() {
        super("stop", "music", ["leave"], []);
    }
    async exec(client, message, args) {
        const player = client.manager.get(message.guild.id);
        if (player) {
            player.destroy();
        }
    }
}
exports.default = StopCommand;
