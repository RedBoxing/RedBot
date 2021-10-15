package fr.redboxing.redbot.command;

import com.jagrosh.jdautilities.command.Command;

import java.util.HashMap;

public class CommandCategory {
    public static Command.Category ADMINISTRATION = new Command.Category("Administration");
    public static Command.Category INFORMATION = new Command.Category("Information");
    public static Command.Category MUSIC = new Command.Category("Music");
    public static Command.Category FUN = new Command.Category("Fun");
    public static Command.Category UNKNOWN = new Command.Category("Unknown");

    public static enum Enum {
        ADMINISTRATION(CommandCategory.ADMINISTRATION, "\uD83D\uDEA6"),
        INFORMATION(CommandCategory.INFORMATION, "\u2139\uFE0F"),
        MUSIC(CommandCategory.MUSIC, "\uD83D\uDD0A"),
        FUN(CommandCategory.FUN, "\uD83C\uDFB2"),
        UNKNOWN(CommandCategory.UNKNOWN, "\u2753")
        ;

        private Command.Category category;
        private String emote;

        Enum(Command.Category category, String emote) {
            this.category = category;
            this.emote = emote;
        }

        public String getName() {
            return this.category.getName();
        }

        public Command.Category getCategory() {
            return category;
        }

        public String getEmote() {
            return emote;
        }
    }
}
