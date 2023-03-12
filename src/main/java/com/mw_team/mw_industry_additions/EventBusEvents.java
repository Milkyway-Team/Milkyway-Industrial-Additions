package com.mw_team.mw_industry_additions;

import com.mw_team.mw_industry_additions.blocks.recipes.TradeStationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IndustrialAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class EventBusEvents {
    @SubscribeEvent
    public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, TradeStationRecipe.Type.ID, TradeStationRecipe.Type.INSTANCE);
    }
}
