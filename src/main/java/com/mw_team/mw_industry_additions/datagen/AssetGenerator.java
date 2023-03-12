package com.mw_team.mw_industry_additions.datagen;

import net.minecraft.data.*;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.*;
import net.minecraftforge.forge.event.lifecycle.*;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public class AssetGenerator{

    public static void main(String[] args){
        System.out.println("hi!");
        // do other generation here.
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        System.out.println("Doing data gen");
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(new ModBlockTagsProvider(generator,MOD_ID,existingFileHelper));
        generator.addProvider(new ModBlockStateProvider(generator,MOD_ID,existingFileHelper));
        generator.addProvider(new ModItemModelProvider(generator,MOD_ID,existingFileHelper));
    }
}
