package baritone.bot;

import baritone.api.bot.IBaritoneUser;
import baritone.api.cache.IWorldData;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.IPlayerController;
import baritone.api.utils.RayTraceUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BotPlayerContext implements IPlayerContext {
    private final IBaritoneUser bot;

    public BotPlayerContext(IBaritoneUser bot) {
        this.bot = bot;
    }

    @Override
    public ClientPlayerEntity player() {
        if (this.bot.getPlayer() == null) {
            return null;
        }
        return this.bot.getPlayer();
    }

    @Override
    public IPlayerController playerController() {
        if (this.bot.getPlayer() == null) {
            return null;
        }
        return this.bot.getPlayerController();
    }

    @Override
    public World world() {
        return this.bot.getWorld();
    }

    @Override
    public IWorldData worldData() {
        return this.bot.getBaritone().getWorldProvider().getCurrentWorld();
    }

    @Override
    public HitResult objectMouseOver() {
        return RayTraceUtils.rayTraceTowards(this.player(), this.playerRotations(), this.playerController().getBlockReachDistance());
    }
}
