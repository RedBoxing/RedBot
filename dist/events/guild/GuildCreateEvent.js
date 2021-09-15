"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const GuildConfig_1 = __importDefault(require("../../database/models/GuildConfig"));
const BaseEvent_1 = __importDefault(require("../../structures/base/BaseEvent"));
class GuildCreateEvent extends BaseEvent_1.default {
    constructor() {
        super("guildCreate");
    }
    async exec(client, guild) {
        let config = await GuildConfig_1.default.findOne({
            where: {
                guildId: guild.id
            }
        });
        if (!config) {
            config = await GuildConfig_1.default.create({
                guildId: guild.id
            });
        }
    }
}
exports.default = GuildCreateEvent;
