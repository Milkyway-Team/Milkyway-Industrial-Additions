package com.mw_team.mw_industry_additions.utils.ui;

import com.mojang.blaze3d.systems.*;
import com.mw_team.mw_industry_additions.client.screen.AnimatedScreen;
import net.minecraft.client.*;
import net.minecraft.util.Mth;
import org.mini2Dx.gdx.utils.*;

public class ScissorStack{
    AnimatedScreen client;
    Array<ScissorMask> stack = new Array<>();

    public ScissorStack(AnimatedScreen client){
        this.client = client;
    }

    public void clip(int x, int y, int w, int h){

        if(!stack.isEmpty()){
            var s = stack.peek();
            int x2 = x+w;
            x = Mth.clamp(x,s.x,s.x+s.w);
            w = Mth.clamp(x2,s.x,s.x+s.w)-x;
            int y2 = y+h;
            y = Mth.clamp(y,s.y,s.y+s.h);
            h = Mth.clamp(y2,s.y,s.y+s.h)-y;
        }
        stack.add(new ScissorMask(x,y,w,h));
        stack.peek().apply();
    }

    public void unclip(){
        stack.pop();
        RenderSystem.disableScissor();
        if(!stack.isEmpty()){
            stack.peek().apply();
        }
    }
    class ScissorMask{
        int x,y,w,h;

        public ScissorMask(int x, int y, int w, int h){
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void apply(){
            //ox and oy, would just be HandledScreen.x and HandledScreen.y
            float d = (float)client.getMinecraft().getWindow().getGuiScale();
            int ay = (int)((client.height-(y+h))*d);
            RenderSystem.enableScissor((int)((x)*d),ay,(int)(w*d),(int)(h*d));
        }
    }
}
