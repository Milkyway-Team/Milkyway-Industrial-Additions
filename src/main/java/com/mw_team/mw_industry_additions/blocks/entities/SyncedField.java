package com.mw_team.mw_industry_additions.blocks.entities;

import com.mw_team.mw_industry_additions.utils.Cons;
import net.minecraft.nbt.*;
import net.minecraftforge.items.*;
import org.mini2Dx.gdx.utils.Array;

/**
 * A field that auto marks the entity as dirty, auto saves and has listeners for changes.
 * @param <T>
 */
public abstract class SyncedField<T>{
    public SyncedField(BasicBlockEntity bbe, String name){
        this.bbe = bbe;
        this.name = name;
    }
    Array<Cons<SyncedField<T>>> listeners = new Array<>();

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

    public void onChanged(){
        if(syncWhenChanged){
           bbe.needsStateUpdate = true;
        }
        if(whenChangedSyncModel){
            bbe.needsModelUpdate = true;
        }
        if(syncImmediatelyOnChange){
            bbe.update();
        }
        listeners.forEach(c->c.get(this));
    }

    public void addListener(Cons<SyncedField<T>> c){
        listeners.add(c);
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
            onChanged();
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
            onChanged();
            return this;
        }

        public SyncedField<Float> add(float value){
            if(value==0){
                return this;
            }
            this.value += value;
            onChanged();
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
            onChanged();
            return this;
        }
    }

    public static class IntArrayField extends ObjectField<int[]>{

        public IntArrayField(BasicBlockEntity bbe, String name, int length){
            super(bbe, name);
            set(new int[length]);
        }

        public void set(int index,int val){
            get()[index] =val;
            onChanged();
        }

        @Override
        public void save(CompoundTag pTag){
            pTag.putIntArray(name, get());
        }

        @Override
        public void load(CompoundTag pTag){
            set(pTag.getIntArray(name));
            onChanged();
        }
    }

    public static class InventoryField<T extends ItemStackHandler> extends ObjectField<T>{

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
