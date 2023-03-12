package com.mw_team.mw_industry_additions.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mw_team.mw_industry_additions.blocks.BloomeryChamber;
import com.mw_team.mw_industry_additions.utils.animation.Interpolations.*;
import com.mw_team.mw_industry_additions.utils.animation.StateMap;
import com.mw_team.mw_industry_additions.utils.animation.StateMap.AnimationState;
import com.mw_team.mw_industry_additions.utils.ui.TextureRegion;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

public class BloomeryChamberScreen extends AnimatedScreen<BloomeryChamberContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/bloomery_ui.png");

    TextureRegion mainbg;

    public static final String STATE_OPENING = "opening";
    public static final String STATE_IDLE= "idle";
    public BloomeryChamberScreen(BloomeryChamberContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle,pmap->{});

        stateMap.addState(AnimationState.get(STATE_OPENING).loops(false).onInit(pmap -> {
            pmap.f("x_offset",-50);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL,5f,"x_offset",new FloatInterpolate(),0f);
        }));
        stateMap.addState(AnimationState.get(STATE_IDLE).loops(true).duration(1000).needsToComplete(false).onInit(pmap -> { }));

        stateMap.onStateEnd = stateMap -> {
            if(stateMap.getCurrent()==null){
                return STATE_OPENING;
            }
            switch(stateMap.getCurrent().getName()){
                case STATE_OPENING:
                    return STATE_IDLE;
                default:
                    return STATE_IDLE;
            }
        };
        imageWidth = 176;
        imageHeight = 194;
    }

    @Override
    protected void init() {
        super.init();
        mainbg = new TextureRegion(0,0,176,194,TEXTURE);
    }

    @Override
    public void updateLogic() {

    }

    @Override
    protected void renderBg(PoseStack matrices, float pPartialTick, int pMouseX, int pMouseY) {
        reset();

        int ox = (width - imageWidth) / 2;
        int oy = (height - imageHeight) / 2;
        matrices.pushPose();
        matrices.translate(ox+stateMap.f("x_offset"),oy,0);
        drawTextureRegion(matrices,mainbg,0,0);

        matrices.popPose();
        RenderSystem.disableBlend();

    }
}
