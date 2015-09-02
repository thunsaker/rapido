package com.thunsaker.rapido;

import android.os.Build;

import com.thunsaker.rapido.app.TestRapidoApp;
import com.thunsaker.rapido.shadows.OutlineShadow;
import com.thunsaker.rapido.ui.MainFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "com.thunsaker.rapido",
        shadows = OutlineShadow.class,
        application = TestRapidoApp.class)
@RunWith(RobolectricGradleTestRunner.class)

public class MainFragmentTest {
    private MainFragment fragment;

    @Before
    public void setup() throws Exception {
        fragment = MainFragment.newInstance();
        startFragment(fragment);
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(fragment);
    }

    @After
    public void teardown() throws Exception {
        fragment = null;
    }
}