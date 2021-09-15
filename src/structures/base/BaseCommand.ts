import CommandHandler from "../CommandHandler";
import Client from '../../client/client'

import { CommandInteraction, PermissionResolvable } from "discord.js";
import { SlashCommandBuilder } from "@discordjs/builders";

export default abstract class BaseCommand {
    private handler: CommandHandler;
    private name: string;
    private description: string;
    private category: string;
    private aliases: Array<string>;
    private permissions: Array<PermissionResolvable>;
  
    constructor(name: string, description: string, category: string, aliases: Array<string>, permissions: Array<PermissionResolvable>) {
      this.name = name;
      this.description = description;
      this.category = category;
      this.aliases = aliases;
      this.permissions = permissions;
    }
  
    public getName(): string { return this.name; }
    public getDescription(): string { return this.description; }
    public getCategory(): string { return this.category; }
    public getAliases(): Array<String> { return this.aliases; }
    public getPermissions(): Array<PermissionResolvable> { return this.permissions; }
    public setCommandHandler(handler: CommandHandler): void { this.handler = handler; }
    public getCommandHandler(): CommandHandler { return this.handler; }
    public abstract exec(client: Client, interaction: CommandInteraction): Promise<void>;
    public abstract build(builder : SlashCommandBuilder) : SlashCommandBuilder;
  }