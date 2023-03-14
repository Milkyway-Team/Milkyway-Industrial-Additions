package com.mw_team.mw_industry_additions.blocks.entities;


import com.mojang.logging.LogUtils;
import com.mw_team.mw_industry_additions.blocks.entities.BlockEntityCapabilityManager.SegmentedInventoryCapabilityManager;
import com.mw_team.mw_industry_additions.client.screen.BloomeryChamberMenu;
import com.mw_team.mw_industry_additions.init.*;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentedItemStackHandler.InventorySegment;
import net.minecraft.core.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.*;

import java.util.Arrays;

public class BloomeryChamberEntity extends BasicBlockEntity {

    public class BData {

    }

    public float baseSpeed = 1f;
    public float speed = 1;
    /**
     * the furnace increments this by an amount every tick, when it exceeds 1, progress is added to recipes and this is decremented.
     **/
    public SyncedField.FloatField leftOverWork;
    public SyncedField.FloatField temperature;
    public SyncedField.IntArrayField progress;
    public SyncedField.IntArrayField state;

    public SyncedField.InventoryField<SegmentedItemStackHandler> inventory;

    Recipe[] currentRecipe;
    int[] recipeTime;

    // master-slave
    boolean isMaster, hasMaster;

    // input output inventory
    public static final String inputInvSegment = "input", processingInvSegment = "processing", outputInvSegment = "output";

    // property delegate to screen:
    ContainerData tdata = new SimpleContainerData(6);
    public static final int TEMP_DATA = 0; //temp
    public static final int BURN_DATA = 1; // whether fuel is burning and how efficient
    public static final int PROGRESS_DATA = 2; // item progress 0 - 100, -1 for not running

    private static final int STATE_WAIT = 0, STATE_PROCESS = 1, STATE_OUT = 2;

    public BloomeryChamberEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.BLOOMERY_CHAMBER_ENTITY.get(), pPos, pBlockState);

        temperature = new SyncedField.FloatField(this, "temperature");
        temperature.syncWhenChanged = false;
        temperature.addListener(t -> {
            tdata.set(TEMP_DATA, t.get().intValue());
        });


        inventory = new SyncedField.InventoryField(this, "inventory");
        inventory.set(createSegmentedInventory(2 + 4 + 2,
                InventorySegment.of(inputInvSegment, 0, 1).universallyInsertable(),
                InventorySegment.of(processingInvSegment, 2, 3, 4, 5).maxItems(1).canManuallyInsert(false),
                InventorySegment.of(outputInvSegment, 6, 7).universallyExtractable().canManuallyInsert(false)
        ));
        inventory.get().getSegment(processingInvSegment).addListener(slot -> {
            if(level!=null && !level.isClientSide) {
                currentRecipe[slot] = null;
                progress.set(slot, 0);
            }
        });

        progress = new SyncedField.IntArrayField(this, "progress", inventory.get().getSegment(processingInvSegment).size());
        progress.addListener(p -> {
            for (int i = 0; i < p.get().length; i++) {
                int val = currentRecipe[i] == null ? -1 : (100 * p.get()[i]) / (recipeTime[i]);
                tdata.set(PROGRESS_DATA + i, val);
            }
        });

        leftOverWork = new SyncedField.FloatField(this, "leftOverWork");
        leftOverWork.syncWhenChanged = false;

        state = new SyncedField.IntArrayField(this, "state", inventory.get().getSegment(processingInvSegment).size());

        syncedFields = new SyncedField[]{
                temperature,
                inventory,
                progress,
                leftOverWork,
                state
        };

        capabilityManagers = new BlockEntityCapabilityManager[]{
                new SegmentedInventoryCapabilityManager(inventory.get())
        };
        currentRecipe = new Recipe[4];
        recipeTime = new int[4];
        Arrays.fill(recipeTime, 9999);

    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        BloomeryChamberEntity tile = (BloomeryChamberEntity) be;
        tile.tick();
    }


    public void updateServer() {
        //todo: remove when testing done
        temperature.set(600f);

        speed = Math.max((temperature.get() - 400) / 400f, 0f);
        leftOverWork.add(speed * baseSpeed);

        Container smeltingRecipeWrapper = new SimpleContainer(1);

        SegmentedItemStackHandler inv = inventory.get();
        InventorySegment input = inv.getSegment(inputInvSegment);
        InventorySegment proc = inv.getSegment(processingInvSegment);
        InventorySegment output = inv.getSegment(outputInvSegment);
        //finding stuff to process
        if (!proc.isFull()) {
            for (int i = 0; i < input.slots.length; i++) {
                smeltingRecipeWrapper.setItem(0, input.getItemInSlot(i));
                Recipe<?> iRecipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, smeltingRecipeWrapper, this.level).orElse(null);
                if (iRecipe == null) {
                    continue;
                }
                inv.transferSlotIntoSegment(proc, input.slots[i]);

            }
        }
        //processing stuff
        boolean progressChanged = false;
        while (leftOverWork.getF() > 1) {
            for (int i = 0; i < proc.slots.length; i++) {
                if (proc.getItemInSlot(i).isEmpty()) {
                    currentRecipe[i] = null;
                    recipeTime[i] = 999;
                    state.set(i, STATE_WAIT);
                    continue;
                }
                switch (state.get()[i]) {
                    case STATE_WAIT:
                        if (!proc.getItemInSlot(i).isEmpty()) {
                            currentRecipe[i] = findRecipe(smeltingRecipeWrapper, proc.getItemInSlot(i));
                            if (currentRecipe[i] != null) {
                                recipeTime[i] = currentRecipe[i] instanceof SmeltingRecipe sr ? sr.getCookingTime() : 1;
                            } else {
                                continue;
                            }
                            progress.get()[i] = 0;
                            progressChanged = true;
                            state.set(i, STATE_PROCESS);
                        }
                        break;
                    case STATE_PROCESS:
                        if (currentRecipe[i] == null || progress.get()[i] == recipeTime[i] - 1) {
                            currentRecipe[i] = findRecipe(smeltingRecipeWrapper, proc.getItemInSlot(i));
                            if (currentRecipe[i] == null) {
                                state.set(i, STATE_WAIT);
                                continue;
                            }
                            if (currentRecipe[i] != null) {
                                recipeTime[i] = currentRecipe[i] instanceof SmeltingRecipe sr ? sr.getCookingTime() : 1;
                            }
                        }
                        progress.get()[i]++;
                        progressChanged = true;
                        if (progress.get()[i] >= recipeTime[i]) {
                            inv.setStackInSlot(proc.slots[i], currentRecipe[i].getResultItem().copy());
                            state.set(i, STATE_OUT);
                        }
                        break;
                    case STATE_OUT:
                        if (inv.transferSlotIntoSegment(output, proc.slots[i]).isEmpty()) {
                            currentRecipe[i] = null;
                            recipeTime[i] = 999;
                            progress.set(i, 0);
                            state.set(i, STATE_WAIT);
                        }
                        break;
                }
            }
            leftOverWork.add(-1);
        }
        if (progressChanged) {
            progress.onChanged();
        }
        //inventory.get().getStackInSlot(0).setCount();
    }

    public Recipe<?> findRecipe(Container temp, ItemStack is) {
        temp.setItem(0, is);
        Recipe<?> iRecipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, temp, this.level).orElse(null);
        return iRecipe;
    }


    @Override
    public MenuProvider getMenu() {
        return new SimpleMenuProvider((id, playerInv, player) -> new BloomeryChamberMenu(id, playerInv, tdata, this.worldPosition), new TextComponent("bloomery_chamber"));
    }
}
