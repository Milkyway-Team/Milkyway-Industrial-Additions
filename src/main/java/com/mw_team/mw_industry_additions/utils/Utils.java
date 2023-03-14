package com.mw_team.mw_industry_additions.utils;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.Random;

import static com.mw_team.mw_industry_additions.IndustrialAdditions.MOD_ID;

public class Utils {
    public static int Count(boolean[] arr, boolean b){
        int t = 0;
        for(int i = 0;i<arr.length;i++){
            if(arr[i]==b){
                t++;
            }
        }
        return t;
    }
    public static int Count(int[] arr, int b){
        int t = 0;
        for(int i = 0;i<arr.length;i++){
            if(arr[i]==b){
                t++;
            }
        }
        return t;
    }
    public static int Count(char[] arr, char b){
        int t = 0;
        for(int i = 0;i<arr.length;i++){
            if(arr[i]==b){
                t++;
            }
        }
        return t;
    }

    public static int toIntMask(boolean[] mask){
        int out = 0;
        for(int i =0;i<mask.length;i++){
            out = out|((mask[i]?1:0)<<i);
        }
        return out;
    }
    public static void fromIntMask(int mask, boolean[] out){
        for(int i =0;i<out.length;i++){
            out[i] = (mask&(1<<i))>0;
        }
    }

    public static float pixels(int pixels){
        return pixels/16f;
    }
    public static float pixels(float pixels){
        return pixels/16f;
    }

    public static Material getSprite(String name){
        return new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(MOD_ID+":"+name));
    }

    public static float[] floatArray(JSONArray ja) throws JSONException {
        float[] floats =new float[ja.length()];
        for(int i=0;i<floats.length;i++){
            floats[i]= (float)ja.getDouble(i);
        }
        return floats;
    }

    public static int rgb(int red,int green, int blue){
        return (red<<16) | (green<<8) | blue;
    }


    public static <T> void setFinalInt(T d,String f, int a){
        Field field = null;
        try{
            field = d.getClass().getDeclaredField(f);
        }catch(NoSuchFieldException e){
            e.printStackTrace();
        }
        field.setAccessible(true);
        try{
            field.setInt(d,a);
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * Credit to <a href="https://github.com/processing/processing4">...</a>
     */
    /**
     */
    public static float noise(float x) {
        return noise(x, 0f, 0f);
    }

    /**
     */
    public static float noise(float x, float y) {
        return noise(x, y, 0f);
    }
    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 1<<PERLIN_YWRAPB;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 1<<PERLIN_ZWRAPB;
    static final int PERLIN_SIZE = 4095;

    static int perlin_octaves = 4; // default to medium smooth
    static float perlin_amp_falloff = 0.5f; // 50% reduction/octave

    static float[] perlin;

    static Random perlinRandom;
    public static float noise(float x, float y, float z) {
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random();
            }
            perlin = new float[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextFloat(); //(float)Math.random();
            }
        }

        if (x<0) x=-x;
        if (y<0) y=-y;
        if (z<0) z=-z;

        int xi=(int)x, yi=(int)y, zi=(int)z;
        float xf = x - xi;
        float yf = y - yi;
        float zf = z - zi;
        float rxf, ryf;

        float r=0;
        float ampl=0.5f;

        float n1,n2,n3;

        for (int i=0; i<perlin_octaves; i++) {
            int of=xi+(yi<<PERLIN_YWRAPB)+(zi<<PERLIN_ZWRAPB);

            rxf=noise_fsc(xf);
            ryf=noise_fsc(yf);

            n1  = perlin[of&PERLIN_SIZE];
            n1 += rxf*(perlin[(of+1)&PERLIN_SIZE]-n1);
            n2  = perlin[(of+PERLIN_YWRAP)&PERLIN_SIZE];
            n2 += rxf*(perlin[(of+PERLIN_YWRAP+1)&PERLIN_SIZE]-n2);
            n1 += ryf*(n2-n1);

            of += PERLIN_ZWRAP;
            n2  = perlin[of&PERLIN_SIZE];
            n2 += rxf*(perlin[(of+1)&PERLIN_SIZE]-n2);
            n3  = perlin[(of+PERLIN_YWRAP)&PERLIN_SIZE];
            n3 += rxf*(perlin[(of+PERLIN_YWRAP+1)&PERLIN_SIZE]-n3);
            n2 += ryf*(n3-n2);

            n1 += noise_fsc(zf)*(n2-n1);

            r += n1*ampl;
            ampl *= perlin_amp_falloff;
            xi<<=1; xf*=2;
            yi<<=1; yf*=2;
            zi<<=1; zf*=2;

            if (xf>=1.0f) { xi++; xf--; }
            if (yf>=1.0f) { yi++; yf--; }
            if (zf>=1.0f) { zi++; zf--; }
        }
        return r;
    }
    private static float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f*(1.0f- Mth.cos(i*Mth.PI));
    }

}
