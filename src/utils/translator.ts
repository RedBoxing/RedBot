import DiscordClient from "../client/client";
import Translation from "../database/models/Translations";

export default class Translator {
    private client : DiscordClient;

    constructor(client: DiscordClient) {
        this.client = client;
    }

    public async getTranslation(guildId: string, name: string) : Promise<string> {
        const lang = await this.client.getConfig().getConfig(guildId, 'language', 'en');

        let translation : Translation = await Translation.findOne({
            where: {
                lang,
                name
            }
        });

        if(translation === undefined) {
            translation = await Translation.findOne({
                where: {
                    lang: 'en',
                    name
                }
            })
        }

        return translation.get().value;
    }
}