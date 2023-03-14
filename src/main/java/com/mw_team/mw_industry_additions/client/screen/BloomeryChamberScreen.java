package com.mw_team.mw_industry_additions.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mw_team.mw_industry_additions.blocks.entities.BloomeryChamberEntity;
import com.mw_team.mw_industry_additions.meta.inventory.SegmentSlot;
import com.mw_team.mw_industry_additions.utils.Mathf;
import com.mw_team.mw_industry_additions.utils.Utils;
import com.mw_team.mw_industry_additions.utils.animation.FrameState;
import com.mw_team.mw_industry_additions.utils.animation.Interpolations.*;
import com.mw_team.mw_industry_additions.utils.animation.StateMap.AnimationState;
import com.mw_team.mw_industry_additions.utils.ui.TextureRegion;
import com.mw_team.mw_industry_additions.utils.ui.components.UIMechanicalCounter;
import com.mw_team.mw_industry_additions.utils.ui.particles.DistortableUIParticle;
import com.mw_team.mw_industry_additions.utils.ui.particles.ParticleDeleter;
import com.mw_team.mw_industry_additions.utils.ui.particles.ParticleWindAffector;
import com.mw_team.mw_industry_additions.utils.ui.particles.UIParticle;
import com.mw_team.mw_industry_additions.utils.ui.particles.UIParticle.RenderAffectors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.Vec2;
import org.mini2Dx.gdx.utils.Array;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

public class BloomeryChamberScreen extends AnimatedScreen<BloomeryChamberMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/bloomery_ui.png");

    TextureRegion mainbg,progressRing,processingGlow, tempbarback,tempBar,inputSlot,processingSlotOn,processingSlotOff,furnaceglow;
    TextureRegion[] spark = new TextureRegion[3];
    Slot[] processingSlots;
    float[] glowStrength;
    float mainGlow = 0;

    UIMechanicalCounter temperatureCounter;

    public static final String STATE_OPENING = "opening";
    public static final String STATE_IDLE= "idle";
    public BloomeryChamberScreen(BloomeryChamberMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle,pmap->{
            pmap.add("temp_x_offset",50f);
        });

        //todo: find better way to manage state maps

        stateMap.addState(AnimationState.get(STATE_OPENING).loops(false).onInit(pmap -> {
            pmap.f("x_offset",-50);
            pmap.f("temp_x_offset",50);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL,5f,"x_offset",new FloatInterpolate(),0f);
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"temp_x_offset",new FloatInterpolate(),
                    FrameState.get(15f,0.6f),
                    FrameState.get(-4f,0.3f),
                    FrameState.get(0f,0.1f)
            );
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
        progressRing = new TextureRegion(155,217,30,30,TEXTURE);
        processingGlow = new TextureRegion(160,197,20,20,TEXTURE);
        tempbarback = new TextureRegion(212,80,40,110,TEXTURE);
        tempBar = new TextureRegion(185,92,10,82,TEXTURE);
        inputSlot = new TextureRegion(0,202,18,18,TEXTURE);
        processingSlotOff = new TextureRegion(18,220,18,18,TEXTURE);
        processingSlotOn = new TextureRegion(0,220,18,18,TEXTURE);
        furnaceglow = new TextureRegion(48,230,49,10,TEXTURE);

        spark[0] = new TextureRegion(64,219,4,5,TEXTURE);
        spark[1] = new TextureRegion(70,200,4,4,TEXTURE);
        spark[2] = new TextureRegion(77,221,3,3,TEXTURE);
        getSlots();
        temperatureCounter = new UIMechanicalCounter("").setDigits(4);
        temperatureCounter.setPositioning(6,94).rebuild();
        temperatureCounter.updateFreq = 8;
        if(!ui.components.isEmpty()){
            return;
        }
        particles.addAffector(new ParticleWindAffector(999,0,0,1,0.3f,-1));
        particles.addAffector(new ParticleDeleter(999,0,0,40).setInvert(true));
        inventoryLabelY = -9999;
        titleLabelY = -20;
    }
    public static RenderAffectors FADE_IN_OUT  = new RenderAffectors((a,b,p)-> {
        float[] c = RenderSystem.getShaderColor();
        float al = SingularInterpolateType.SMOOTH_STEP.interpolate(0,1,Math.min(p.life,40-p.life)/20f,0);
        RenderSystem.setShaderColor(c[0], c[1], c[2], Math.min(1.0f,al));
    });
    @Override
    public void updateLogic() {
        temperatureCounter.value = menu.temp()+(int)tick*9;
        temperatureCounter.update();
        float max = 0;
        for(int i = 0;i<processingSlots.length;i++){
            glowStrength[i] = Mth.lerp(0.1f,glowStrength[i],menu.process(i)>0?1:0);
            max = Math.max(glowStrength[i],max);

            if(Mathf.randFloat(15)<glowStrength[i]){
                Vec2 randp = Mathf.randVec2().scale(4);
                particles.add(new DistortableUIParticle(processingSlots[i].x + randp.x+9, processingSlots[i].y + randp.y+9, 1, randp.x, randp.y, spark[Mathf.randInt(3)],
                        DistortableUIParticle.noiseDistort(4,0.05f))).renderAffectors = new RenderAffectors[]{FADE_IN_OUT};
            }
        }
        //particles.
        mainGlow = max;
        if(Mathf.randFloat(8)<mainGlow){
            Vec2 randp = Mathf.randVec2().scale(3);
            particles.add(new DistortableUIParticle(88 + randp.x*10f, 102 + randp.y, 1, randp.x, randp.y-4, spark[Mathf.randInt(3)],
                    DistortableUIParticle.noiseDistort(4,0.05f))).renderAffectors = new RenderAffectors[]{FADE_IN_OUT};
        }
    }

    @Override
    protected void renderBg(PoseStack matrices, float pPartialTick, int pMouseX, int pMouseY) {
        reset();

        spriteDrawer.reset(matrices);
        spriteDrawer.begin();
        spriteDrawer.pushMatrix();
        spriteDrawer.translate(originX+stateMap.f("x_offset"),originY);

        //thermometer
        spriteDrawer.pushMatrix();
        spriteDrawer.translate(-40+stateMap.f("temp_x_offset"),0,-10);
        spriteDrawer.draw(tempbarback,0,0);
        temperatureCounter.draw(spriteDrawer);
        spriteDrawer.setColor(1.0f,0.5f,0.4f);
        spriteDrawer.draw(tempBar,15,9);
        spriteDrawer.popMatrix();
        spriteDrawer.resetColor();

        spriteDrawer.draw(mainbg,0,0);

        for(var slot: menu.slots){
            if(slot instanceof SegmentSlot ss){
                switch (ss.getSegment().name){
                    case BloomeryChamberEntity.inputInvSegment -> {
                        spriteDrawer.draw(inputSlot,slot.x-1,slot.y-1);
                    }
                    case BloomeryChamberEntity.processingInvSegment -> {
                        spriteDrawer.draw(processingSlotOff,slot.x-1,slot.y-1);
                    }
                    case BloomeryChamberEntity.outputInvSegment -> {
                        spriteDrawer.draw(inputSlot,slot.x-1,slot.y-1);
                    }
                }
            }
        }
        for(int i = 0;i<processingSlots.length;i++){
            if(menu.process(i)>0) {
                renderProgressRing(processingSlots[i],menu.process(i)/100f);
            }
        }
        spriteDrawer.end();
        spriteDrawer.resetTex();
        for(int i = 0;i<processingSlots.length;i++){
       //     getTextRenderer().draw(spriteDrawer.getPoseStack(), new TextComponent(""+menu.process(i)), processingSlots[i].x+100 , processingSlots[i].y , Utils.rgb(250,250,250));
        }
        spriteDrawer.popMatrix();

        RenderSystem.disableBlend();

    }



    @Override
    public void renderTop() {
        //shiny things here
        reset();
        additiveBlendMode();

        spriteDrawer.begin();
        spriteDrawer.pushMatrix();
        spriteDrawer.translate(originX+stateMap.f("x_offset"),originY,0);


        spriteDrawer.setAlpha(mainGlow);
        spriteDrawer.draw(furnaceglow,64,95);


        //processing glows
        spriteDrawer.translate(0,0,400);
        setBlitOffset(400);
        spriteDrawer.begin();
        for(int i = 0;i<processingSlots.length;i++){
            if(menu.process(i)>0) {
                spriteDrawer.setAlpha(glowStrength[i]);
                spriteDrawer.draw(processingGlow, processingSlots[i].x - 2, processingSlots[i].y - 2);
            }
        }

        spriteDrawer.end();

        particles.draw(spriteDrawer.getPoseStack(), this);
        this.setBlitOffset(0);
        spriteDrawer.popMatrix();
        resetBlendMode();
    }

    void getSlots(){
        Array<Slot> slots = new Array<>(Slot.class);
        for(var slot: menu.slots){
            if(slot instanceof SegmentSlot ss){
                if(ss.getSegment().name.equals(BloomeryChamberEntity.processingInvSegment)){
                    slots.add(ss);
                }
            }
        }
        processingSlots = slots.toArray();
        glowStrength = new float[processingSlots.length];
    }

    public void renderProgressRing(Slot s,float progress){
        progress= Mth.clamp(progress,0,1);
        if(progress==0){
            return;
        }
        if(progress==1){
            spriteDrawer.drawCentered(progressRing,s.x+9,s.y+9);
            return;
        }
        float w2 = progressRing.w*0.5f;
        spriteDrawer.pushMatrix();
        spriteDrawer.translate(s.x+8,s.y+8);
        spriteDrawer.beginTriangleFan(progressRing);
        spriteDrawer.triangleFanVertex(0,0,(0.5f),(0.5f));
        spriteDrawer.triangleFanVertex(w2,0,(1f),(0.5f));
        float t = 0;
        t = Mth.clampedMap(progress,0,0.125f,0,1);
        spriteDrawer.triangleFanVertex(w2,-w2*t,(1f),(0.5f + t*0.5f));

        if(progress>0.125f){
            t = Mth.clampedMap(progress,0.125f,0.125f + 0.25f,0,1);
            spriteDrawer.triangleFanVertex(-w2*(t-0.5f)*2f,-w2,(1f-t),(1f));
        }
        if(progress>0.125f + 0.25f){
            t = Mth.clampedMap(progress,0.125f + 0.25f,0.125f + 0.5f,0,1);
            spriteDrawer.triangleFanVertex(-w2,w2*(t-0.5f)*2f,(0),(1f-t));
        }
        if(progress>0.125f + 0.5f){
            t = Mth.clampedMap(progress,0.125f + 0.5f,0.125f + 0.75f,0,1);
            spriteDrawer.triangleFanVertex(w2*(t-0.5f)*2f,w2,(t),(0));
        }
        if(progress>0.125f + 0.75f){
            t = Mth.clampedMap(progress,0.125f + 0.75f,1,0,1);
            spriteDrawer.triangleFanVertex(w2,-w2*(t-1f),(1f),(t*0.5f));
        }

        spriteDrawer.endTriangleFan();
        spriteDrawer.popMatrix();
    }
}
