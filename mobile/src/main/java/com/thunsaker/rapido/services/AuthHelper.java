package com.thunsaker.rapido.services;

import com.thunsaker.rapido.BuildConfig;

public class AuthHelper {
    // Twitter
    public final static String TWITTER_KEY = BuildConfig.TWITTER_KEY;
    public final static String TWITTER_SECRET = BuildConfig.TWITTER_SECRET;

    // Bit.ly
    public final static String BITLY_CLIENT_ID = BuildConfig.BITLY_ID;
    public final static String BITLY_CLIENT_SECRET = BuildConfig.BITLY_SECRET;
    public final static String BITLY_REDIRECT_URL = "http://thomashunsaker.com/apps/rapido";

    // Foursquare
    public final static String FOURSQUARE_CLIENT_ID = BuildConfig.FOURSQUARE_ID;
    public final static String FOURSQUARE_CLIENT_SECRET = BuildConfig.FOURSQUARE_SECRET;
}
