package com.thunsaker.rapido;

import android.os.Build;

import com.thunsaker.rapido.app.TestRapidoApp;
import com.thunsaker.rapido.shadows.OutlineShadow;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "com.thunsaker.rapido",
        shadows = OutlineShadow.class,
        application = TestRapidoApp.class)
@RunWith(RobolectricGradleTestRunner.class)

public class EnvironmentalVariablesTest {
    @Test
    public void hasTwitterKeys() throws Exception {
        Assert.assertTrue(System.getenv("RAPIDO_TWIT_KEY") != null);
        Assert.assertTrue(System.getenv("RAPIDO_TWIT_SECRET") != null);
    }

    @Test
    public void hasFoursquareKeys() throws Exception {
        Assert.assertTrue(System.getenv("RAPIDO_FOUR_ID") != null);
        Assert.assertTrue(System.getenv("RAPIDO_FOUR_SECRET") != null);
    }

    @Test
    public void hasBitlyKeys() throws Exception {
        Assert.assertTrue(System.getenv("RAPIDO_BIT_ID") != null);
        Assert.assertTrue(System.getenv("RAPIDO_BIT_SECRET") != null);
    }
}
