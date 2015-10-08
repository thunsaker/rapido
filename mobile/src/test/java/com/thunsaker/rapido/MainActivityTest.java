package com.thunsaker.rapido;

import android.os.Build;

import com.thunsaker.rapido.app.TestRapidoApp;
import com.thunsaker.rapido.shadows.OutlineShadow;
import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.ui.MainFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "com.thunsaker.rapido",
        shadows = OutlineShadow.class,
        application = TestRapidoApp.class)
@RunWith(RobolectricGradleTestRunner.class)

public class MainActivityTest {
    @Inject
    RapidoPrefsManager mPrefs;

    private static final String TAG_MAIN_FRAGMENT = "main_fragment";
    private MainActivity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
        activity.inject(this);
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveMainFragment() throws Exception {
        MainFragment mainFragment =
                (MainFragment)activity.getSupportFragmentManager()
                        .findFragmentByTag(TAG_MAIN_FRAGMENT);
        assertNotNull(mainFragment);
    }

    @Test
    public void shouldProvideRapidoPrefsManager() throws Exception {
        assertNotNull(mPrefs);
    }

    @After
    public void teardown() throws Exception {
        activity = null;
    }
}