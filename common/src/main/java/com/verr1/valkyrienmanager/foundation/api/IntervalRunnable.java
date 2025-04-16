package com.verr1.valkyrienmanager.foundation.api;

public interface IntervalRunnable extends Runnable{

    int getCyclesRemained();

    int getIntervalTicks();

    void reset();

    void tickDown();

    void cycleDown();

    void onExpire();
}
