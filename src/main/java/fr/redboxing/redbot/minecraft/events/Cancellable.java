package fr.redboxing.redbot.minecraft.events;

import lombok.Getter;
import lombok.Setter;

public class Cancellable {
    @Getter
    @Setter
    private boolean cancelled = false;
}

