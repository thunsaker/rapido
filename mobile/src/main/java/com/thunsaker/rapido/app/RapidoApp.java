package com.thunsaker.rapido.app;

import com.thunsaker.android.common.dagger.DaggerApplication;

import java.util.Collections;
import java.util.List;

public class RapidoApp extends DaggerApplication {
    @Override
    protected List<Object> getAppModules() {
        return Collections.<Object>singletonList(new RapidoAppModule());
    }
}