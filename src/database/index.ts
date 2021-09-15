import { Sequelize } from 'sequelize-typescript'
import { Dialect } from 'sequelize/types';

export const sequelize = new Sequelize(process.env.DATABASE_DB, process.env.DATABASE_USER, process.env.DATABASE_PASSWORD, {
  host: process.env.DATABASE_HOST,
  dialect: process.env.DATABASE_DIALECT as Dialect,
  models: [ __dirname + "/models" ],
  logging: false
});