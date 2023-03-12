package com.mw_team.mw_industry_additions.blocks.entities;

import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler.SidedSegmentedInvWrapper;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mini2Dx.gdx.utils.Array;

public abstract class BlockEntityCapabilityManager {
    public abstract <T> LazyOptional<T> getCap(@NotNull Capability<T> cap, @Nullable Direction side);

    public abstract void invalidate();



    public static class SegmentedInventoryCapabilityManager extends BlockEntityCapabilityManager{
        protected LazyOptional<IItemHandlerModifiable>[] handlers;
        int[] directionMapping = {-1,-1,-1,-1,-1,-1};
        public SegmentedInventoryCapabilityManager(SegmentedItemStackHandler e){
            Array<LazyOptional<IItemHandlerModifiable>> array = new Array<>(LazyOptional.class);
            for(Direction d:Direction.values()){
                if(e.hasCapabilityInDirection(d)){
                    array.add(SidedSegmentedInvWrapper.create(e,d));
                    directionMapping[d.ordinal()] = array.size-1;
                }
            }
            handlers = array.toArray();
        }
        @Override
        public <T> LazyOptional<T> getCap(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (side != null && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                if(directionMapping[side.ordinal()]!=-1){
                    return handlers[directionMapping[side.ordinal()]].cast();
                }
            }
            return null;
        }

        @Override
        public void invalidate() {
            for(int i = 0;i<handlers.length;i++){
                handlers[i].invalidate();
            }
        }
    }
}
