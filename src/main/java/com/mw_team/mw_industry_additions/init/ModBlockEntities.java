package com.mw_team.mw_industry_additions.init;

import com.mw_team.mw_industry_additions.IndustrialAdditions;
import com.mw_team.mw_industry_additions.blocks.entities.TradeStationEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, IndustrialAdditions.MOD_ID);

   public static final RegistryObject<BlockEntityType<TradeStationEntity>> trade_entity =
           BLOCK_ENTITIES.register("trade_station_block_entity", () ->
                   BlockEntityType.Builder.of(TradeStationEntity::new,
                           ModBlocks.TRADE_STATION.get()).build(null));
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
