import { Model, Table, AutoIncrement, PrimaryKey, Column, AllowNull, NotEmpty, DataType } from "sequelize-typescript";

export interface IGuildConfig {
    guildId: string,
    prefix: string,
    mutedRole: string,
    announcementChannel: string,
    commandReaction: boolean,
    moderationChannel: string
}

@Table(
    {
        tableName: 'guilds_config',
        timestamps: true
    }
)
export default class GuildConfig extends Model implements IGuildConfig {
    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    guildId: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    prefix: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    mutedRole: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    announcementChannel: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    commandReaction: boolean;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    moderationChannel: string;
}