package com.verr1.valkyrienmanager.foundation.api;

public interface QueryConditionRunnable extends DeferralRunnable{


    boolean condition();

    @Override
    default void tick() {

    }

    @Override
    default int getDeferralTicks(){
        return condition() ? -1 : 1;
    }
}

