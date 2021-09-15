import path from 'path'

import DiscordClient from "../client/client";
import BaseCommand from '../structures/base/BaseCommand';

import { promises as fs } from 'fs';
import BaseEvent from '../structures/base/BaseEvent';

function asyncForEach(arr: Array<any>, cb: Function) {
    for (let i = 0; i < arr.length; i++) {
        cb(arr[i], i, arr);
    }
}

async function getFiles(filePath: string) {
    return fs.readdir(filePath);
}

async function isDir(dir: string) {
    const d = await fs.lstat(dir);
    return d.isDirectory();
}

export async function registerCommands(client: DiscordClient, dir: string) {
    const filePath : string = path.join(__dirname, dir);
    const files : Array<string> = await getFiles(filePath);

    if (files.length === 0) return null;
    await asyncForEach(files, async (file: string) => {
        const _isDir: boolean = await isDir(path.join(filePath, file));

        if (_isDir) await registerCommands(client, path.join(dir, file));
        if (file.endsWith('.ts') || file.endsWith('.js')) {
            const BaseCommandClass = await import(path.join(dir, file));
            const command = new BaseCommandClass.default();
            if (command instanceof BaseCommand) {
                client.addCommand(command);
            }
        }
    });

    return true;
}

export async function registerEvents(client: DiscordClient, dir: string) {
    const filePath: string = path.join(__dirname, dir);
    const files: Array<string> = await getFiles(filePath);

    if (files.length === 0) return null;
    await asyncForEach(files, async (file: string) => {
        const _isDir: boolean = await isDir(path.join(filePath, file));
        if (_isDir) await registerEvents(client, path.join(dir, file));
        else if (file.endsWith('.ts') || file.endsWith('.js')) {
            const BaseEventClass = await import(path.join(dir, file));
            const event = new BaseEventClass.default();
            if (event instanceof BaseEvent) {
                client.addEvent(event);
            }
        }
    });
    
    return true;
}

