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
const BaseEvent_1 = __importDefault(require("../structures/base/BaseEvent"));
const logger = __importStar(require("../utils/logger"));
const database_1 = require("../database");
class ReadyEvent extends BaseEvent_1.default {
    constructor() {
        super("ready");
    }
    async exec(client, args) {
        logger.info("Initializing database...");
        database_1.sequelize.authenticate().then(async () => {
            logger.success("Database successfully initialized !");
            try {
                await database_1.sequelize.sync();
            }
            catch (err) {
                logger.error(err.message);
                client.destroy();
                process.exit(0);
            }
            client.manager.init(client.user.id);
            logger.success("Bot Connected to " + client.guilds.cache.size + " guilds !");
        }).catch(err => {
            logger.error("Failed to connect to database : " + err);
        });
        const status = client.getConfig().getBotStatus();
        client.user.setActivity(status[Math.floor(Math.random() * (status.length - 1) + 1)], { type: 'WATCHING' });
        setInterval(() => {
            client.user.setActivity(status[Math.floor(Math.random() * (status.length - 1) + 1)], { type: 'WATCHING' });
        }, 10 * 1000);
    }
}
exports.default = ReadyEvent;
