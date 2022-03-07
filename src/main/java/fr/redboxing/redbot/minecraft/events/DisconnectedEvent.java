package fr.redboxing.redbot.minecraft.events;

import net.minecraft.text.Text;

public record DisconnectedEvent(Text reason) {
}
