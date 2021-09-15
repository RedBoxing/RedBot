"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.checkPermission = void 0;
function checkPermission(member, permissions) {
    for (const permission of permissions) {
        if (!member.hasPermission(permission))
            return false;
    }
    return true;
}
exports.checkPermission = checkPermission;
