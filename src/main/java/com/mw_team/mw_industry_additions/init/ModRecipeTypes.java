package com.mw_team.mw_industry_additions.init;

import com.mw_team.mw_industry_additions.IndustrialAdditions;
import com.mw_team.mw_industry_additions.blocks.entity.TradeStationRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IndustrialAdditions.MOD_ID);

    public static final RegistryObject<RecipeSerializer<TradeStationRecipe>> TRADING_SERIALIZER =
            SERIALIZERS.register("trading", () -> TradeStationRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
