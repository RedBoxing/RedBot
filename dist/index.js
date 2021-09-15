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
const dotenv_1 = require("dotenv");
(0, dotenv_1.config)();
const registry_1 = require("./utils/registry");
const client_1 = __importDefault(require("./client/client"));
const TrackStartEvent_1 = __importDefault(require("./events/music/TrackStartEvent"));
const TrackEndEvent_1 = __importDefault(require("./events/music/TrackEndEvent"));
const RawEvent_1 = __importDefault(require("./events/music/RawEvent"));
const logger = __importStar(require("./utils/logger"));
const client = new client_1.default();
(async () => {
    await (0, registry_1.registerCommands)(client, '../commands');
    await (0, registry_1.registerEvents)(client, '../events');
    await client.addMusicManagerEvent(new TrackStartEvent_1.default());
    await client.addMusicManagerEvent(new TrackEndEvent_1.default());
    await client.addMusicManagerEvent(new RawEvent_1.default());
    await client.login("Mzg0MjYwNzI4Nzg3NDM1NTMw.Whp8sA.K8N0tOprY8PWFv8EhDd5C11wJak");
})();
process.on('SIGINT', () => {
    logger.info("Gracefully shutting down from SIGINT (Ctrl-C)");
    client.destroy();
    process.exit(0);
});
