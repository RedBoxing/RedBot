import GuildConfig from '../../database/models/GuildConfig';

import * as logger from '../../utils/logger'

export default class BotConfigurable {
    public async setPrefix(guildId: string, value: string) : Promise<void> {
        await this.setConfig(guildId, 'prefix', value);
    }

    public async setMutedRole(guildId: string, value: string) : Promise<void> {
        await this.setConfig(guildId, 'mutedRole', value);
    }

    public async setAnnouncementChannel(guildId: string, value: string) : Promise<void> {
        await this.setConfig(guildId, 'announcementChannel', value);
    }

    public async setCommandReaction(guildId: string, value: boolean) : Promise<void> {
        await this.setConfig(guildId, 'commandReaction', value);
    }

    public async setModerationChannel(guildId: string, value: string) : Promise<void> {
        await this.setConfig(guildId, 'moderationChannel', value);
    }

    public async getPrefix(guildId: string) : Promise<string> {
        return await this.getConfig(guildId, 'prefix');
    }

    public async getMutedRole(guildId: string) : Promise<string> {
        return await this.getConfig(guildId, 'mutedRole');
    }

    public async getAnnouncementChannel(guildId: string) : Promise<string> {
        return await this.getConfig(guildId, 'announcementChannel');
    }

    public async getCommandReaction(guildId: string) : Promise<boolean> {
        return await this.getConfig(guildId, 'commandReaction');
    }

    public async getModerationChannel(guildId: string) : Promise<string> {
        return await this.getConfig(guildId, 'moderationChannel');
    }

    public getBotStatus() : Array<string> {
        return [
            "Bot par RedBoxing",
            "https://redboxing.fr",
            ".help | Bot par RedBoxing",
            "RedBoxing = 👑",
            "Azes = 🧊",
            "Zephyr = 🎄",
            "Quentin = ❤️",
            "Yoshi = 🌴",
            "Naruki = 🍟",
            "Les frites c'est Zephyr",
            "Acheter RedBot Premium",
        ]
    }

    public async setConfig(guildId: string, name: string, value: any) : Promise<void> {
        return new Promise<void>((resolve, reject) => {
            GuildConfig.findOne({
                where: {
                    guildId: guildId
                }
            }).then(config => {
                if(!config) {
                    GuildConfig.create({
                        guildId: guildId
                    }).then(_config => {
                        if(name in _config) {
                            _config[name] = value;
                            _config.save();
                        } else {
                            logger.error(`unknown property ${name} guild config for guild : ${guildId}`);
                        }
                    });

                    return;
                }

                if(name in config) {
                    config[name] = value;
                    config.save();
                } else {
                    logger.error(`unknown property ${name} guild config for guild : ${guildId}`);
                }
            })    
        });
    }

    public getConfig(guildId: string, name: string) : Promise<any> {
        return new Promise<any>((resolve, reject) => {
            GuildConfig.findOne({
                where: {
                    guildId: guildId
                }
            }).then(config => {
                if(!config) {
                    const _config = GuildConfig.create({
                        guildId: guildId
                    });

                    if(name in _config) {
                        resolve(_config[name]);
                    } else {
                        logger.error(`unknown property ${name} guild config for guild : ${guildId}`);
                    }

                    return;
                }

                if(name in config) {
                    resolve(config[name]);
                } else {
                    logger.error(`unknown property ${name} guild config for guild : ${guildId}`);
                }
            })    
        });
    }
}