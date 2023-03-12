package com.mw_team.mw_industry_additions.blocks.recipes;

import net.minecraft.resources.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraftforge.common.util.*;

public class BloomeryRecipe implements Recipe<Container>{

    public final int time;
    public final int minTemp;
    public final Ingredient input;
    public final ItemStack output;

    public BloomeryRecipe(int time, int minTemp, Ingredient input, ItemStack output){
        this.time = time;
        this.minTemp = minTemp;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel){
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer){
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight){
        return false;
    }

    @Override
    public ItemStack getResultItem(){
        return output;
    }

    @Override
    public ResourceLocation getId(){
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer(){
        return null;
    }

    @Override
    public RecipeType<?> getType(){
        return null;
    }
}
