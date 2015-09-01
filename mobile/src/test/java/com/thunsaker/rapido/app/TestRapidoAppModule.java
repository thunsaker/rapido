package com.thunsaker.rapido.app;

import android.content.Context;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.services.FacebookTasks;
import com.thunsaker.rapido.services.TwitterTasks;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = { TestRapidoApp.class },
        includes = RapidoAppModule.class,
        overrides = true)

public class TestRapidoAppModule {
        @Provides
        @Singleton
        TwitterTasks providesTwitterTasks(@ForApplication Context mContext) {
                return new TwitterTasks((TestRapidoApp)mContext);
        }

        @Provides
        @Singleton
        FacebookTasks providesFacebookTasks(@ForApplication Context mContext) {
                return new FacebookTasks((TestRapidoApp)mContext);
        }
}
