import GuildConfig from '../../database/models/GuildConfig';

export const availableOptions = {
    "mutedRole": "Muted Role",
    "announcementChannel" : "Announcement Channel",
    "commandReaction" : "Command Reaction",
    "moderationChannel" : "Moderation Channel",
    "countingChannel" : "Counting Channel"
}

export default class BotConfigurable {
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

    public async setCountingChannel(guildId: string, value: string) : Promise<void> {
        await this.setConfig(guildId, 'countingChannel', value);
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

    public async getCountingChannel(guildId: string) : Promise<string> {
        return await this.getConfig(guildId, 'countingChannel');
    }

    public getBotStatus() : Array<string> {
        return [
            "Bot par RedBoxing",
            "https://redboxing.fr",
            "/help | Bot par RedBoxing",
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
        const config = (await GuildConfig.findOne({
            where: {
                guildId,
                name
            }
        }));

        if(!config) {
            await GuildConfig.create({
                guildId: guildId,
                name,
                value
            })

            return;
        }

        await GuildConfig.update({
            value
        }, {
            where: {
                guildId,
                name
            }
        });  
    }

    public async getConfig(guildId: string, name: string, _default : any = undefined) : Promise<any> {
        let config : GuildConfig = (await GuildConfig.findOne({
            where: {
                guildId,
                name
            }
        }))

        if(!config) {
            config = (await GuildConfig.create({
                guildId,
                name,
                _default
            }));
        }

        return config.get().value;
    }
}