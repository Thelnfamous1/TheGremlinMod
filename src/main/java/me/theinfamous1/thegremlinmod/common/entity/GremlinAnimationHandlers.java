package me.theinfamous1.thegremlinmod.common.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class GremlinAnimationHandlers {
    public static <T extends AbstractGremlin & GeoEntity> PlayState animateMovement(T gremlin, AnimationState<T> state, RawAnimation swim, RawAnimation walk, RawAnimation run, RawAnimation idle, RawAnimation idle2) {
        if (gremlin.isWalking()) {
            if(gremlin.isSwimming() && !gremlin.isPerformingSpecialAction()){
                return state.setAndContinue(swim);
            }
            if(gremlin.isPerformingSpecialAction()){
                if(gremlin.canWalkWhilePerformingSpecialAction()){
                    return state.setAndContinue(walk);
                }
            } else{
                return state.setAndContinue(gremlin.isSprinting() ? run : walk);
            }
        } else if (!gremlin.isPerformingSpecialAction()){
            return state.setAndContinue(gremlin.isUsingAlternateIdle() ? idle2 : idle);
        }
        return PlayState.STOP;
    }

    public static <T extends AbstractGremlin & GeoEntity> PlayState animateDuplicate(T gremlin, AnimationState<T> state, RawAnimation hideDuplicate) {
        if(!gremlin.isDuplicating()){
            return state.setAndContinue(hideDuplicate);
        }
        return PlayState.STOP;
    }
}
