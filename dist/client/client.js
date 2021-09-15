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
const Discord = __importStar(require("discord.js"));
const erela_js_1 = require("erela.js");
const CommandHandler_1 = __importDefault(require("../structures/CommandHandler"));
const BotConfigurable_1 = __importDefault(require("../structures/config/BotConfigurable"));
const EventHandler_1 = __importDefault(require("../structures/EventHandler"));
class DiscordClient extends Discord.Client {
    commands;
    events;
    config;
    nodes;
    constructor(options) {
        super(options);
        this.commands = new CommandHandler_1.default();
        this.events = new EventHandler_1.default();
        this.config = new BotConfigurable_1.default();
        this.nodes = [
            {
                host: process.env.LAVALINK_HOST,
                port: parseInt(process.env.LAVALINK_PORT),
                password: process.env.LAVALINK_PASSWORD
            }
        ];
        this.manager = new erela_js_1.Manager({
            nodes: this.nodes,
            send: (id, payload) => {
                const guild = this.guilds.cache.get(id);
                if (guild)
                    guild.shard.send(payload);
            }
        });
    }
    addCommand(command) {
        this.commands.set(command.getName(), command);
        return this;
    }
    deleteCommand(command) {
        const cmd = this.searchCommand(command);
        if (cmd) {
            this.commands.delete(cmd.getName());
            return this;
        }
        return false;
    }
    searchCommand(command) {
        return this.commands.get(command) ? this.commands.get(command) : null;
    }
    addEvent(event) {
        this.events.set(event.getName(), event);
        const name = event.getName();
        this.on(name, event.exec.bind(null, this));
    }
    addMusicManagerEvent(event) {
        const name = event.getName();
        this.manager.on(name, event.exec.bind(null, this));
    }
    getCommands() {
        return this.commands;
    }
    getEvents() {
        return this.events;
    }
    getConfig() {
        return this.config;
    }
}
exports.default = DiscordClient;
