"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.sequelize = void 0;
const sequelize_typescript_1 = require("sequelize-typescript");
exports.sequelize = new sequelize_typescript_1.Sequelize(process.env.DATABASE_DB, process.env.DATABASE_USER, process.env.DATABASE_PASSWORD, {
    host: process.env.DATABASE_HOST,
    dialect: process.env.DATABASE_DIALECT,
    models: [__dirname + "/models"],
    logging: false
});
