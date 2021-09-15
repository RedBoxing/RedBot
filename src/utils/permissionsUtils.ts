import { APIInteractionGuildMember } from "discord-api-types";
import { GuildMember, PermissionResolvable } from "discord.js";

export function checkPermission(member: GuildMember, permissions: Array<PermissionResolvable>) : boolean {
    for(const permission of permissions) {
        if(!member.permissions.has(permission)) return false;
    }

    return true;
}