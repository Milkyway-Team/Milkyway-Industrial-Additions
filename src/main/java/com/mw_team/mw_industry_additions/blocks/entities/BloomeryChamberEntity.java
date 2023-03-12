package com.mw_team.mw_industry_additions.blocks.entities;


import com.mw_team.mw_industry_additions.blocks.entities.BlockEntityCapabilityManager.SegmentedInventoryCapabilityManager;
import com.mw_team.mw_industry_additions.blocks.entities.SyncedField.*;
import com.mw_team.mw_industry_additions.client.screen.BloomeryChamberContainer;
import com.mw_team.mw_industry_additions.init.*;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler.InventorySegment;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler.SidedSegmentedInvWrapper;
import net.minecraft.core.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraftforge.common.Tags.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.*;
import org.jetbrains.annotations.*;

public class BloomeryChamberEntity extends BasicBlockEntity{

    public class BData{

    }
    public float baseSpeed = 0.1f;
    public float speed = 1;
    /**the furnace increments this by an amount every tick, when it exceeds 1, progress is added to recipes and this is decremented.**/
    SyncedField.FloatField leftOverWork;
    SyncedField.FloatField temperature;
    SyncedField.IntArrayField progress;
    SyncedField.InventoryField inventory;

    // master-slave
    boolean isMaster, hasMaster;

    // input output inventory
    final String inputInvSegment = "input", processingInvSegment = "processing" , outputInvSegment = "output";

    ContainerData tdata = new SimpleContainerData(4);

   // protected final ItemStackHandler inventory; // we will be using master-slave multiblock method.


    public BloomeryChamberEntity(BlockPos pPos, BlockState pBlockState){
        super(ModBlocks.BLOOMERY_CHAMBER_ENTITY.get(), pPos, pBlockState);

        //todo, better to initialise first then insert into array, stops incorrect casting
        syncedFields = new SyncedField[]{
            new SyncedField.FloatField(this,"temperature"),
            new SyncedField.InventoryField(this,"inventory"),
            new SyncedField.IntArrayField(this,"progress"),
            new SyncedField.FloatField(this,"leftOverWork"),
        };
        temperature = (FloatField)syncedFields[0];
        temperature.syncWhenChanged = false;
        inventory = (InventoryField)syncedFields[1];
        inventory.set(createSegmentedInventory(2 + 4 + 2,
                InventorySegment.of(inputInvSegment,0,1).insertableFrom(Direction.WEST),
                InventorySegment.of(processingInvSegment,2,3,4,5),
                InventorySegment.of(outputInvSegment,6,7).extractableFrom(Direction.EAST)
        ));
        progress = (IntArrayField)syncedFields[2];
        progress.syncWhenChanged = false;
        leftOverWork = (FloatField)syncedFields[3];
        leftOverWork.syncWhenChanged = false;

        capabilityManagers = new BlockEntityCapabilityManager[]{
                new SegmentedInventoryCapabilityManager((SegmentedItemStackHandler) inventory.get())
        };
    }

    protected LazyOptional<IItemHandlerModifiable>[] handlers;


    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        BloomeryChamberEntity tile = (BloomeryChamberEntity) be;
        tile.tick();
    }


    public void tick(){
        super.tick();
        speed = Math.min((temperature.get() - 600)/600f,0f);
        leftOverWork.add(speed*baseSpeed);
        while(leftOverWork.getF() > 1){
            /// todo

            leftOverWork.add(-1);
        }

        //inventory.get().getStackInSlot(0).setCount();
    }


    @Override
    public MenuProvider getMenu() {
        return new SimpleMenuProvider((id, playerInv, player) -> new BloomeryChamberContainer(id, playerInv, (SegmentedItemStackHandler) inventory.get(), tdata,this.worldPosition),new TextComponent("bloomery_chamber"));
    }
}
