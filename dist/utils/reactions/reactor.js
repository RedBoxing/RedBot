"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.reactor = exports.Reactor = void 0;
const ACK_REACTIONS = ['👍', '🎮', '💚', '🍜'];
const EXPIRED_REACTIONS = ['🖤'];
const FAILURE_REACTIONS = ['❌', '⛔'];
/** Gets a random element of an array. */
const getRandom = (array) => array[Math.floor(Math.random() * array.length)];
class Reactor {
    /** Indicates to the user that the command was executed successfully. */
    async success(message) {
        if (!(await this.isReactionEnabled(message)))
            return;
        await message.react(getRandom(ACK_REACTIONS));
    }
    /** Indicates to the user that the command failed for some reason. */
    async failure(message) {
        if (!(await this.isReactionEnabled(message)))
            return;
        await message.reactions.removeAll();
        await message.react(getRandom(FAILURE_REACTIONS));
    }
    /** Indicates to the user that the command is no longer active, as intended. */
    async expired(message) {
        if (!(await this.isReactionEnabled(message)))
            return;
        await message.reactions.removeAll();
        await message.react(getRandom(EXPIRED_REACTIONS));
    }
    async isReactionEnabled(message) {
        return (await message.client.getConfig().getCommandReaction(message.guild.id));
    }
}
exports.Reactor = Reactor;
exports.reactor = new Reactor();
