package com.mw_team.mw_industry_additions.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mw_team.mw_industry_additions.utils.Cons;
import com.mw_team.mw_industry_additions.utils.Mathf;
import com.mw_team.mw_industry_additions.utils.Utils;
import com.mw_team.mw_industry_additions.utils.animation.ParameterMap;
import com.mw_team.mw_industry_additions.utils.animation.StateMap;
import com.mw_team.mw_industry_additions.utils.ui.SpriteDrawer;
import com.mw_team.mw_industry_additions.utils.ui.TextureRegion;
import com.mw_team.mw_industry_additions.utils.ui.UITextures;
import com.mw_team.mw_industry_additions.utils.ui.components.UIHandler;
import com.mw_team.mw_industry_additions.utils.ui.particles.UIParticleSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class AnimatedScreen <T extends InventoryContainerMenu> extends AbstractContainerScreen<T> {
    float tick=0;
    private long prevTime = 0;
    public StateMap stateMap;
    public UIParticleSystem particles;
    public UIHandler ui;
    public SpriteDrawer spriteDrawer;
    public AnimatedScreen(T pMenu, Inventory pPlayerInventory, Component pTitle,Cons<ParameterMap> cons) {
        super(pMenu, pPlayerInventory, pTitle);
        stateMap = new StateMap((a)->null,pmap -> {
            pmap.add("x_offset",0f);
            pmap.add("y_offset", 0f);
            cons.get(pmap);
        });
        particles = new UIParticleSystem();
        ui = new UIHandler(this);
        spriteDrawer = new SpriteDrawer(new PoseStack(),this);
    }

    @Override
    protected void init() {
        super.init();
        prevTime = System.currentTimeMillis();
    }

    public abstract void updateLogic();

    public int ox,oy, originX,originY;
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta){
        tick += (System.currentTimeMillis()-prevTime)/1000f;
        prevTime = System.currentTimeMillis();
        spriteDrawer.reset(matrices);
        if(ox==0||oy==0){
            ox=leftPos;oy=topPos;
        }
        originX = (width - imageWidth) / 2;
        originY = (height - imageHeight) / 2;
        particles.update();
        ui.update();
        updateLogic();
        stateMap.update();
        leftPos = ox+(int)stateMap.f("x_offset");
        topPos = oy+(int)stateMap.f("y_offset");

        renderBackground(matrices);
        spriteDrawer.resetColor();
        spriteDrawer.resetTex();
        matrices.pushPose();
        matrices.translate(leftPos+stateMap.f("x_offset"),topPos,0);
        spriteDrawer.reset(matrices);
        spriteDrawer.begin();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, UITextures.TEXTURE);
        ui.draw();
        spriteDrawer.end();
        matrices.popPose();

        super.render(matrices, mouseX, mouseY, delta);
        renderTop();

        renderTooltip(matrices, mouseX, mouseY);
    }

    public void renderTop(){

    }
    //fuck you handled screen




    public void drawItem(ItemStack stack, float x, float y, String amountText) {
        drawItem(stack,x,y,200,amountText);
    }
    //this.renderItem(stack, ModelTransformation.Mode.GUI, false, PoseStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
    public void drawItem(ItemStack stack, float x, float y, float z, String amountText) {
        this.setBlitOffset((int)z);
        this.itemRenderer.blitOffset = z;
        this.itemRenderer.renderAndDecorateItem(stack, (int)x, (int)y);
        this.itemRenderer.renderGuiItemDecorations(this.font, stack, (int)x, (int)y, amountText);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
    }
    static final Vector3f topLeftLight = Util.make(new Vector3f(-1.0F, -0.7F, 0.2F), Vector3f::normalize);
    static final Vector3f topDownLight = Util.make(new Vector3f(-0.2F, -1F, 0.7F), Vector3f::normalize);

    public void drawItem(PoseStack matrices, ItemStack stack, float x, float y, float z, String amountText) {

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        resetBlendMode();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pushPose();
        matrices.translate(x, y, z);
        matrices.scale(16, -16, 0.1f);
        RenderSystem.applyModelViewMatrix();
        var immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        var model = itemRenderer.getModel(stack, null, null, 0);
        boolean bl = !model.usesBlockLight();
        if(bl){
            Lighting.setupForFlatItems();
        }else{
            RenderSystem.setShaderLights(topLeftLight, topDownLight);
        }
        this.itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, matrices, immediate, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY, model);
        immediate.endBatch();
        RenderSystem.enableDepthTest();
        if(bl){
            Lighting.setupFor3DItems();
        }
        matrices.popPose();


        if(amountText.length()>0){
            matrices.pushPose();
            matrices.translate(0, 0, z + 1);
            this.font.drawShadow(matrices, amountText, x + 2 - (amountText.length() - 1) * 8, y + 1, Utils.rgb(255, 255, 255));
            matrices.popPose();
        }
        //DiffuseLighting.method_34742();

    }
    public void resetBlendMode(){
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
    public void additiveBlendMode(){
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }

    public void reset(){
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
    }

    public void alpha(float alpha){
        float[] c = RenderSystem.getShaderColor();
        RenderSystem.setShaderColor(c[0], c[1], c[2], alpha);
    }

    public void rotate(PoseStack matrices, float angle){
        matrices.mulPose(Vector3f.ZP.rotationDegrees(angle));
    }
    public void drawSection(PoseStack matrices,float x,float y,float u,float v,float uw,float vh, float sx0, float sx1, float sy0, float sy1){
        sx0 = Mth.clamp(sx0,0,1);
        sx1 = Mth.clamp(sx1,0,1);
        sy0 = Mth.clamp(sy0,0,1);
        sy1 = Mth.clamp(sy1,0,1);
        blit(matrices, (int)(x+sx0*uw), (int)(y+sy0*vh), (int)(u+sx0*uw), (int)(v+sy0*vh), (int)(uw * (sx1-sx0)), (int)(vh * (sy1-sy0)));
    }

    public void drawTextureRegion(PoseStack matrices, TextureRegion tr, int x,int y){
        if(tr.getTexid()!=RenderSystem.getTextureId(0)){
            RenderSystem.setShaderTexture(0,tr.texture);
        }
        var tex  = tr.rawTex;
        blit(matrices, x, y, (int)(tr.x), (int)(tr.y), (int)(tr.w), (int)(tr.h));
    }

    public void drawTexturedQuadWH(PoseStack matrices, float[][] pos, float u, float v, float regionWidth, float regionHeight) {
        drawTexturedQuadWH(matrices,pos,u,v,regionWidth,regionHeight,256,256);
    }
    public void drawTexturedQuadWH(PoseStack matrices, float[][] pos, float u, float v, float regionWidth, float regionHeight, float tw, float th) {
        drawTexturedQuad(matrices.last().pose(), pos,u/tw,(u+regionWidth)/tw,v/th,(v+regionHeight)/th);
    }
    public void drawTexturedQuad(Matrix4f matrices, float[][] pos, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrices, pos[0][0], pos[0][1], 0).uv(u0, v1).endVertex();
        bufferBuilder.vertex(matrices, pos[1][0], pos[1][1], 0).uv(u1, v1).endVertex();
        bufferBuilder.vertex(matrices, pos[2][0], pos[2][1], 0).uv(u1, v0).endVertex();
        bufferBuilder.vertex(matrices, pos[3][0], pos[3][1], 0).uv(u0, v0).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
    }

    public void drawArc(PoseStack matrices, float x,float y,float r,float t,float fromA,float toA, float u, float v, float regionWidth, float regionHeight) {
        drawArc(matrices.last().pose(), x,y,r,t,fromA,toA, 180f/r,u/256f,(u+regionWidth)/256f,v/256f,(v+regionHeight)/256f );
    }
    public void drawArc(Matrix4f matrices, float x, float y, float r, float t, float fromA, float toA, float segsize, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float[] prevOuter = new float[2],prevInner = new float[2];
        float innerR = r-t;
        prevOuter[0] = Mathf.sinDeg(fromA)*r+x;
        prevOuter[1] = Mathf.cosDeg(fromA)*r+y;
        prevInner[0] = Mathf.sinDeg(fromA)*innerR+x;
        prevInner[1] = Mathf.cosDeg(fromA)*innerR+y;
        for(float i = fromA;i<toA;i+=segsize){
            float fa = Math.min(toA,i+segsize);
            float s = Mathf.sinDeg(fa);
            float c = Mathf.cosDeg(fa);
            bufferBuilder.vertex(matrices, prevOuter[0],  prevOuter[1],  0).uv(u0, v1).endVertex();
            bufferBuilder.vertex(matrices, s*r+x,     c*r+y,     0).uv(u1, v1).endVertex();
            bufferBuilder.vertex(matrices, s*innerR+x,c*innerR+y,0).uv(u1, v0).endVertex();
            bufferBuilder.vertex(matrices, prevInner[0],  prevInner[1],  0).uv(u0, v0).endVertex();

            prevOuter[0] = s*r+x;
            prevOuter[1] = c*r+y;
            prevInner[0] = s*innerR+x;
            prevInner[1] = c*innerR+y;

        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
    }

    public Minecraft getClient(){
        return this.minecraft;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(this.getSlotAt(mouseX, mouseY)==null){
            return ui.mouseClicked((float)mouseX - leftPos, (float)mouseY - topPos, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        ui.mouseDragged(mouseX-leftPos, mouseY-topPos, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        if(this.getSlotAt(mouseX, mouseY)==null){
            ui.mouseReleased(mouseX - leftPos, mouseY - topPos, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount){
        ui.mouseScrolled(mouseX-leftPos, mouseY-topPos, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        ui.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        ui.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers){
        ui.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    public Font getTextRenderer(){
        return this.font;
    }


    @Override
    public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY){
        return super.getChildAt(mouseX,mouseY);
    }

    private Slot getSlotAt(double x, double y) {
        for(int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = (Slot)this.menu.slots.get(i);
            if (this.isPointOverSlot(slot, x, y) && slot.isActive()) {
                return slot;
            }
        }
        return null;
    }
    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return this.isHovering(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    public <T extends InventoryContainerMenu> T getInvScreenHandler(){
        return (T)super.getMenu();
    }
}
