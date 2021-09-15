"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sequelize_typescript_1 = require("sequelize-typescript");
let GuildConfig = class GuildConfig extends sequelize_typescript_1.Model {
    guildId;
    prefix;
    mutedRole;
    announcementChannel;
    commandReaction;
    moderationChannel;
};
__decorate([
    (0, sequelize_typescript_1.AllowNull)(false),
    sequelize_typescript_1.NotEmpty,
    (0, sequelize_typescript_1.Column)(sequelize_typescript_1.DataType.STRING)
], GuildConfig.prototype, "guildId", void 0);
__decorate([
    (0, sequelize_typescript_1.AllowNull)(true),
    sequelize_typescript_1.NotEmpty,
    (0, sequelize_typescript_1.Column)(sequelize_typescript_1.DataType.STRING)
], GuildConfig.prototype, "prefix", void 0);
__decorate([
    (0, sequelize_typescript_1.AllowNull)(true),
    sequelize_typescript_1.NotEmpty,
    (0, sequelize_typescript_1.Column)(sequelize_typescript_1.DataType.STRING)
], GuildConfig.prototype, "mutedRole", void 0);
__decorate([
    (0, sequelize_typescript_1.AllowNull)(true),
    sequelize_typescript_1.NotEmpty,
    (0, sequelize_typescript_1.Column)(sequelize_typescript_1.DataType.STRING)
], GuildConfig.prototype, "announcementChannel", void 0);
__decorate([
    (0, sequelize_typescript_1.AllowNull)(true),
    sequelize_typescript_1.NotEmpty,
    (0, sequelize_typescript_1.Column)(sequelize_typescript_1.DataType.STRING)
], GuildConfig.prototype, "commandReaction", void 0);
__decorate([
    (0, sequelize_typescript_1.AllowNull)(true),
    sequelize_typescript_1.NotEmpty,
    (0, sequelize_typescript_1.Column)(sequelize_typescript_1.DataType.STRING)
], GuildConfig.prototype, "moderationChannel", void 0);
GuildConfig = __decorate([
    (0, sequelize_typescript_1.Table)({
        tableName: 'guilds_config',
        timestamps: true
    })
], GuildConfig);
exports.default = GuildConfig;
