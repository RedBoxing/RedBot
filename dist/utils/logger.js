"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.auth = exports.success = exports.error = exports.info = void 0;
const colors_1 = __importDefault(require("colors"));
function info(message) {
    console.log(colors_1.default.cyan('[INFO]'), message);
}
exports.info = info;
function error(message) {
    console.log(colors_1.default.red('[ERROR]'), message);
}
exports.error = error;
function success(message) {
    console.log(colors_1.default.green('[SUCCESS]'), message);
}
exports.success = success;
function auth(message) {
    console.log(colors_1.default.yellow('[AUTH]'), message);
}
exports.auth = auth;
