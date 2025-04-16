package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.foundation.executor.DeferralExecutor;
import com.verr1.valkyrienmanager.foundation.executor.IntervalExecutor;

public class ValkyrienManagerClient {

    public static final DeferralExecutor CLIENT_DEFERRAL_EXECUTOR = new DeferralExecutor();
    public static final IntervalExecutor CLIENT_INTERVAL_EXECUTOR = new IntervalExecutor();


    public static void init(){

    }
}
