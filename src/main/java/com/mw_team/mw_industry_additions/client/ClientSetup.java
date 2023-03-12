package com.mw_team.mw_industry_additions.client;

import com.mw_team.mw_industry_additions.blocks.entity.screens.TradeStationScreen;
import com.mw_team.mw_industry_additions.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.trader_menu.get(), TradeStationScreen::new);
    }

}
