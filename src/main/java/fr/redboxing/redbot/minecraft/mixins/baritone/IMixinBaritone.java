package fr.redboxing.redbot.minecraft.mixins.baritone;

import baritone.Baritone;
import baritone.api.utils.IPlayerContext;
import baritone.behavior.InventoryBehavior;
import baritone.behavior.LookBehavior;
import baritone.behavior.MemoryBehavior;
import baritone.behavior.PathingBehavior;
import baritone.cache.WorldProvider;
import baritone.command.manager.CommandManager;
import baritone.event.GameEventHandler;
import baritone.process.*;
import baritone.selection.SelectionManager;
import baritone.utils.InputOverrideHandler;
import baritone.utils.PathingControlManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Baritone.class, remap = false)
public interface IMixinBaritone {
    @Accessor("gameEventHandler")
    void setGameEventHandler(GameEventHandler value);

    @Accessor("pathingBehavior")
    void setPathingBehavior(PathingBehavior value);

    @Accessor("lookBehavior")
    void setLookBehavior(LookBehavior value);

    @Accessor("memoryBehavior")
    void setMemoryBehavior(MemoryBehavior value);

    @Accessor("inventoryBehavior")
    void setInventoryBehavior(InventoryBehavior value);

    @Accessor("inputOverrideHandler")
    void setInputOverrideHandler(InputOverrideHandler value);

    @Accessor("followProcess")
    void setFollowProcess(FollowProcess value);

    @Accessor("mineProcess")
    void setMineProcess(MineProcess value);

    @Accessor("getToBlockProcess")
    void setGetToBlockProcess(GetToBlockProcess value);

    @Accessor("customGoalProcess")
    void setCustomGoalProcess(CustomGoalProcess value);

    @Accessor("builderProcess")
    void setBuilderProcess(BuilderProcess value);

    @Accessor("exploreProcess")
    void setExploreProcess(ExploreProcess value);

    @Accessor("backfillProcess")
    void setBackfillProcess(BackfillProcess value);

    @Accessor("farmProcess")
    void setFarmProcess(FarmProcess value);

    @Accessor("pathingControlManager")
    void setPathingControlManager(PathingControlManager value);

    @Accessor("selectionManager")
    void setSelectionManager(SelectionManager value);

    @Accessor("commandManager")
    void setCommandManager(CommandManager value);

    @Accessor("playerContext")
    void setPlayerContext(IPlayerContext value);

    @Accessor("worldProvider")
    void setWorldProvider(WorldProvider value);

    @Accessor("backfillProcess")
    BackfillProcess getBackfillProcess();
}
