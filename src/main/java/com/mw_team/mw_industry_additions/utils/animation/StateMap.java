package com.mw_team.mw_industry_additions.utils.animation;

import org.mini2Dx.gdx.utils.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class StateMap{
    ObjectMap<String,AnimationState> states =new ObjectMap<>();
    ParameterMap params = new ParameterMap();
    AnimationState current=null;
    AnimationState pending = null;
    float internalTick = 0;
    public Function<StateMap,String> onStateEnd;

    public StateMap(Function<StateMap, String> onStateEnd, Consumer<ParameterMap> initVariables){
        this.onStateEnd = onStateEnd;
        initVariables.accept(params);
    }
    public boolean is(String as){
        if(as==null || as.length()==0){
            return current==null;
        }
        if(current==null){return false;}
        return current.name.equals(as);
    }
    public StateMap addState(AnimationState as){
        states.put(as.name,as);
        return this;
    }

    public StateMap setState(String as){
        if(as==null){
            return this;
        }
        current = states.get(as);
        params.removeInterops();
        current.init.accept(params);
        internalTick=0;
        return this;
    }

    public StateMap request(String as){
        if(current==null || !current.needsToComplete){
            setState(as);
        }else{
            pending = states.get(as);
        }
        return this;
    }


    public void update(){
        if(current==null){
            setState(onStateEnd.apply(this));
            if(current==null){
                return;
            }
        }
        params.update(internalTick/current.duration);
        internalTick++;
        if(internalTick>=current.duration){
            internalTick=0;
            if(pending!=null){
                setState(pending.getName());
                pending=null;
            }else{
                if(!current.loops){
                    setState(onStateEnd.apply(this));
                }else if(current.resetOnloop){
                    setState(current.getName());
                }
            }
        }
    }

    public AnimationState getCurrent(){
        return current;
    }

    public float f(String k){
        return params.f(k);
    }
    public int i(String k){
        return params.i(k);
    }



    public static class AnimationState{
        String name;
        boolean loops = true;
        boolean resetOnloop = true;
        Consumer<ParameterMap> init;
        float duration=60;
        boolean needsToComplete=true;
        AnimationState(String name){
            this.name = name;
        }
        public AnimationState(String name, boolean loops, float duration, Consumer<ParameterMap> init){
            this.name = name;
            this.loops = loops;
            this.init = init;
            this.duration = duration;
        }
        public static AnimationState get(String name){
            return new AnimationState(name);
        }
        public AnimationState loops(boolean loops){
            this.loops = loops;
            return this;
        }
        public AnimationState resetOnloop(boolean loops){
            this.resetOnloop = loops;
            return this;
        }
        public AnimationState needsToComplete(boolean needsToComplete){
            this.needsToComplete = needsToComplete;
            return this;
        }
        public AnimationState duration(float duration){
            this.duration = duration;
            return this;
        }
        public AnimationState onInit(Consumer<ParameterMap> init){
            this.init = init;
            return this;
        }

        public String getName(){
            return name;
        }
    }
}
