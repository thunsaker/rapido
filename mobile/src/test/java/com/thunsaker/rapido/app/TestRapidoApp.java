package com.thunsaker.rapido.app;

import java.util.Collections;
import java.util.List;

public class TestRapidoApp extends RapidoApp {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected List<Object> getAppModules() {
        return Collections.<Object>singletonList(
                new TestRapidoAppModule());
    }

    @Override
    protected boolean isInUnitTests() {
        return true;
    }
}