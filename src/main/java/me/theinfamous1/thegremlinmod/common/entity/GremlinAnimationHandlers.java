package me.theinfamous1.thegremlinmod.common.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class GremlinAnimationHandlers {
    protected static <T extends AbstractGremlin & GeoEntity> PlayState animateMovement(T gremlin, AnimationState<T> state, RawAnimation swim, RawAnimation walk, RawAnimation run, RawAnimation idle, RawAnimation idle2) {
        if (gremlin.isWalking()) {
            if(gremlin.isSwimming() && !gremlin.isPerformingAnimatedAction()){
                return state.setAndContinue(swim);
            }
            if(gremlin.isPerformingAnimatedAction()){
                if(gremlin.canWalkWhilePerformingAnimatedAction()){
                    return state.setAndContinue(walk);
                }
            } else{
                return state.setAndContinue(gremlin.isSprinting() ? run : walk);
            }
        } else if (!gremlin.isPerformingAnimatedAction()){
            return state.setAndContinue(gremlin.isUsingAlternateIdle() ? idle2 : idle);
        }
        return PlayState.STOP;
    }
}
