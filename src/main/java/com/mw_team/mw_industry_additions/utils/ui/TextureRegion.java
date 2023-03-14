package com.mw_team.mw_industry_additions.utils.ui;

import com.mojang.blaze3d.systems.*;
import com.mw_team.mw_industry_additions.utils.Mathf;
import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;

public class TextureRegion{
    public float u,u2,v,v2,x,y,w,h;
    public ResourceLocation texture;
    private int texid=-1;
    public AbstractTexture rawTex;

    public TextureRegion(float u, float u2, float v, float v2,float x, float y, float w, float h, ResourceLocation texture){
        this.u = u;
        this.u2 = u2;
        this.v = v;
        this.v2 = v2;
        this.texture = texture;
        this.w=w;
        this.h=h;
    }
    public TextureRegion(float w, float h, ResourceLocation texture){
        this.u = 0;
        this.u2 = 1;
        this.v = 0;
        this.v2 = 1;
        this.texture = texture;
        this.w=w;
        this.h=h;
    }
    public TextureRegion(int x, int y, int w, int h, int tw, int th, ResourceLocation texture){
        this((float)x/(float)tw,(float)(x+w)/(float)tw,((float)y/(float)th),((y+(float)h)/(float)th),x,y, w, h, texture);
    }
    public TextureRegion(int x, int y, int w, int h, ResourceLocation texture){
        this(x,y,w,h,256,256, texture);
    }

    public TextureRegion subRegion(int x, int y, int w, int h){
        return new TextureRegion(
            Mathf.map(x,0,this.w,u,u2),Mathf.map(x+w,0,this.w,u,u2),
            Mathf.map(y,0,this.h,v,v2),Mathf.map(y+h,0,this.h,v,v2),this.x+x,this.y+y,w,h,this.texture);
    }

    public int getTexid(){
        if(rawTex==null){
            rawTex= Minecraft.getInstance().getTextureManager().getTexture(texture);
        }
        return rawTex.getId();
    }

    public float relU(float x){
        return Mth.lerp(x,u,u2);
    }
    public float relV(float y){
        return Mth.lerp(y,v,v2);
    }

    @Override
    public String toString(){
        return "TextureRegion{" +
        "u=" + u +
        ", u2=" + u2 +
        ", v=" + v +
        ", v2=" + v2 +
        ", w=" + w +
        ", h=" + h +
        ", texture=" + texture +
        ", texid=" + texid +
        '}';
    }
}
