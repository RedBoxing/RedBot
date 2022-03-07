package fr.redboxing.redbot.minecraft.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;

@RequiredArgsConstructor
public class PlayerTravelEvent extends Cancellable {
    @Getter
    private final PlayerEntity player;
}
