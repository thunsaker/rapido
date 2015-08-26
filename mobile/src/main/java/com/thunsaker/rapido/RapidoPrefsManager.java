package com.thunsaker.rapido;

import android.content.SharedPreferences;

import com.tale.prettysharedpreferences.BooleanEditor;
import com.tale.prettysharedpreferences.PrettySharedPreferences;
import com.tale.prettysharedpreferences.StringEditor;

public class RapidoPrefsManager extends PrettySharedPreferences<RapidoPrefsManager> {
    private SharedPreferences mPreferences;

    public RapidoPrefsManager(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public void ClearAllPreferences() {
        mPreferences.getAll().clear();
    }

    public BooleanEditor<RapidoPrefsManager> twitterEnabled() {
        return getBooleanEditor("twitter_enabled");
    }

    public StringEditor<RapidoPrefsManager> twitterUsername() {
        return getStringEditor("twitter_username");
    }

    public StringEditor<RapidoPrefsManager> twitterAuthToken() {
        return getStringEditor("twitter_auth_token");
    }

    public StringEditor<RapidoPrefsManager> twitterAuthSecret() {
        return getStringEditor("twitter_auth_secret");
    }

    public BooleanEditor<RapidoPrefsManager> googleEnabled() {
        return getBooleanEditor("google_enabled");
    }

    public StringEditor<RapidoPrefsManager> googleUsername() {
        return getStringEditor("google_username");
    }

    public BooleanEditor<RapidoPrefsManager> foursquareEnabled() {
        return getBooleanEditor("foursquare_enabled");
    }

    public StringEditor<RapidoPrefsManager> foursquareUsername() {
        return getStringEditor("foursquare_username");
    }

    public StringEditor<RapidoPrefsManager> foursquareToken() {
        return getStringEditor("foursquare_token");
    }

    public BooleanEditor<RapidoPrefsManager> facebookEnabled() {
        return getBooleanEditor("facebook_enabled");
    }

    public StringEditor<RapidoPrefsManager> facebookUsername() {
        return getStringEditor("facebook_username");
    }

    public StringEditor<RapidoPrefsManager> facebookPendingActionText() {
        return getStringEditor("facebook_pending_action_text");
    }

    public BooleanEditor<RapidoPrefsManager> bitlyEnabled() {
        return getBooleanEditor("bitly_enabled");
    }

    public StringEditor<RapidoPrefsManager> bitlyUsername() {
        return getStringEditor("bitly_username");
    }

    public StringEditor<RapidoPrefsManager> bitlyToken() {
        return getStringEditor("bitly_token");
    }

    public StringEditor<RapidoPrefsManager> bitlyApiKey() {
        return getStringEditor("bitly_api_key");
    }
}