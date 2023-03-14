package com.mw_team.mw_industry_additions.utils.ui.components;

import com.mw_team.mw_industry_additions.utils.Mathf;
import com.mw_team.mw_industry_additions.utils.animation.Interpolations;
import com.mw_team.mw_industry_additions.utils.ui.RegionNineSlice;
import com.mw_team.mw_industry_additions.utils.ui.SpriteDrawer;
import com.mw_team.mw_industry_additions.utils.ui.UITextures;
import net.minecraft.util.Mth;

public class UIMechanicalCounter extends UIComponent{

    public RegionNineSlice bg = UITextures.defaultButtonDown;
    public int updateFreq = 8;
    int tick = 0;

    float transition = 0;

    int prev = 0;
    int current = 0;

    public int value = 0;
    public int digits = 2;

    public int digitW = 7;

    public int digitH = 12;

    public boolean allowBackWards = false;

    public UIMechanicalCounter(String id) {
        super(id);
        setDigits(2);
    }

    public UIMechanicalCounter setDigits(int digits) {
        this.digits = digits;
        this.setMinimumSize(digits * digitW,digitH);
        return this;
    }



    @Override
    public void draw(SpriteDrawer sb) {
        sb.clipRelative(x,y,w,h);
        sb.getPoseStack().pushPose();
        sb.getPoseStack().translate(x,y,0);

        int digitX = (digitW-5)/2;
        float digitY = (h-7)/2;
        float value = Interpolations.SingularInterpolateType.EXPONENTIAL2.interpolate(0,1,transition,0.2f);

        int f = 1;
        for(int i = 0;i<digits;i++){
            int p = (prev/f)%10;
            int c = (current/f)%10;
            int dx = (digits-i-1)*digitW;
            if(p==c){
                sb.setColor(1);
                bg.drawInner(sb,dx,0,digitW,h);
                sb.setColor(0.2f);
                sb.draw(UITextures.numbers[c],dx+digitX,digitY);
            }else{
                sb.setColor(1);
                bg.drawInner(sb,dx,0,digitW,h);
                sb.setColor(0.2f);
                int forwardDist = c>=p ? c-p : 10+c-p;
                int backwardDist = p>=c ? p-c : 10+p-c;
                int am = forwardDist;
                int dir = 1;
                if(allowBackWards && backwardDist<forwardDist){
                    dir=-1;
                    am = backwardDist;
                }
                for(int d = 0;d<=am;d++) {
                    int digit = (10+p+dir*d)%10;
                    sb.draw(UITextures.numbers[digit], dx+digitX, -dir * d * h + digitY + Mth.lerp(value,0,am*dir*h));
                }
            }
            f*=10;
        }
        sb.getPoseStack().popPose();
        sb.unclip();
        sb.setColor(1);
    }

    @Override
    public void update() {
        tick ++;
        if(tick % updateFreq == 0){
            prev = current;
            current = value;
        }
        transition = (tick % updateFreq)/(float)updateFreq;
    }
}
