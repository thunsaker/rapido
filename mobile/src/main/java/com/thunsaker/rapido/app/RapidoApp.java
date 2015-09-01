package com.thunsaker.rapido.app;

import com.squareup.leakcanary.LeakCanary;
import com.thunsaker.android.common.dagger.DaggerApplication;

import java.util.Collections;
import java.util.List;

public class RapidoApp extends DaggerApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: Remove this later
        if (!isInUnitTests()) {
            LeakCanary.install(this);
        }
    }

    @Override
    protected List<Object> getAppModules() {
        return Collections.<Object>singletonList(
                new RapidoAppModule());
    }

    protected boolean isInUnitTests() {
        return false;
    }
}