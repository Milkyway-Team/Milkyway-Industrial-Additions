package com.mw_team.mw_industry_additions.blocks.entities;

import net.minecraft.nbt.*;
import net.minecraftforge.items.*;

public abstract class SyncedField<T>{
    public SyncedField(BasicBlockEntity bbe, String name){
        this.bbe = bbe;
        this.name = name;
    }

    BasicBlockEntity bbe;

    public String name;

    public boolean syncWhenChanged = true;
    public boolean syncConstantly = false;
    public boolean syncImmediatelyOnChange = false;

    public boolean whenChangedSyncModel = false;

    public abstract void save(CompoundTag pTag);

    public abstract void load(CompoundTag pTag);

    public abstract T get();

    public abstract SyncedField<T> set(T value);

    public void OnChanged(){
        if(syncWhenChanged){
           bbe.needsStateUpdate = true;
        }
        if(whenChangedSyncModel){
            bbe.needsModelUpdate = true;
        }
        if(syncImmediatelyOnChange){
            bbe.update();
        }
    }

    public static class FloatField extends SyncedField<Float>{
        private float value;

        public FloatField(BasicBlockEntity bbe, String name){
            super(bbe, name);
        }

        @Override
        public void save(CompoundTag pTag){
            pTag.putFloat(name,value);
        }

        @Override
        public void load(CompoundTag pTag){
            value = pTag.getFloat(name);
        }

        public Float get(){
            return value;
        }

        public float getF(){
            return value;
        }

        @Override
        public SyncedField<Float> set(Float value){
            if(value==this.value){
                return this;
            }
            this.value = value;
            OnChanged();
            return this;
        }

        public SyncedField<Float> add(float value){
            if(value==0){
                return this;
            }
            this.value += value;
            OnChanged();
            return this;
        }
    }


    public static abstract class ObjectField<T> extends SyncedField<T>{
        private T value;

        public ObjectField(BasicBlockEntity bbe, String name){
            super(bbe, name);
        }

        public T get(){
            return value;
        }

        public SyncedField<T> set(T value){
            if(value==this.value){
                return this;
            }
            this.value = value;
            OnChanged();
            return this;
        }
    }

    public static class IntArrayField extends ObjectField<int[]>{

        public IntArrayField(BasicBlockEntity bbe, String name){
            super(bbe, name);
        }

        @Override
        public void save(CompoundTag pTag){
            pTag.putIntArray(name, get());
        }

        @Override
        public void load(CompoundTag pTag){
            set(pTag.getIntArray(name));
        }
    }

    public static class InventoryField extends ObjectField<ItemStackHandler>{

        public InventoryField(BasicBlockEntity bbe, String name){
            super(bbe, name);
        }

        @Override
        public void save(CompoundTag pTag){
            pTag.put(name, get().serializeNBT());
        }

        @Override
        public void load(CompoundTag pTag){
            this.get().deserializeNBT(pTag.getCompound(name));
        }
    }
}
