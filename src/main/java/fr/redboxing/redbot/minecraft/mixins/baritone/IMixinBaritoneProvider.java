package fr.redboxing.redbot.minecraft.mixins.baritone;

import baritone.Baritone;
import baritone.BaritoneProvider;
import baritone.api.IBaritone;
import baritone.api.bot.IBaritoneUser;
import baritone.bot.UserManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = BaritoneProvider.class, remap = false)
public class IMixinBaritoneProvider {
    @Shadow
    @Final
    private Baritone primary;

    /**
     * @author RedBoxing
     */
    @Overwrite
    public List<IBaritone> getAllBaritones() {
        List<IBaritone> baritones = new ArrayList<>();
        baritones.add(this.primary);

        for(IBaritoneUser user : UserManager.INSTANCE.getUsers()) {
            baritones.add(user.getBaritone());
        }

        return baritones;
    }
}
