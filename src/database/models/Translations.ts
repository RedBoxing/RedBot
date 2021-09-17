import { Model, Table, Column, AllowNull, NotEmpty, DataType } from "sequelize-typescript";

export interface ITranslations {
    lang: string,
    name: string,
    value: string
}

@Table(
    {
        tableName: 'translations',
        timestamps: true
    }
)
export default class Translation extends Model implements ITranslations {
    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    lang: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    name: string;

    @AllowNull(false)
    @NotEmpty
    @Column(DataType.STRING)
    value: string;
}