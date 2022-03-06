package fr.redboxing.redbot.minecraft.events;

import net.minecraft.network.MessageType;

import java.util.UUID;

public record ChatMessageEvent(MessageType messageType, String name, String message, UUID sender) {
}
