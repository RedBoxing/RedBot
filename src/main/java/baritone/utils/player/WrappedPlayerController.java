package baritone.utils.player;

import baritone.api.utils.IPlayerController;
import baritone.utils.accessor.IPlayerControllerMP;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class WrappedPlayerController implements IPlayerController {
    private final ClientPlayerInteractionManager controller;

    public WrappedPlayerController(ClientPlayerInteractionManager controller) {
        this.controller = controller;
    }


    protected ClientPlayerInteractionManager getController() {
        return this.controller;
    }

    @Override
    public void syncHeldItem() {
        ((IPlayerControllerMP) this.getController()).callSyncCurrentPlayItem();
    }

    @Override
    public boolean hasBrokenBlock() {
        return ((IPlayerControllerMP) this.getController()).getCurrentBlock().getY() == -1;
    }

    @Override
    public boolean onPlayerDamageBlock(BlockPos pos, Direction side) {
        return this.getController().updateBlockBreakingProgress(pos, side);
    }

    @Override
    public void resetBlockRemoving() {
        this.getController().cancelBlockBreaking();
    }

    @Override
    public void windowClick(int windowId, int slotId, int mouseButton, SlotActionType type, PlayerEntity player) {
        this.getController().clickSlot(windowId, slotId, mouseButton, type, player);
    }


    @Override
    public GameMode getGameType() {
        return this.getController().getCurrentGameMode();
    }

    @Override
    public ActionResult processRightClickBlock(ClientPlayerEntity player, World world, Hand hand, BlockHitResult result) {
        return this.getController().interactBlock(player, (ClientWorld)world, hand, result);
    }

    @Override
    public ActionResult processRightClick(ClientPlayerEntity player, World world, Hand hand) {
        return this.getController().interactItem(player, world, hand);
    }

    @Override
    public boolean clickBlock(BlockPos loc, Direction face) {
        return this.getController().attackBlock(loc, face);
    }

    @Override
    public void setHittingBlock(boolean hittingBlock) {
        ((IPlayerControllerMP) this.getController()).setIsHittingBlock(hittingBlock);
    }
}
