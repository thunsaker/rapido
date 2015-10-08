package com.thunsaker.rapido.app;

import android.app.Activity;
import android.content.Context;

import com.thunsaker.android.common.annotations.ForActivity;
import com.thunsaker.rapido.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = true,
        library = true,
        addsTo = RapidoAppModule.class,
        injects = {
                MainActivity.class
        }
)
public class RapidoActivityModule {
    private final Activity mActivity;

    public RapidoActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @Singleton
    @ForActivity
    Context providesActivityContext() {
        return mActivity;
    }

    @Provides
    @Singleton
    Activity providesActivity() {
        return mActivity;
    }
}