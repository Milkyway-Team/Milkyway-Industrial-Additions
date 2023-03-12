package com.mw_team.mw_industry_additions.client.screen;

import com.mw_team.mw_industry_additions.init.ModBlocks;
import com.mw_team.mw_industry_additions.init.ModMenuTypes;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BloomeryChamberContainer extends InventoryContainerMenu {
    public BloomeryChamberContainer(int id, Inventory playerInv) {
        this(id,playerInv,new SegmentedItemStackHandler(32), new SimpleContainerData(0),BlockPos.ZERO);
    }

    public BloomeryChamberContainer(int id, Inventory playerInv, SegmentedItemStackHandler slots, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.BLOOMERY_CHAMBER_CONTAINER.get(), id,slots,pos);
        addPlayerInventory(playerInv,22,118);
        addColumn(slots,0,14,24,3);
        addSingle(slots, 3,94,46);
        addColumn(slots,4,172,24,3);
    }
}
