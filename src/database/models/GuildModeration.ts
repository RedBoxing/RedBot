import { Model, Table, AutoIncrement, PrimaryKey, Column, AllowNull, NotEmpty, DataType } from "sequelize-typescript";

export interface IGuildModeration {
    guildId: string,
    userId: string,
    moderatorId: string,
    sanctionType: string,
    reason: string,
    expiration: Date,
    sanctionDate: Date
}

@Table(
    {
        tableName: 'guilds_moderations',
        timestamps: true
    }
)
export default class GuildModeration extends Model implements IGuildModeration {
    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    guildId: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    userId: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    moderatorId: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    sanctionType: string;

    @AllowNull(true)
    @NotEmpty
    @Column(DataType.STRING)
    reason: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.DATE)
    expiration: Date;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.DATE)
    sanctionDate: Date;
}