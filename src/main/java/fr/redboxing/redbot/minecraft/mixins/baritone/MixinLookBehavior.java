package fr.redboxing.redbot.minecraft.mixins.baritone;

import baritone.Baritone;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.utils.Rotation;
import baritone.behavior.Behavior;
import baritone.behavior.LookBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LookBehavior.class, remap = false)
public abstract class MixinLookBehavior extends Behavior {
    @Shadow
    private Rotation target;

    @Shadow private boolean force;

    @Shadow protected abstract void nudgeToLevel();

    @Shadow private float lastYaw;

    protected MixinLookBehavior(Baritone baritone) {
        super(baritone);
    }

    /**
     * @author RedBoxing
     * @reason SmoothAim
     */
    @Overwrite
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (this.target != null) {
            boolean silent = (Boolean) Baritone.settings().antiCheatCompatibility.value && !this.force;
            switch(event.getState()) {
                case PRE:
                    if (this.force) {
                        /*this.ctx.player().setYaw(this.target.getYaw());
                        float oldPitch = this.ctx.player().getPitch();
                        float desiredPitch = this.target.getPitch();
                        this.ctx.player().setPitch(desiredPitch);*/

                        float smoothAim = 2f;

                        float oldYaw = Math.round(ctx.player().getYaw());
                        float desiredYaw = Math.round(this.target.getYaw());
                        float oldPitch = Math.round(ctx.player().getPitch());
                        float desiredPitch = Math.round(this.target.getPitch());
                        float difYaw = (desiredYaw - oldYaw) / Math.round(smoothAim + Math.random());
                        float difPitch = (desiredPitch - oldPitch) / Math.round(smoothAim + Math.random());
                        if (ctx.player().getYaw() != desiredYaw) {
                            ctx.player().setYaw(ctx.player().getYaw() + difYaw);
                        }
                        if (ctx.player().getPitch() != desiredPitch) {
                            ctx.player().setPitch(ctx.player().getPitch() + difPitch);
                        }

                        this.ctx.player().setYaw((float)((double)this.ctx.player().getYaw() + (Math.random() - 0.5D) * (Double)Baritone.settings().randomLooking.value));
                        this.ctx.player().setPitch((float)((double)this.ctx.player().getPitch() + (Math.random() - 0.5D) * (Double)Baritone.settings().randomLooking.value));
                        if (desiredPitch == oldPitch && !(Boolean)Baritone.settings().freeLook.value) {
                            this.nudgeToLevel();
                        }

                        this.target = null;
                    }

                    if (silent) {
                        this.lastYaw = this.ctx.player().getYaw();
                        this.ctx.player().setYaw(this.target.getYaw());
                    }
                    break;
                case POST:
                    if (silent) {
                        this.ctx.player().setYaw(this.lastYaw);
                        this.target = null;
                    }
            }

        }
    }
}
