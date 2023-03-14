package com.mw_team.mw_industry_additions.utils.ui.particles;

import com.mw_team.mw_industry_additions.utils.Utils;

public class ParticleWindAffector extends ParticleAffector{
    float am,vx,vy;
    private float t;
    public ParticleWindAffector(float radius, float x, float y,float am,float vx,float vy) {
        super(radius, x, y);
        this.am=am;
        this.vx=vx;
        this.vy=vy;
    }

    @Override
    public void affect(UIParticle particle) {
        t+=0.005f;
        float dx = x-particle.x;
        float dy = y-particle.y;
        if(dx*dx+dy*dy>radius*radius){return;}
        particle.vx += ((Utils.noise(particle.x*0.02f+t,particle.y*0.02f)-0.5f)*am +vx - particle.vx )*0.1f;
        particle.vx *= 0.97f;
        particle.vy += ((Utils.noise(particle.x*0.02f+100,particle.y*0.02f+t)-0.5f)*am + vy - particle.vy )*0.1f;
        particle.vy *= 0.97f;
    }
}
