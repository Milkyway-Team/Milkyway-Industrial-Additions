package com.mw_team.mw_industry_additions.blocks.entities;

import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.*;

public class BasicBlockEntity extends BlockEntity{
    protected boolean needsModelUpdate = false;
    protected boolean needsStateUpdate = false;
    SyncedField[] syncedFields;
    BlockEntityCapabilityManager[] capabilityManagers;

    public BasicBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState){
        super(pType, pPos, pBlockState);
    }

    //save load
    @Override
    protected void saveAdditional(CompoundTag pTag){
        super.saveAdditional(pTag);
        for(int i = 0;i<syncedFields.length;i++){
            syncedFields[i].save(pTag);
        }
    }

    @Override
    public void load(CompoundTag pTag){
        super.load(pTag);
        for(int i = 0;i<syncedFields.length;i++){
           syncedFields[i].load(pTag);
        }
    }

    /// sync on block update
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getTag());
    }


    //sync on chunkload
    @Override
    public CompoundTag getUpdateTag(){
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag){
        super.handleUpdateTag(tag);
        load(tag);
    }

    public void update(){
        if(needsStateUpdate && level != null){
            setChanged();
            if(level != null){
                level.setBlockAndUpdate(this.worldPosition, getBlockState());
            }
            needsStateUpdate = false;
        }
        if(needsModelUpdate && level != null){
            requestModelDataUpdate();
            needsModelUpdate = false;
        }
    }

    public void tick(){
        update();
    }


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(!remove) {
            for (var capmanager : capabilityManagers) {
                var d = capmanager.getCap(cap, side);
                if (d != null) {
                    return d;
                }
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        for(var cap:capabilityManagers){
            cap.invalidate();
        }
    }


    //helper

    protected ItemStackHandler createInventory(int size){
        return new ItemStackHandler(size){
            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate){
                needsStateUpdate = true;
                BasicBlockEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
                needsStateUpdate = true;
                BasicBlockEntity.this.update();
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    protected SegmentedItemStackHandler createSegmentedInventory(int size, SegmentedItemStackHandler.InventorySegment... s){
        return new SegmentedItemStackHandler(size,s){
            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate){
                needsStateUpdate = true;
                BasicBlockEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
                needsStateUpdate = true;
                BasicBlockEntity.this.update();
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public MenuProvider getMenu(){
        return null;
    }


}
