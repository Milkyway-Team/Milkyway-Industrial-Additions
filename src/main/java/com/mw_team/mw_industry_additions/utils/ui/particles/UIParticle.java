package com.mw_team.mw_industry_additions.utils.ui.particles;

import com.mojang.blaze3d.platform.GlStateManager.*;
import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mw_team.mw_industry_additions.client.screen.AnimatedScreen;
import com.mw_team.mw_industry_additions.utils.Cons;

public abstract class UIParticle{
    public float x,y,vx,vy;
    public float life = 0;
    public RenderAffectors[] renderAffectors = {};
    public boolean cantDie = false;
    public long layer = 1;
    public UIParticle(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void update(){
        x+=vx;
        y+=vy;
        life++;
    }
    public abstract void draw(PoseStack matrices, AnimatedScreen at);

    public void forceKill(){
        life=-99;
        cantDie=false;
    }

    public UIParticle setLayer(int layer,boolean enabled){
        this.layer = (this.layer-(this.layer&(1L<<layer)))|(enabled?1L:0L)<<layer;
        return this;
    }

    public UIParticle setRenderAffectors(RenderAffectors... renderAffectors){
        this.renderAffectors = renderAffectors;
        return this;
    }

    public static class RenderAffectors{
        Cons.Cons3<PoseStack, AnimatedScreen, UIParticle> effect = (a, b, c)->{};

        public RenderAffectors(Cons.Cons3<PoseStack, AnimatedScreen, UIParticle> effect){
            this.effect = effect;
        }



        public static RenderAffectors FADE_IN  = new RenderAffectors((a,b,p)-> {
            float[] c = RenderSystem.getShaderColor();
            RenderSystem.setShaderColor(c[0], c[1], c[2], Math.min(1.0f,p.life*0.05f));
        });
        public static RenderAffectors ADD_BLEND  = new RenderAffectors((a,b,p)-> b.additiveBlendMode());
        public static RenderAffectors NORM_BLEND  = new RenderAffectors((a,b,p)-> RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA));
    }
}
