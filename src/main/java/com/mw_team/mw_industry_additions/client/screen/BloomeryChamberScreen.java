package com.mw_team.mw_industry_additions.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mw_team.mw_industry_additions.blocks.BloomeryChamber;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

public class BloomeryChamberScreen extends AnimatedScreen<BloomeryChamberContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/bloomery_ui.png");
    public BloomeryChamberScreen(BloomeryChamberContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle,100,pmap->{});
    }

    @Override
    public void updateLogic() {

    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {

    }
}
