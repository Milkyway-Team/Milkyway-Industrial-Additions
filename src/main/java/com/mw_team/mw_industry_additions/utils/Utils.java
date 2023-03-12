package com.mw_team.mw_industry_additions.utils;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;

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
}
