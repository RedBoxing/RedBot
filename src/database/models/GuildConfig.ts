import { Model, Table, AutoIncrement, PrimaryKey, Column, AllowNull, NotEmpty, DataType } from "sequelize-typescript";

export interface IGuildConfig {
    guildId: string,
    name: string,
    value: string
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

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    name: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    value: string;
}