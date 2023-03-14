package com.mw_team.mw_industry_additions.client.screen;

import com.mojang.logging.LogUtils;
import com.mw_team.mw_industry_additions.blocks.entities.BloomeryChamberEntity;
import com.mw_team.mw_industry_additions.init.ModMenuTypes;
import com.mw_team.mw_industry_additions.meta.inventory.ContainerWrapper;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class BloomeryChamberMenu extends InventoryContainerMenu {
    //ok this is client side
    private final ContainerData data;
    public BloomeryChamberMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id,playerInv, new SimpleContainerData(6),extraData.readBlockPos());
    }

    //and this is server side.
    public BloomeryChamberMenu(int id, Inventory playerInv, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.BLOOMERY_CHAMBER_CONTAINER.get(), id, ((BloomeryChamberEntity)playerInv.player.level.getBlockEntity(pos)).inventory.get(),pos);
        SegmentedItemStackHandler slots = (SegmentedItemStackHandler)((ContainerWrapper)this.inventory).get();
        addPlayerInventory(playerInv,8,112);
        addColumn(slots.getSegment(BloomeryChamberEntity.inputInvSegment),19,30);
        addGrid(slots.getSegment(BloomeryChamberEntity.processingInvSegment), 66,30,2,8);
        addColumn(slots.getSegment(BloomeryChamberEntity.outputInvSegment),140,30);

        this.data=data;
        this.addDataSlots(data);
    }


    public int temp(){
        return data.get(BloomeryChamberEntity.TEMP_DATA);
    }
    public int process(int slot){
        return data.get(BloomeryChamberEntity.PROGRESS_DATA+slot);
    }
}
