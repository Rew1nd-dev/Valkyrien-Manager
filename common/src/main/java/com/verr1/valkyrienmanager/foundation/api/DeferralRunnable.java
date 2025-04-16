package com.verr1.valkyrienmanager.foundation.api;

public interface DeferralRunnable extends Runnable {

    int getDeferralTicks();

    void tick();
}
