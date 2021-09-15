"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.reactionHandler = exports.ReactionListener = exports.ReactionHandler = void 0;
class ReactionHandler {
    listeners = new Map();
    addReactionListener(guild, msg, handler) {
        if (!handler)
            return;
        if (msg.channel.isText() && !msg.guild.me.hasPermission('ADD_REACTIONS'))
            return;
        if (!this.listeners.has(guild.id)) {
            this.listeners.set(guild.id, new Map());
        }
        if (!this.listeners.get(guild.id).has(msg.id)) {
            for (let i in handler.reactions)
                msg.react(handler.reactions[i]);
            this.listeners.get(guild.id).set(msg.id, handler);
        }
    }
    async handle(channel, msg, user, reaction) {
        const listener = this.listeners.get(channel.guild.id).get(msg.id);
        if (!listener.active || listener.expiresIn < new Date().getTime()) {
            this.listeners.get(channel.guild.id).delete(msg.id);
        }
        else if (listener.hasReaction(reaction.emoji.name) && listener.userId === user.id) {
            const message = channel.messages.resolve(msg);
            listener.react(reaction.emoji.name, message);
            reaction.remove();
        }
    }
}
exports.ReactionHandler = ReactionHandler;
class ReactionListener {
    reactions;
    userId;
    data;
    expiresIn;
    lastAction;
    active;
    constructor(user, data) {
        this.data = data;
        this.userId = user.id;
        this.reactions = new Array();
        this.active = true;
        this.lastAction = new Date().getTime();
        this.expiresIn = 300000;
    }
    hasReaction(emote) {
        return this.reactions.find(r => r === emote) != null;
    }
    registerReaction(emote) {
        this.reactions.push(emote);
    }
    react(emote, msg) {
        msg.react(emote);
    }
    updateLastAction() {
        this.lastAction = new Date().getTime();
    }
}
exports.ReactionListener = ReactionListener;
exports.reactionHandler = new ReactionHandler();
