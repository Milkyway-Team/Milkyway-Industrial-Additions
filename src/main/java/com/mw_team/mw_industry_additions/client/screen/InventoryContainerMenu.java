package com.mw_team.mw_industry_additions.client.screen;

import com.mw_team.mw_industry_additions.meta.inventory.ContainerWrapper;
import com.mw_team.mw_industry_additions.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryContainerMenu extends AbstractContainerMenu {
    //what retarded mojang naming is this
    public Container inventory;
    public Inventory playerInv;
    ContainerData propertyDelegate;
    protected InventoryContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Container i) {
        super(pMenuType, pContainerId);
        this.inventory =i;
    }
    protected InventoryContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, IItemHandlerModifiable i) {
        super(pMenuType, pContainerId);
        this.inventory =new ContainerWrapper(null,i);
    }
    protected InventoryContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, IItemHandlerModifiable i, BlockPos bp) {
        super(pMenuType, pContainerId);
        this.inventory =new ContainerWrapper(bp,i);
    }
    public int getSyncedInt(int index){
        return propertyDelegate.get(index);
    }
    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.inventory.stillValid(player);
    }

    public Slot addSingle(Inventory pl, int index, int x,int y){
        return this.addSlot(new Slot(pl, index, x , y));
    }
    public Slot addSingle(IItemHandler pl, int index, int x,int y){
        return this.addSlot(new SlotItemHandler(pl, index, x , y));
    }
    public void addColumn(Inventory pl, int index, int x,int y, int amount){
        for (int m = 0; m < amount; ++m) {
            this.addSlot(new Slot(pl, m+index, x , y+ m * 18));
        }
    }
    public void addColumn(IItemHandler pl, int index, int x, int y, int amount){
        for (int m = 0; m < amount; ++m) {
            this.addSlot(new SlotItemHandler(pl, m+index, x , y+ m * 18));
        }
    }

    public void addRow(Container pl, int index, int x,int y, int amount){
        for (int m = 0; m < amount; ++m) {
            this.addSlot(new Slot(pl, m+index, x+ m * 18 , y));
        }
    }
    public void addRow(IItemHandler pl, int index, int x, int y, int amount){
        for (int m = 0; m < amount; ++m) {
            this.addSlot(new SlotItemHandler(pl, m+index, x+ m * 18 , y));
        }
    }
    /*
    public void addHiddenSlots(HiddenInventory pl){
        for (int m = 0; m < pl.size(); ++m) {
            pl.ids[m] = this.addSlot(new Slot(pl, m, -900 , -900)).id;
        }
    }*/

    public void addPlayerInventory(Inventory pl, int x,int y){
        this.playerInv=pl;
        int m;
        int l;
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(pl, l + m * 9 + 9, x + l * 18, y+ m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(pl, m, x + m * 18, y+58));
        }
    }

    public void moveSlot(Slot slot, int x, int y){
        Utils.setFinalInt(slot,"x",x);
        Utils.setFinalInt(slot,"y",y);
    }

    // Shift + Player Inv Slot


    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    public Container getInventory(){
        return inventory;
    }
}
