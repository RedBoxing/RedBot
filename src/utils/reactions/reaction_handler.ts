import { Guild, Message, MessageReaction, TextChannel, User } from "discord.js";

export class ReactionHandler {
    private listeners : Map<string, Map<string, ReactionListener<any>>> = new Map<string, Map<string, ReactionListener<any>>>();

    addReactionListener(guild: Guild, msg: Message, handler: ReactionListener<any>) {
        if(!handler) return;
        if(msg.channel.isText() && !msg.guild.me.permissions.has('ADD_REACTIONS')) return;

        if(!this.listeners.has(guild.id)) {
            this.listeners.set(guild.id, new Map<string, ReactionListener<any>>());
        }

        if(!this.listeners.get(guild.id).has(msg.id)) {
            for(let i in handler.reactions) 
                msg.react(handler.reactions[i]);
            
            this.listeners.get(guild.id).set(msg.id, handler);
        }
    }

    async handle(channel: TextChannel, msg: Message, user: User, reaction: MessageReaction) {
        const listener: ReactionListener<any> = this.listeners.get(channel.guild.id).get(msg.id)
        if(!listener.active || listener.expiresIn < new Date().getTime()) {
            this.listeners.get(channel.guild.id).delete(msg.id);
        } else if(listener.hasReaction(reaction.emoji.name) && listener.userId === user.id) {
            const message = channel.messages.resolve(msg);
            listener.react(reaction.emoji.name, message);
            reaction.remove();
        }
    }
}

export class ReactionListener<T> {
    reactions: string[];
    userId : string;
    data : T;
    expiresIn : number;
    lastAction : number;
    active : boolean

    constructor(user : User, data : T) {
        this.data = data;
        this.userId = user.id;
        this.reactions = new Array<string>();
        this.active = true;
        this.lastAction = new Date().getTime();
        this.expiresIn = 300000;
    }

    hasReaction(emote: string) {
        return this.reactions.find(r => r === emote) != null;
    }

    registerReaction(emote: string, ) {
        this.reactions.push(emote);
    }

    react(emote: string, msg : Message) {
        msg.react(emote);
    }

    updateLastAction() {
        this.lastAction = new Date().getTime();
    }
}

export const reactionHandler = new ReactionHandler();