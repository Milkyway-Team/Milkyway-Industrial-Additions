package com.mw_team.mw_industry_additions.utils;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static net.minecraft.util.Mth.*;

public class Mathf{

    public static final float PI = 3.1415927F;
    public static final float HALF_PI = 1.5707964F;
    public static final float TAU = 6.2831855F;
    public static final float RADIANS_PER_DEGREE = 0.017453292F;
    public static final float DEGREES_PER_RADIAN = 57.295776F;
    public static final float EPSILON = 1.0E-5F;
    public static final float SQUARE_ROOT_OF_TWO = sqrt(2.0F);
    static Random rand = new Random();
    static {

    }
    public static Vector3f randVec3(){
        float t  = rand.nextFloat()* Mth.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        return new Vector3f(s1z* cos(t),s1z* sin(t),z);
    }
    public static Vec2 randVec2(){
        float t  = rand.nextFloat()*Mth.PI*2.0f;
        return new Vec2(cos(t), sin(t));
    }
    public static Vec2 randVec2Uniform(){
        return new Vec2(rand.nextFloat(), rand.nextFloat());
    }
    public static float randFloat(float max){
        return rand.nextFloat()*max;
    }
    public static int randInt(int exclusiveMax){
        return rand.nextInt(exclusiveMax);
    }
    public static float randFloat(float min,float max){
        return rand.nextFloat()*(max-min) + min;
    }
    public static void randVec3(Cons.Cons3<Float,Float,Float> cons){
        float t  = rand.nextFloat()*Mth.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        cons.get(s1z* cos(t),s1z* sin(t),z);
    }
    public static float sinDeg(float deg){
        return sin(deg*RADIANS_PER_DEGREE);
    }
    public static float cosDeg(float deg){
        return cos(deg*RADIANS_PER_DEGREE);
    }

    public static float getHorzAngle(Vector3f vec){
        return (float)Math.toDegrees(atan2(vec.z(),vec.x()))+90;
    }

    public static Vector3f relativeDirectionHorzF(Direction dir, Vector3f vec){
        Vector3f dirvecZ = new Vector3f(dir.getStepX(),dir.getStepY(), dir.getStepZ());
        Vector3f dirvecX = new Vector3f(-dir.getStepZ(),dir.getStepY(), dir.getStepX());
        Vector3f dirvecY = dirvecX.copy();
        dirvecY.cross(dirvecZ);
        dirvecX.mul(vec.x());
        dirvecY.mul(vec.y());
        dirvecZ.mul(vec.z());
        dirvecX.add(dirvecY);
        dirvecX.add(dirvecZ);
        return  dirvecX;

    }
    public static Vec3i relativeDirectionHorz(Direction dir, Vector3f vec){
        Vector3f v = relativeDirectionHorzF(dir,vec);
        return new Vec3i(v.x(),v.y(),v.z());
    }
    public static Vec3i vec3i(Vector3f v){
        return new Vec3i(v.x(),v.y(),v.z());
    }
    public static float lerp(float t, float x,float x2){return t*(x2-x) + x;}
    public static float map(float r, float rmin,float rmax, float mapmin,float mapmax){
        return mapmin + (mapmax-mapmin)*(r-rmin)/(rmax-rmin);
    }
    public static float mapClamped(float r, float rmin,float rmax, float mapmin,float mapmax){
        return mapmin + (mapmax-mapmin)*Mth.clamp((r-rmin)/(rmax-rmin),0,1);
    }
    public static float catmull(float t, float x,float x2,float m1,float m2){
        float t2 = t*t;
        float t3 = t2*t;
        return  (2*t3 - 3*t2 + 1)*x + (t3 - 2*t2 + t)*m1 + (-2*t3 + 3*t2)*x2 + (t3-t2)*m2;
    }
    public static float catmullNorm(float t,float m1,float m2){
        float t2 = t*t;
        float t3 = t2*t;
        return  (t3 - 2*t2 + t)*m1 + (-2*t3 + 3*t2) + (t3-t2)*m2;
    }

    /**
     * x ^ e
     * @param x
     * @param e
     * @return
     */
    public static int intPow(int x,int e){
        if(e<0){
            return 0;
        }
        int total = 1;
        for(int i = 0;i<e;i++){
           total*=x;
        }
        return total;
    }
    public static Quaternion fromEulerDegXYZ(float x, float y, float z){
        return Quaternion.fromXYZ(x*RADIANS_PER_DEGREE,y*RADIANS_PER_DEGREE,z*RADIANS_PER_DEGREE);
    }


    public static float dst2(float x,float y){
        return x*x+y*y;
    }

    public static float approach(float x, float target,float speed){
        return x+(target>=x?Math.min(speed,target-x):-Math.min(speed,x-target));
    }
    public static float lerpTowards(float x, float target,float speed){
        return x+(target-x)*speed;
    }

    public static final int lookupSize = 256;
    public static float[][] randLookup = new float[lookupSize][lookupSize];

    public static float getRandFromPoint(int x,int y){
        return randLookup[x&0xFF][y&0xFF];
    }
    static {
        for(int i = 0;i<lookupSize;i++){
            for(int j = 0;j<lookupSize;j++){
                randLookup[i][j] = Mathf.randFloat(1);
            }
        }
    }

}
