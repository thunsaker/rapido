package com.thunsaker.rapido.app;

import com.thunsaker.android.common.dagger.BaseActivity;

public class BaseRapidoActivity extends BaseActivity {
    @Override
    protected Object[] getActivityModules() {
        return new Object[] {
                new RapidoActivityModule(this)
        };
    }
}