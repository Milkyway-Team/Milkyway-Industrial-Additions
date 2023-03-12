package com.mw_team.mw_industry_additions.init;

import com.mw_team.mw_industry_additions.IndustrialAdditions;
import com.mw_team.mw_industry_additions.blocks.entity.screens.TradeStationMenu;
import com.mw_team.mw_industry_additions.client.screen.BloomeryChamberContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, IndustrialAdditions.MOD_ID);

    public static final RegistryObject<MenuType<TradeStationMenu>> trader_menu =
            registerMenuType(TradeStationMenu::new, "trade_station_menu");
    public static final RegistryObject<MenuType<BloomeryChamberContainer>> BLOOMERY_CHAMBER_CONTAINER
            = MENUS.register("bloomery_chamber", ()-> new MenuType<>(BloomeryChamberContainer::new));



    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                 String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
