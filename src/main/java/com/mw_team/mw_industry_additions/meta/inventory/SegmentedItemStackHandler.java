package com.mw_team.mw_industry_additions.meta.inventory;

import com.mw_team.mw_industry_additions.utils.Action;
import com.mw_team.mw_industry_additions.utils.Utils;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.*;

import java.util.function.Predicate;

/**
 * A directional, sectioned inventory. Undefined behaviour results when segments overlap.
 */
public class SegmentedItemStackHandler extends ItemStackHandler{

    int[][] directionalSlots = new int[6][];
    Array<InventorySegment> segments = new Array<>();
    Array<Action> segmentListeners  = new Array<>();

    public SegmentedItemStackHandler(int size)
    {
        super(size);
    }
    public SegmentedItemStackHandler(int size,InventorySegment... s)
    {
        super(size);
        setConfig(s);
    }
    public void setConfig(InventorySegment... s){
        for(var segment:s){
            addSegment(segment);
        }
    }

    public void addSegment(InventorySegment is){
        if(segments == null){
            setConfig();
        }
        segments.add(is);
        is.inventory = this;
        segmentChanged();
    }

    public InventorySegment getSegment(String name){
        for(InventorySegment segment : segments){
            if(segment.name.equals(name)){
                return segment;
            }
        }
        return null;
    }

    public void segmentChanged(){
        segmentListeners.forEach(Action::invoke);
    }

    public InventorySegment getSegment(int slot){
        for(InventorySegment segment : segments){
            for(Integer i : segment.slots){
                if(i == slot)
                    return segment;
            }
        }
        return null;
    }

    public ItemStack insertIntoSegment(InventorySegment segment, ItemStack stack){
        ItemStack stackcpy = stack.copy();
        for(int i : segment.slots){
            int left = insertItem(i, stackcpy, false).getCount();
            stackcpy.setCount(left);
            if(left == 0){
                break;
            }
        }
        return stackcpy;
    }

    public void transferIntoSegment(InventorySegment segment, int slot){
        ItemStack is = getStackInSlot(slot);
        is.setCount(insertIntoSegment(segment, is).getCount());
        if(getSegment(slot).syncOnChange){
            onContentsChanged(slot);
        }
    }


    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir){
        for(InventorySegment segment : segments){
            if(dir == null || segment.insertFrom[dir.ordinal()]){
                for(Integer i : segment.slots)
                    if(i == slot)
                        return true;
            }
        }
        return false;
    }


    public boolean canExtract(int slot, ItemStack stack, Direction dir){
        for(InventorySegment segment : segments){
            if(dir == null || segment.extractFrom[dir.ordinal()]){
                for(Integer i : segment.slots)
                    if(i == slot)
                        return true;
            }
        }
        return false;
    }

    /**
     * Do not modify output
     * @param dir
     * @return
     */
    public int[] slotsOfSide(Direction dir){
        int dirid = dir.ordinal();
        if(directionalSlots[dirid] == null){
            generateDirectionalSlots(dirid);
        }
        return directionalSlots[dirid];
    }

    private void generateDirectionalSlots(int dirid){
        IntArray slots = new IntArray();
        for(InventorySegment segment : segments){
            if(segment.insertFrom[dirid] || segment.extractFrom[dirid]){
                slots.addAll(segment.slots);
            }
        }
        directionalSlots[dirid] = slots.toArray();
    }

    public boolean hasCapabilityInDirection(Direction dir){
        return slotsOfSide(dir).length>0;
    }



    public static class SidedSegmentedInvWrapper implements IItemHandlerModifiable{
        protected final SegmentedItemStackHandler inv;
        protected final Direction[] side;

        int[] slots;

        public SidedSegmentedInvWrapper(SegmentedItemStackHandler inv, Direction[] side){
            this.inv = inv;
            this.side = side;
            inv.segmentListeners.add(this::recalculateSlots);
            recalculateSlots();
        }

        public static LazyOptional<IItemHandlerModifiable> create(SegmentedItemStackHandler inv, Direction... sides){
            return LazyOptional.of(() -> new SidedSegmentedInvWrapper(inv, sides));
        }

        public void recalculateSlots(){
            boolean[] slotsOpen = new boolean[inv.getSlots()];
            for(Direction direction : side){
                slots = inv.slotsOfSide(direction);
                for(int i = 0;i<slots.length;i++){
                    slotsOpen[slots[i]] = true;
                }
            }
            slots = new int[Utils.Count(slotsOpen,true)];
            int index = 0;
            for(int i = 0;i<slotsOpen.length;i++){
                if(slotsOpen[i]){
                    slots[index++] = i;
                }
            }
        }

        public static int arrayBoundsCheck(int[] arr, int index){
            if(index < 0 || index >= arr.length){
                return -1;
            }
            return arr[index];
        }

        public int getSlot(int relativeSlot){
            return arrayBoundsCheck(slots,relativeSlot);
        }

        @Override
        public int getSlots(){
            return slots.length;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot){
            return inv.getStackInSlot(getSlot(slot));
        }
        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack){
            inv.setStackInSlot(getSlot(slot), stack);
        }
        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
            if(stack.isEmpty() || slot < 0){
                return ItemStack.EMPTY;
            }
            int actualslot = getSlot(slot);
            if(actualslot< 0 || actualslot > inv.getSlots()){
                return stack;
            }
            return inv.insertItem(actualslot,stack,simulate);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if(amount == 0 || slot < 0){
                return ItemStack.EMPTY;
            }
            int actualslot = getSlot(slot);
            if(actualslot< 0 || actualslot > inv.getSlots()){
                return ItemStack.EMPTY;
            }
            return inv.extractItem(actualslot,amount,simulate);
        }

        @Override
        public int getSlotLimit(int slot){
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack){
            return true;
        }
    }

    public static class InventorySegment{
        SegmentedItemStackHandler inventory;
        public String name;
        public int[] slots;
        boolean[] insertFrom = new boolean[6], extractFrom = new boolean[6];
        int maxItems = 64;
        boolean syncOnChange = false;

        public static InventorySegment of(String name, int... slots){
            InventorySegment is = new InventorySegment();
            is.name = name;
            is.slots = slots;
            return is;
        }

        public static InventorySegment ofRange(String name, int slotFrom, int slotTo){
            InventorySegment is = new InventorySegment();
            is.name = name;
            is.slots = new int[slotTo - slotFrom + 1];
            for(int i = slotFrom; i <= slotTo; i++){
                is.slots[i - slotFrom] = i;
            }
            return is;
        }

        public int getFirstSlot(Predicate<ItemStack> cond){
            for(int i = 0; i < slots.length; i++){
                ItemStack stack = inventory.getStackInSlot(slots[i]);
                if(!stack.isEmpty() && cond.test(stack)){
                    return slots[i];
                }
            }
            return -1;
        }

        public int getAnyFilledSlot(){
            return getFirstSlot(i -> true);
        }


        public InventorySegment maxItems(int maxItems){
            this.maxItems = maxItems;
            return this;
        }

        public InventorySegment insertableFrom(Direction... directions){
            for(Direction d : directions){
                insertFrom[d.ordinal()] = true;
            }
            if(inventory!=null)
                inventory.segmentChanged();
            return this;
        }

        public InventorySegment universallyInsertable(){
            for(int i = 0; i < 6; i++){
                insertFrom[i] = true;
            }
            if(inventory!=null)
                inventory.segmentChanged();
            return this;
        }

        public InventorySegment extractableFrom(Direction... directions){
            for(Direction d : directions){
                extractFrom[d.ordinal()] = true;
            }
            if(inventory!=null)
                inventory.segmentChanged();
            return this;
        }

        public InventorySegment universallyExtractable(){
            for(int i = 0; i < 6; i++){
                extractFrom[i] = true;
            }
            if(inventory!=null)
                inventory.segmentChanged();
            return this;
        }

        public InventorySegment syncOnChange(boolean b){
            this.syncOnChange = b;
            return this;
        }
    }
}
