package com.mw_team.mw_industry_additions.meta.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SegmentSlot extends SlotItemHandler {
    SegmentedItemStackHandler.InventorySegment segment;
    public SegmentSlot(SegmentedItemStackHandler.InventorySegment segment, int index, int xPosition, int yPosition) {
        super(segment.inventory, index, xPosition, yPosition);
        this.segment = segment;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return segment.canManuallyInsert && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return segment.canManuallyExtract && super.mayPickup(playerIn);
    }

    public SegmentedItemStackHandler.InventorySegment getSegment() {
        return segment;
    }
}
