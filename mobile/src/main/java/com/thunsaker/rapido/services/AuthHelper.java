package com.thunsaker.rapido.services;

public class AuthHelper {
    // Twitter
    public final static String TWITTER_KEY = System.getenv("RAPIDO_TWIT_KEY");
    public final static String TWITTER_SECRET = System.getenv("RAPIDO_TWIT_SECRET");

    // Bit.ly
    public final static String BITLY_CLIENT_ID = System.getenv("RAPIDO_BIT_ID");
    public final static String BITLY_CLIENT_SECRET = System.getenv("RAPIDO_BIT_SECRET");
    public final static String BITLY_REDIRECT_URL = "http://thomashunsaker.com/apps/rapido";

    // Foursquare
    public final static String FOURSQUARE_CLIENT_ID = System.getenv("RAPIDO_FOUR_ID");
    public final static String FOURSQUARE_CLIENT_SECRET = System.getenv("RAPIDO_FOUR_SECRET");
}
