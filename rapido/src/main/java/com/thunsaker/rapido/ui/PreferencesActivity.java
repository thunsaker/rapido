package com.thunsaker.rapido.ui;

import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.util.PreferencesHelper;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    final String TAG = "PreferencesActivity";
    boolean useLogo = true;
    boolean showHomeUp = true;

    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(PreferencesHelper.PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);

        if(PreferencesHelper.getFacebookEnabled(getApplicationContext())) {
            CheckBoxPreference fbEnabled = (CheckBoxPreference)findPreference("rapido_facebook_enabled");
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(showHomeUp);
            ab.setDisplayUseLogoEnabled(useLogo);
        }

        Preference clear_prefs = findPreference(getString(R.string.prefs_clear_accounts));
        if(clear_prefs != null)
            clear_prefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ClearTwitterPreferences();
                    ClearFacebookPreferences();
                    ClearFoursquarePreferences();
                    ClearBitlyPreferences();
                    ClearGooglePlusPreferences();
                    ClearAppDotNetPreferences();
                    Toast.makeText(getApplicationContext(), "All Application Settings Cleared", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        final Preference rateRapido = (Preference)findPreference(getString(R.string.prefs_about_rate));
        if(rateRapido != null)
            rateRapido.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.thunsaker.rapido")));
                    return false;
                }
            });

//        final CheckBoxPreference appDotNetPref = (CheckBoxPreference)findPreference(getString(R.string.prefs_app_dot_net_connected));
//        appDotNetPref.setShouldDisableView(true);
//        if(Util.isPro()) {
//        	appDotNetPref.setEnabled(true);
//        } else {
//        	appDotNetPref.setEnabled(false);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GooglePlusAuthorizationActivity.REQUEST_CODE_RESOLVE_ERR: {
                if(resultCode == RESULT_OK) {
                    PreferencesHelper.setGooglePlusConnected(getApplicationContext(), true);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Google Plus auth cancelled.", Toast.LENGTH_SHORT).show();
                    ClearGooglePlusPreferences();
                } else {
                    Toast.makeText(getApplicationContext(), "Google Plus auth failed, try again.", Toast.LENGTH_SHORT).show();
                    ClearGooglePlusPreferences();
                }
                break;
            }

            case MainActivity.REQUEST_FOURSQUARE_AUTH_TOKEN:
                AccessTokenResponse tokenResponse =
                        FoursquareOAuth.getTokenFromResult(resultCode, data);

                if(tokenResponse != null && tokenResponse.getAccessToken() != null) {
                    PreferencesHelper.setFoursquareToken(getApplicationContext(),
                            tokenResponse.getAccessToken());
                    PreferencesHelper.setFoursquareConnected(getApplicationContext(), true);
                } else {
                    if(tokenResponse != null && tokenResponse.getException() != null) {
                        Toast.makeText(getApplicationContext(),
                                "Problem Authenticating: " + tokenResponse.getException().toString(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Problem Authenticating: An unknown error occurred",
                                Toast.LENGTH_SHORT).show();
                    }

                    PreferencesHelper.setFoursquareToken(getApplicationContext(), "");
                    PreferencesHelper.setFoursquareConnected(getApplicationContext(), false);
                }
            default:
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if(getString(R.string.prefs_facebook_connected).equals(key)) {
            Session session = Session.getActiveSession();
            if(PreferencesHelper.getFacebookConnected(getApplicationContext())) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
                    } else {
                        Session.openActiveSession(this, true, statusCallback);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.dialog_no_internets, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!session.isClosed()) {
                    session.closeAndClearTokenInformation();
                }
                ClearFacebookPreferences();
            }
        } else if (getString(R.string.prefs_twitter_connected).equals(key)) {
            if(PreferencesHelper.getTwitterConnected(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), TwitterAuthorizationActivity.class));
            } else {
                Log.i(TAG, "Wiping twitter user tokens");
                ClearTwitterPreferences();
            }
        } else if (getString(R.string.prefs_bitly_connected).equals(key)) {
            if(PreferencesHelper.getBitlyConnected(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), BitlyAuthorizationActivity.class));
            } else {
                Log.i(TAG, "Wiping bitly user tokens");
                ClearBitlyPreferences();
            }
        } else if (getString(R.string.prefs_foursquare_connected).equals(key)) {
            if(PreferencesHelper.getFoursquareConnected(getApplicationContext())) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    Intent intent =
                            FoursquareOAuth.getConnectIntent(
                                    getApplicationContext(),
                                    AuthHelper.FOURSQUARE_CLIENT_ID);
                    startActivityForResult(intent, MainActivity.REQUEST_FOURSQUARE_AUTH);
                } else {
                    // Show Dialog
                    Toast.makeText(getApplicationContext(), R.string.dialog_no_internets, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i(TAG, "Wiping foursquare user tokens");
                ClearFoursquarePreferences();
            }
        } else if (getString(R.string.prefs_google_plus_connected).equals(key)) {
            if(PreferencesHelper.getGooglePlusConnected(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), PlusAuthorizationActivity.class));
            } else {
                Log.i(TAG, "Wiping google plus user tokens");
                ClearGooglePlusPreferences();
            }
        } else if (getString(R.string.prefs_app_dot_net_connected).equals(key)) {
            if(PreferencesHelper.getAppDotNetConnected(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), AppDotNetAuthorizationActivity.class));
            } else {
                Log.i(TAG, "Wiping appNet user tokens");
                ClearAppDotNetPreferences();
            }
        } else if (getString(R.string.prefs_persistent_notification_enabled).equals(key)) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mNotificationRapidoPersistent =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_rapido)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentTitle(getString(R.string.alert_persistent_notification_title))
                            .setContentText(getString(R.string.alert_persistent_notification))
                            .setContentIntent(MainActivity.genericPendingIntent)
                            .setAutoCancel(true)
                            .setOngoing(true);

            if(PreferencesHelper.getPersistentNotificationEnabled(getApplicationContext())) {
                mNotificationManager.notify(MainActivity.RAPIDO_NOTIFICATION_PERSISTENT, mNotificationRapidoPersistent.getNotification());
                Log.i(TAG, "Show Persistent Notification");
            } else {
                mNotificationManager.cancel(MainActivity.RAPIDO_NOTIFICATION_PERSISTENT);
                Log.i(TAG, "Dismissing Persistent Notification");
            }
        }
    }

    public void ClearTwitterPreferences() {
        PreferencesHelper.setTwitterConnected(getApplicationContext(), false);
        PreferencesHelper.setTwitterEnabled(getApplicationContext(), false);
        PreferencesHelper.setTwitterToken(getApplicationContext(), null);
        PreferencesHelper.setTwitterSecret(getApplicationContext(), null);
    }

    public void ClearFacebookPreferences() {
        PreferencesHelper.setFacebookConnected(getApplicationContext(), false);
        PreferencesHelper.setFacebookEnabled(getApplicationContext(), false);
        PreferencesHelper.setFacebookKey(getApplicationContext(), null);
    }

    public void ClearFoursquarePreferences() {
        PreferencesHelper.setFoursquareConnected(getApplicationContext(), false);
        PreferencesHelper.setFoursquareEnabled(getApplicationContext(), false);
        PreferencesHelper.setFoursquareToken(getApplicationContext(), null);
    }

    public void ClearBitlyPreferences() {
        PreferencesHelper.setBitlyConnected(getApplicationContext(), false);
        PreferencesHelper.setBitlyToken(getApplicationContext(), null);
        PreferencesHelper.setBitlyApiKey(getApplicationContext(), null);
        PreferencesHelper.setBitlyLogin(getApplicationContext(), null);
    }

    public void ClearGooglePlusPreferences() {
        PreferencesHelper.setGooglePlusConnected(getApplicationContext(), false);
        PreferencesHelper.setGooglePlusEnabled(getApplicationContext(), false);
    }

    public void ClearAppDotNetPreferences() {
        PreferencesHelper.setAppDotNetConnected(getApplicationContext(), false);
        PreferencesHelper.setAppDotNetEnabled(getApplicationContext(), false);
        PreferencesHelper.setAppDotNetToken(getApplicationContext(), null);
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                PreferencesHelper.setFacebookConnected(getApplicationContext(), true);
                PreferencesHelper.setFacebookKey(getApplicationContext(), session.getAccessToken());
                PreferencesHelper.setFacebookEnabled(getApplicationContext(), true);
            }
        }
    }
}