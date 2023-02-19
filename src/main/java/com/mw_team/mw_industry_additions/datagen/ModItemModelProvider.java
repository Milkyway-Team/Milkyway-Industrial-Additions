package com.mw_team.mw_industry_additions.datagen;

import com.mojang.logging.*;
import com.mw_team.mw_industry_additions.init.*;
import net.minecraft.data.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.*;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

public class ModItemModelProvider extends ItemModelProvider{
    public ModItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper){
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels(){
        for(var entry: ModItems.models){
            entry.value.apply(entry.key.get(),this);
        }
    }

    public ItemModelBuilder handheldItem(Item item){
        return withExistingParent(item.getRegistryName().getPath(),
        new ResourceLocation("item/handheld")).texture("layer0",
        new ResourceLocation(MOD_ID,"item/"+item.getRegistryName().getPath()));
    }
    public ItemModelBuilder blockItem(Item item){
        if(item instanceof BlockItem bi){
            return withExistingParent(bi.getBlock().getRegistryName().getPath(),
            modLoc("block/"+bi.getBlock().getRegistryName().getPath()));
        }
        LogUtils.getLogger().error(item.toString()+" is not a Block Item and cant be used as a model");
        return null;
    }
}
