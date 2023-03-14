package com.mw_team.mw_industry_additions.meta.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ContainerWrapper extends RecipeWrapper {
    BlockPos worldPosition;
    public ContainerWrapper(BlockPos bp, IItemHandlerModifiable inv) {
        super(inv);
        this.worldPosition =bp;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        //idk lmao
    }

    @Override
    public boolean stillValid(Player player) {
        if(worldPosition == null){
            return true;
        }
        return !(player.distanceToSqr(this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
    }

    public IItemHandlerModifiable get(){
        return inv;
    }
}
