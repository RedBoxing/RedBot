package baritone.bot.spec;

import baritone.bot.BaritoneUser;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.network.IMixinClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BotPlayerController extends ClientPlayerInteractionManager {
    private BaritoneUser user;

    public BotPlayerController(BaritoneUser user, ClientPlayNetworkHandler networkHandler) {
        super(user.getMinecraft(), networkHandler);
        this.user = user;
    }

    @Override
    public ActionResult interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult) {
        if(hitResult != null) {
            DiscordBot.getInstance().getMinecraftManager().getBot(this.user.getPlayer()).ifPresent(bot -> bot.getAltoClef().getContainerTracker().onBlockInteract(hitResult.getBlockPos(), world.getBlockState(hitResult.getBlockPos()).getBlock()));
        }

        return super.interactBlock(player, world, hand, hitResult);
    }

    @Override
    public void cancelBlockBreaking() {
        if(((IMixinClientPlayerInteractionManager) this).getCurrentBreakingProgress() == 0) {
            DiscordBot.getInstance().getMinecraftManager().getBot(this.user.getPlayer()).ifPresent(bot -> bot.getAltoClef().getControllerExtras().onBlockStopBreaking());
        }

        super.cancelBlockBreaking();
    }

    @Override
    public boolean updateBlockBreakingProgress(BlockPos pos, Direction direction) {
        return super.updateBlockBreakingProgress(pos, direction);
    }
}
