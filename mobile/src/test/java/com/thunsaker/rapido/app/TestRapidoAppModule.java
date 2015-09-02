package com.thunsaker.rapido.app;

import com.thunsaker.rapido.MainActivityTest;

import dagger.Module;

@Module(
    injects = {
        TestRapidoApp.class,
        MainActivityTest.class
    },
    includes = RapidoAppModule.class,
    library = true)

public class TestRapidoAppModule {
    public TestRapidoAppModule() {}
}