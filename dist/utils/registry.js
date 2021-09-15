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
exports.registerEvents = exports.registerCommands = void 0;
const path_1 = __importDefault(require("path"));
const BaseCommand_1 = __importDefault(require("../structures/base/BaseCommand"));
const fs_1 = require("fs");
const BaseEvent_1 = __importDefault(require("../structures/base/BaseEvent"));
function asyncForEach(arr, cb) {
    for (let i = 0; i < arr.length; i++) {
        cb(arr[i], i, arr);
    }
}
async function getFiles(filePath) {
    return fs_1.promises.readdir(filePath);
}
async function isDir(dir) {
    const d = await fs_1.promises.lstat(dir);
    return d.isDirectory();
}
async function registerCommands(client, dir) {
    const filePath = path_1.default.join(__dirname, dir);
    const files = await getFiles(filePath);
    if (files.length === 0)
        return null;
    await asyncForEach(files, async (file) => {
        const _isDir = await isDir(path_1.default.join(filePath, file));
        if (_isDir)
            await registerCommands(client, path_1.default.join(dir, file));
        if (file.endsWith('.ts') || file.endsWith('.js')) {
            const BaseCommandClass = await Promise.resolve().then(() => __importStar(require(path_1.default.join(dir, file))));
            const command = new BaseCommandClass.default();
            if (command instanceof BaseCommand_1.default) {
                client.addCommand(command);
            }
        }
    });
    return true;
}
exports.registerCommands = registerCommands;
async function registerEvents(client, dir) {
    const filePath = path_1.default.join(__dirname, dir);
    const files = await getFiles(filePath);
    if (files.length === 0)
        return null;
    await asyncForEach(files, async (file) => {
        const _isDir = await isDir(path_1.default.join(filePath, file));
        if (_isDir)
            await registerEvents(client, path_1.default.join(dir, file));
        else if (file.endsWith('.ts') || file.endsWith('.js')) {
            const BaseEventClass = await Promise.resolve().then(() => __importStar(require(path_1.default.join(dir, file))));
            const event = new BaseEventClass.default();
            if (event instanceof BaseEvent_1.default) {
                client.addEvent(event);
            }
        }
    });
    return true;
}
exports.registerEvents = registerEvents;
