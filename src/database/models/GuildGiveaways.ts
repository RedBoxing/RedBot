import { Model, Table, Column, AllowNull, NotEmpty, DataType } from "sequelize-typescript";

export interface IGuildGiveaways {
    guildId: string,
    messageId: string,
    channelId: string,
    hostId: string,
    winners: number,
    prize: string,
    end: Date
}

@Table(
    {
        tableName: 'guilds_giveaways',
        timestamps: true
    }
)
export default class GuildGiveaways extends Model implements IGuildGiveaways {
    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    guildId: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    messageId: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    channelId: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    hostId: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.INTEGER)
    winners: number;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    prize: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.DATE)
    end: Date;
}