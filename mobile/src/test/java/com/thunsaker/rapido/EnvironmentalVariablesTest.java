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
        Assert.assertNotNull(BuildConfig.TWITTER_KEY);
        Assert.assertNotNull(BuildConfig.TWITTER_SECRET);
    }

    @Test
    public void hasFoursquareKeys() throws Exception {
        Assert.assertNotNull(BuildConfig.FOURSQUARE_ID);
        Assert.assertNotNull(BuildConfig.FOURSQUARE_SECRET);
    }

    @Test
    public void hasBitlyKeys() throws Exception {
        Assert.assertNotNull(BuildConfig.BITLY_ID);
        Assert.assertNotNull(BuildConfig.BITLY_SECRET);
    }
}
