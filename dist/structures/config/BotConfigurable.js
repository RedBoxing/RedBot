"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    Object.defineProperty(o, k2, { enumerable: true, get: function() { return m[k]; } });
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const GuildConfig_1 = __importDefault(require("../../database/models/GuildConfig"));
const logger = __importStar(require("../../utils/logger"));
class BotConfigurable {
    async setPrefix(guildId, value) {
        await this.setConfig(guildId, 'prefix', value);
    }
    async setMutedRole(guildId, value) {
        await this.setConfig(guildId, 'mutedRole', value);
    }
    async setAnnouncementChannel(guildId, value) {
        await this.setConfig(guildId, 'announcementChannel', value);
    }
    async setCommandReaction(guildId, value) {
        await this.setConfig(guildId, 'commandReaction', value);
    }
    async setModerationChannel(guildId, value) {
        await this.setConfig(guildId, 'moderationChannel', value);
    }
    async getPrefix(guildId) {
        return await this.getConfig(guildId, 'prefix');
    }
    async getMutedRole(guildId) {
        return await this.getConfig(guildId, 'mutedRole');
    }
    async getAnnouncementChannel(guildId) {
        return await this.getConfig(guildId, 'announcementChannel');
    }
    async getCommandReaction(guildId) {
        return await this.getConfig(guildId, 'commandReaction');
    }
    async getModerationChannel(guildId) {
        return await this.getConfig(guildId, 'moderationChannel');
    }
    getBotStatus() {
        return [
            "Bot par RedBoxing",
            "https://redboxing.fr",
            ".help | Bot par RedBoxing",
            "RedBoxing = 👑",
            "Azes = 🧊",
            "Zephyr = 🎄",
            "Quentin = ❤️",
            "Yoshi = 🌴",
            "Naruki = 🍟",
            "Les frites c'est Zephyr",
            "Acheter RedBot Premium",
        ];
    }
    async setConfig(guildId, name, value) {
        try {
            let config = await GuildConfig_1.default.findOne({
                where: {
                    guildId: guildId
                }
            });
            if (!config) {
                config = await GuildConfig_1.default.create({
                    guildId: guildId
                });
            }
            if (name in config) {
                config[name] = value;
                await config.save();
            }
            else {
                logger.error(`unknown property ${name} guild config for guild : ${guildId}`);
            }
        }
        catch (err) {
            logger.error(`Failed to set config for guild ${guildId} : ${err}`);
        }
    }
    async getConfig(guildId, name) {
        try {
            let config = await GuildConfig_1.default.findOne({
                where: {
                    guildId: guildId
                }
            });
            if (!config) {
                config = await GuildConfig_1.default.create({
                    guildId: guildId
                });
            }
            if (name in config) {
                return config[name];
            }
            else {
                logger.error(`unknown property ${name} guild config for guild : ${guildId}`);
            }
        }
        catch (err) {
            logger.error(`Failed to get config for guild ${guildId} : ${err}`);
        }
    }
}
exports.default = BotConfigurable;
