package com.thunsaker.rapido.app;

import com.thunsaker.android.common.dagger.BasePreferenceActivity;

public class BaseRapidoPreferenceActivity extends BasePreferenceActivity {
    @Override
    protected Object[] getActivityModules() {
        return new Object[] {
                new RapidoActivityModule(this)
        };
    }
}
