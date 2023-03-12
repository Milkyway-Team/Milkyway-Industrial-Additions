package com.mw_team.mw_industry_additions.client;

import com.mw_team.mw_industry_additions.client.screen.TradeStationScreen;
import com.mw_team.mw_industry_additions.client.screen.BloomeryChamberScreen;
import com.mw_team.mw_industry_additions.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.trader_menu.get(), TradeStationScreen::new);
        MenuScreens.register(ModMenuTypes.BLOOMERY_CHAMBER_CONTAINER.get(), BloomeryChamberScreen::new);
    }

}
