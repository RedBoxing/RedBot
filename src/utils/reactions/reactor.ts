import { Message } from 'discord.js';

const ACK_REACTIONS = ['👍', '✔️', '💚', '✅'];
const EXPIRED_REACTIONS = ['🖤'];
const FAILURE_REACTIONS = ['❌', '⛔'];

/** Gets a random element of an array. */
const getRandom = (array: string[]) =>
  array[Math.floor(Math.random() * array.length)];

export class Reactor {
  /** Indicates to the user that the command was executed successfully. */
  async success(message: Message) : Promise<void> {
    await message.react(getRandom(ACK_REACTIONS));
  }

  /** Indicates to the user that the command failed for some reason. */
  async failure(message: Message) : Promise<void> {
    await message.reactions.removeAll();
    await message.react(getRandom(FAILURE_REACTIONS));
  }

  /** Indicates to the user that the command is no longer active, as intended. */
  async expired(message: Message) : Promise<void> {
    await message.reactions.removeAll();
    await message.react(getRandom(EXPIRED_REACTIONS));
  }
}

export const reactor = new Reactor();