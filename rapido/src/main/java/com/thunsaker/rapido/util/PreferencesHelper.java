package com.thunsaker.rapido.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.thunsaker.rapido.R;

public class PreferencesHelper {
    public final static String PREFS_NAME = "RapidoPrefs";

    // Facebook Prefs
    public static boolean getFacebookEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_facebook_enabled),
                false);
    }

    public static void setFacebookEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_facebook_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getFacebookConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_facebook_connected),
                false);
    }

    public static void setFacebookConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_facebook_connected),
                newValue);
        prefsEditor.commit();
    }

    public static Long getFacebookExpiration(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(
                context.getString(R.string.prefs_facebook_expiration),
                0);
    }

    public static void setFacebookExpiration(Context context, Long newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putLong(
                context.getString(R.string.prefs_facebook_expiration),
                newValue);
        prefsEditor.commit();
    }

    public static String getFacebookKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_facebook_key),
                null);
    }

    public static void setFacebookKey(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_facebook_key),
                newValue);
        prefsEditor.commit();
    }

    public static String getFacebookName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_facebook_name),
                null);
    }

    public static void setFacebookName(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_facebook_name),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getFacebookDeleteHashtags(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_facebook_hashtags),
                false);
    }

    public static void setFacebookDeleteHashtags(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_facebook_hashtags),
                newValue);
        prefsEditor.commit();
    }

    // Twitter Prefs
    public static boolean getTwitterEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_twitter_enabled),
                false);
    }

    public static void setTwitterEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_twitter_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getTwitterConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_twitter_connected),
                false);
    }

    public static void setTwitterConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_twitter_connected),
                newValue);
        prefsEditor.commit();
    }

    public static String getTwitterToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_twitter_token),
                null);
    }

    public static void setTwitterToken(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_twitter_token),
                newValue);
        prefsEditor.commit();
    }

    public static String getTwitterSecret(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_twitter_secret),
                null);
    }

    public static void setTwitterSecret(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_twitter_secret),
                newValue);
        prefsEditor.commit();
    }

    // Bit.ly Prefs
    public static boolean getBitlyConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_bitly_connected),
                false);
    }

    public static void setBitlyConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_bitly_connected),
                newValue);
        prefsEditor.commit();
    }

    public static String getBitlyToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_bitly_token),
                null);
    }

    public static void setBitlyToken(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_bitly_token),
                newValue);
        prefsEditor.commit();
    }

    public static String getBitlyApiKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_bitly_apikey),
                null);
    }

    public static void setBitlyApiKey(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_bitly_apikey),
                newValue);
        prefsEditor.commit();
    }

    public static String getBitlyLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_bitly_login),
                null);
    }

    public static void setBitlyLogin(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_bitly_login),
                newValue);
        prefsEditor.commit();
    }

    // Foursquare Prefs
    public static boolean getFoursquareEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_foursquare_enabled),
                false);
    }
    public static void setFoursquareEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_foursquare_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getFoursquareConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_foursquare_connected),
                false);
    }
    public static void setFoursquareConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_foursquare_connected),
                newValue);
        prefsEditor.commit();
    }

    public static String getFoursquareToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_foursquare_token),
                null);
    }
    public static void setFoursquareToken(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_foursquare_token),
                newValue);
        prefsEditor.commit();
    }

    public static String getFoursquareApiKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_foursquare_apikey),
                null);
    }
    public static void setFoursquareApiKey(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_foursquare_apikey),
                newValue);
        prefsEditor.commit();
    }

    // Google Plus Prefs
    public static boolean getGooglePlusEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_google_plus_enabled),
                false);
    }
    public static void setGooglePlusEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_google_plus_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getGooglePlusConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_google_plus_connected),
                false);
    }
    public static void setGooglePlusConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_google_plus_connected),
                newValue);
        prefsEditor.commit();
    }

    public static String getGooglePlusAccountName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_google_plus_account_name),
                "");
    }
    public static void setGooglePlusAccountName(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_google_plus_account_name),
                newValue);
        prefsEditor.commit();
    }


    // Misc Prefs
    public static boolean getSendOnEnterEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_sendOnEnter_enabled),
                false);
    }

    public static void setSendOnEnterEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_sendOnEnter_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getPersistentNotificationEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_persistent_notification_enabled),
                false);
    }

    public static void setPersistentNotificationEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_persistent_notification_enabled),
                newValue);
        prefsEditor.commit();
    }

	// app.net Prefs
    public static boolean getAppDotNetEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.rapido_app_dot_net_enabled),
                false);
    }
    public static void setAppDotNetEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.rapido_app_dot_net_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getAppDotNetConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_app_dot_net_connected),
                false);
    }
    public static void setAppDotNetConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_app_dot_net_connected),
                newValue);
        prefsEditor.commit();
    }

    public static String getAppDotNetToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_app_dot_net_token),
                null);
    }
    public static void setAppDotNetToken(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_app_dot_net_token),
                newValue);
        prefsEditor.commit();
    }

    public static String getAppDotNetApiKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_app_dot_net_apikey),
                null);
    }
    public static void setAppDotNetApiKey(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_app_dot_net_apikey),
                newValue);
        prefsEditor.commit();
    }
}