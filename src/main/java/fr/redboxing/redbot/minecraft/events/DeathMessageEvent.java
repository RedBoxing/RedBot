package fr.redboxing.redbot.minecraft.events;

import net.minecraft.entity.Entity;

public record DeathMessageEvent(Entity entity) {
}
