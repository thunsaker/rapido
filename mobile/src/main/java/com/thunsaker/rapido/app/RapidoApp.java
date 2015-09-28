package com.thunsaker.rapido.app;

import com.squareup.leakcanary.LeakCanary;
import com.thunsaker.android.common.dagger.DaggerApplication;

import java.util.Collections;
import java.util.List;

public class RapidoApp extends DaggerApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!isInUnitTests()) {
            installLeakCanary();
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

    protected void installLeakCanary() {
//        ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
//                // Google Maps API : https://github.com/square/leakcanary/issues/224
//                .staticField("com.google.android.chimera.container.a", "a")
//                .build();
//        LeakCanary.install(this, DisplayLeakService.class, excludedRefs);
        LeakCanary.install(this);
    }
}