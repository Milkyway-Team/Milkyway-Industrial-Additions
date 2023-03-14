package com.mw_team.mw_industry_additions.utils.ui.particles;

public class ParticleDeleter extends ParticleAffector{
    boolean invert = false;
    float maxlife= 999;
    public ParticleDeleter(float radius, float x, float y){
        super(radius, x, y);
    }
    public ParticleDeleter(float radius, float x, float y,float maxLife){
        super(radius, x, y);
        this.maxlife = maxLife;
    }

    public static ParticleDeleter create(float radius, float x, float y){
        return new ParticleDeleter(radius,x,y);
    }

    public static ParticleDeleter create(float radius,float maxlife, float x, float y){
        return new ParticleDeleter(radius,x,y,maxlife);
    }
    public ParticleDeleter setInvert(boolean invert){
        this.invert = invert;
        return this;
    }

    @Override
    public void affect(UIParticle particle){
        float dx = x-particle.x;
        float dy = y-particle.y;
        if(particle.life < maxlife && dx*dx+dy*dy>radius*radius ^ invert){return;}
        particle.life=-99;
    }
}
