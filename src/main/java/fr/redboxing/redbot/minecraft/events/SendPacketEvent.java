package fr.redboxing.redbot.minecraft.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.Packet;

@RequiredArgsConstructor
public class SendPacketEvent extends Cancellable{
    @Getter
    private final Packet<?> packet;
}
