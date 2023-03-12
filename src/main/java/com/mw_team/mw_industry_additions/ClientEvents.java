package com.mw_team.mw_industry_additions;

import com.mw_team.mw_industry_additions.client.screen.BloomeryChamberScreen;
import com.mw_team.mw_industry_additions.init.ModBlocks;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents(){}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        MenuScreens.register(ModBlocks.BLOOMERY_CHAMBER_CONTAINER.get(), BloomeryChamberScreen::new);
    }
}
