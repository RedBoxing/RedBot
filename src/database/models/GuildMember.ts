import { Model, Table, AutoIncrement, PrimaryKey, Column, AllowNull, NotEmpty, DataType } from "sequelize-typescript";

export interface IGuildMember {
    id?: number | null,
    guildId: string,
    userId: string,
    experience: number,
    last_experience_increase: number,
    join_date: number
}

@Table(
    {
        tableName: 'guilds_member',
        timestamps: true
    }
)
export default class GuildMember extends Model implements IGuildMember {
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
    @Column(DataType.INTEGER)
    experience: number;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.INTEGER)
    last_experience_increase: number;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.INTEGER)
    join_date: number;
}