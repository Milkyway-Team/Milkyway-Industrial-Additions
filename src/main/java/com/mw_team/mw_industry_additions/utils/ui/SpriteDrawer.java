package com.mw_team.mw_industry_additions.utils.ui;

import com.mojang.blaze3d.systems.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.mw_team.mw_industry_additions.client.screen.AnimatedScreen;

import com.mojang.blaze3d.vertex.*;
import com.mw_team.mw_industry_additions.utils.Mathf;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.function.*;

public class SpriteDrawer{
    BufferBuilder bufferBuilder;
    boolean drawing = false;
    PoseStack PoseStack;
    int c_texture = -1;
    public ScissorStack scissor;
    AnimatedScreen screen;
    VertexFormat.Mode Mode = VertexFormat.Mode.QUADS;

    public SpriteDrawer(PoseStack PoseStack,AnimatedScreen screen){
        this.PoseStack = PoseStack;
        scissor = new ScissorStack(screen);
        this.screen=screen;
    }

    public void reset(PoseStack PoseStack){
        end();
        this.PoseStack=PoseStack;
    }
    public void resetTex(){
        interrupt(()->c_texture=-1);
    }

    public void begin(){
        if(drawing){
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bufferBuilder= Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(Mode, DefaultVertexFormat.POSITION_COLOR_TEX);

        drawing = true;
    }

    public void setMode(VertexFormat.Mode Mode){
        if(this.Mode.equals(Mode)){return;}
        end();
        this.Mode = Mode;
        begin();
    }

    public void end(){
        if(!drawing){
            return;
        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        bufferBuilder = null;
        drawing = false;
    }

    public BufferBuilder getBufferBuilder(){
        return bufferBuilder;
    }

    public void interrupt(Runnable runnable){
        if(drawing){
            end();
            runnable.run();
            begin();
        }else{
            runnable.run();
        }
    }

    public PoseStack getPoseStack(){
        return PoseStack;
    }

    public void setShader(Supplier<ShaderInstance> shader){
        interrupt(()->RenderSystem.setShader(shader));
    }
    public void setTexture(int id){
        if(c_texture==id){return;}
        interrupt(()->{
            RenderSystem.setShaderTexture(0, id);
            c_texture = id;
        });
    }

    /**
     * Rotates the current transform by x degrees in the z plane... aka just normal rotation in 2d.
     * @param angle
     */
    public void rotate(float angle){
        getPoseStack().mulPose(Vector3f.ZP.rotationDegrees(angle));
    }

    public void drawItem(ItemStack stack, float x, float y, float z){
        int tex = RenderSystem.getShaderTexture(0);
        end();
        screen.drawItem(getPoseStack(),stack,x,y,z,stack.getCount()>1?stack.getCount()+"":"");
        RenderSystem.setShaderTexture(0,tex);
        RenderSystem.enableBlend();
        begin();
    }
    public void pushMatrix(){
        getPoseStack().pushPose();
    }
    public void popMatrix(){
        getPoseStack().popPose();
    }
    public void translate(float x,float y){
        translate(x,y,0);
    }
    public void translate(float x,float y,float z){
        getPoseStack().translate(x,y,z);
    }

    public float[] color = {1,1,1,1};

    public void setColor(float r,float g, float b ,float a){
        this.color[0]=r;
        this.color[1]=g;
        this.color[2]=b;
        this.color[3]=a;
    }
    public void setAlpha(float a){
        this.color[3]=a;
    }
    public void setColor(float a){
        setColor(a,a,a,1.0f);
    }
    public void resetColor(){
        setColor(1,1,1,1);
    }
    public void setColor(Vector4f a){
        setColor(a.x(),a.y(),a.z(),a.w());
    }
    public void setColor(float g,float a){
        setColor(g,g,g,a);
    }
    public void setColor(float r,float g, float b ){
        setColor(r,g,b,1.0f);
    }

    private Vec2 initVertexPolygon = new Vec2(0,0); Vec2 texLocPolygon = new Vec2(0,0);
    private Vec2 pinitVertexPolygon = new Vec2(0,0); Vec2 ptexLocPolygon = new Vec2(0,0);
    private TextureRegion polygontex;
    private boolean initVertex = false;
    private boolean initpVertex = false;
    public void beginTriangleFan(TextureRegion tr){
        setMode(VertexFormat.Mode.TRIANGLES);
        polygontex = tr;
        setTexture(tr.getTexid());
    }
    public void triangleFanVertex(float x, float y, float u, float v){
        if(!initVertex){
            initVertexPolygon = new Vec2(x,y);
            texLocPolygon = new Vec2(polygontex.relU(u),polygontex.relV(v));
            initVertex = true;
            return;
        }
        if(!initpVertex){
            pinitVertexPolygon = new Vec2(x,y);
            ptexLocPolygon = new Vec2(polygontex.relU(u),polygontex.relV(v));
            initpVertex = true;
            return;
        }
        setTexture(polygontex.getTexid());
        vertex(initVertexPolygon.x,initVertexPolygon.y,texLocPolygon.x,texLocPolygon.y);
        vertex(pinitVertexPolygon.x,pinitVertexPolygon.y,ptexLocPolygon.x,ptexLocPolygon.y);
        vertex(x,y,polygontex.relU(u),polygontex.relV(v));
        pinitVertexPolygon = new Vec2(x,y);
        ptexLocPolygon = new Vec2(polygontex.relU(u),polygontex.relV(v));
    }
    private void vertex(float x, float y, float u, float v){
            bufferBuilder.vertex(getPoseStack().last().pose(), x, y, 0).color(color[0],color[1],color[2],color[3]).uv(u, v).endVertex();
    }
    public void endTriangleFan(){
        initVertex = false;
        initpVertex = false;
    }
    public void drawLine(float x, float y, float x2, float y2, float w){
        drawLine(UITextures.blanksquare,x,y,x2,y2,w);
    }
    public void drawLine(TextureRegion tr , float x, float y, float x2, float y2, float w){
        setMode(Mode.QUADS);
        setTexture(tr.getTexid());
        float dx = x2-x;
        float dy = y2-y;
        float d = Mth.sqrt(dx*dx+dy+dy);
        dx*=w/d;
        dy*=w/d;
        vertex(x2-dy,y2+dx,tr.u, tr.v2);
        vertex(x2+dy,y2-dx,tr.u2, tr.v2);
        vertex(x+dy,y-dx,tr.u2, tr.v);
        vertex(x-dy,y+dx,tr.u, tr.v);

    }

    public void drawRect(float x, float y, float w, float h){
        draw(UITextures.blanksquare,x,y,    w,1);
        draw(UITextures.blanksquare,x,y+h-1,w,1);
        draw(UITextures.blanksquare,x,y,    1,h);
        draw(UITextures.blanksquare,x+w-1,y,1,h);
    }
    public void clipRelative(float x, float y, float w, float h){
        Vector4f p = new Vector4f(x,y,0,1);
        p.transform(getPoseStack().last().pose());
        Vector4f p2 = new Vector4f(x+w,y+h,0,1);
        p2.transform(getPoseStack().last().pose());
        interrupt(()->scissor.clip((int)p.x(),(int)p.y(),(int)(p2.x()-p.x()),(int)(p2.y()-p.y())));
    }
    public void clip(float x, float y, float w, float h){
        interrupt(()->scissor.clip((int)x + screen.ox,(int)y + screen.oy,(int)w,(int)h));
    }
    public void unclip(){
        interrupt(()->scissor.unclip());
    }
    public void drawCentered(TextureRegion tr, float x, float y){
        draw(tr,x-tr.w*0.5f,y-tr.h*0.5f,tr.w,tr.h);
    }

    public void draw(TextureRegion tr, float x, float y){
        draw(tr,x,y,tr.w,tr.h);
    }
    public void draw(TextureRegion tr, float x, float y, float w, float h){
        setMode(Mode.QUADS);
        setTexture(tr.getTexid());
        Matrix4f matrix = getPoseStack().last().pose();
        bufferBuilder.vertex(matrix, x, y+h, 0).color(color[0],color[1],color[2],color[3]).uv(tr.u, tr.v2).endVertex();;
        bufferBuilder.vertex(matrix, x+w, y+h, 0).color(color[0],color[1],color[2],color[3]).uv(tr.u2, tr.v2).endVertex();
        bufferBuilder.vertex(matrix, x+w, y, 0).color(color[0],color[1],color[2],color[3]).uv(tr.u2, tr.v).endVertex();
        bufferBuilder.vertex(matrix, x, y, 0).color(color[0],color[1],color[2],color[3]).uv(tr.u, tr.v).endVertex();
    }
    public void drawSection(TextureRegion tr, float x, float y, float rx, float ry,float rw, float rh){
        setMode(Mode.QUADS);
        float au1 = Mth.clamp(Mathf.lerp(rx/tr.w,tr.u,tr.u2),tr.u,tr.u2);
        float au2 = Mth.clamp(Mathf.lerp((rx+rw)/tr.w,tr.u,tr.u2),tr.u,tr.u2);
        float av1 = Mth.clamp(Mathf.lerp(ry/tr.h,tr.v,tr.v2),tr.v,tr.v2);
        float av2 = Mth.clamp(Mathf.lerp((ry+rh)/tr.h,tr.v,tr.v2),tr.v,tr.v2);
        setTexture(tr.getTexid());
        Matrix4f matrix = getPoseStack().last().pose();
        bufferBuilder.vertex(matrix, x, y+rh, 0).color(color[0],color[1],color[2],color[3]).uv(au1, av2).endVertex();
        bufferBuilder.vertex(matrix, x+rw, y+rh, 0).color(color[0],color[1],color[2],color[3]).uv(au2, av2).endVertex();
        bufferBuilder.vertex(matrix, x+rw, y, 0).color(color[0],color[1],color[2],color[3]).uv(au2, av1).endVertex();
        bufferBuilder.vertex(matrix, x, y, 0).color(color[0],color[1],color[2],color[3]).uv(au1, av1).endVertex();
    }

}
