package com.thunsaker.rapido.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.github.mrengineer13.snackbar.SnackBar;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.app.BaseRapidoActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseRapidoActivity {
    @Inject @ForApplication
    Context mContext;

    @Inject
    EventBus mBus;

    @Inject
    Fabric mFabric;

    @Inject
    RapidoPrefsManager mPreferences;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static boolean mTwitterEnabled;
    public static boolean mFacebookEnabled;
    public static boolean mFoursquareEnabled;
    public static boolean mPlusEnabled;
    public static boolean mBitlyEnabled;

    FragmentManager mFragmentManager;
    private MainFragment mMainFragment;
    private static final String TAG_MAIN_FRAGMENT = "main_fragment";

    public static Location currentLocation;

    public static String REPOST_FACEBOOK = "RAPIDO_REPOST_FACEBOOK";
    public static String REPOST_TWITTER = "RAPIDO_REPOST_TWITTER";
    public static String REPOST_ALL = "RAPIDO_REPOST_ALL";
    public static String REPOST_FOURSQUARE = "RAPIDO_REPOST_FOURSQUARE";
    public static String REPOST_GOOGLE_PLUS = "RAPIDO_REPOST_GOOGLE_PLUS";
    public static String REPOST_APP_DOT_NET = "RAPIDO_REPOST_APP_DOT_NET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        if(mBus != null && !mBus.isRegistered(this))
//            mBus.register(this);

        setSupportActionBar(mToolbar);
        setTitle(null);

        SetupAccounts();

        String receivedText = "";

        // Handle Received Text From Share Action
        if(getIntent() != null) {
            Intent receivedIntent = getIntent();
            String action = receivedIntent.getAction();
            if (Intent.ACTION_SEND.equals(action)) {
                receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                String subjectText = receivedIntent.getStringExtra(Intent.EXTRA_SUBJECT);
                if(subjectText != null && subjectText.length() > 0)
                    receivedText = String.format("%s - %s", receivedIntent.getStringExtra(Intent.EXTRA_SUBJECT), receivedText);
            }
        }

        mFragmentManager = getSupportFragmentManager();

        mMainFragment = (MainFragment) mFragmentManager.findFragmentByTag(TAG_MAIN_FRAGMENT);

        if(mMainFragment == null) {
            if(receivedText != null && receivedText.length() > 0)
                mMainFragment = MainFragment.newInstance(receivedText);
            else
                mMainFragment = MainFragment.newInstance();
        }

        if(!mMainFragment.isInLayout()) {
            mFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_top, mMainFragment, TAG_MAIN_FRAGMENT)
                    .commit();
        }
    }

    private void SetupAccounts() {
        mFacebookEnabled = mPreferences.facebookEnabled().getOr(false);
        mTwitterEnabled = mPreferences.twitterEnabled().getOr(false);
        mPlusEnabled = mPreferences.googleEnabled().getOr(false);
        mFoursquareEnabled = mPreferences.foursquareEnabled().getOr(false);
        mBitlyEnabled = mPreferences.bitlyEnabled().getOr(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            PopSnackBar("About Screen");
            // TODO: Add about libs here
            new LibsBuilder()
                    .withLibraries("Eventbus","Dagger","LeakCanary","Retrofit","Butterknife",
                            "OkHttp","rxAndroid","FloatingActionButton","foursquareAndroidOauth",
                            "RxBinding")
                    .withAutoDetect(true)
                    .withLicenseShown(true)
                    .withVersionShown(true)
                    .withActivityTitle(getString(R.string.action_about))
                    .withActivityTheme(R.style.Theme_Rapido_White)
                    .start(MainActivity.this);
            return true;
        } else if (id == R.id.action_sign_out) {
            PopSnackBar("Sign Out of All the Things!");
//            startActivity(new Intent(mContext, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void PopSnackBar(String message) {
        new SnackBar.Builder(this)
                .withMessage(message)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mMainFragment != null)
            mMainFragment.onActivityResult(requestCode, resultCode, data);
    }
}