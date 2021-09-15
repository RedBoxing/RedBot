import { Manager } from 'erela.js'

declare module "discord.js" {
    interface Client extends BaseClient {
        manager: Manager
    }
}

export interface Settings {
    bot: { token: string, default_prefix: string, status : string[] },
    lavalink: { host: string, port: number, password: string },
    apis: { google: string },
    database: { host: string, port: number, user: string, pass: string, name: string },
    other: { creator: number }
}