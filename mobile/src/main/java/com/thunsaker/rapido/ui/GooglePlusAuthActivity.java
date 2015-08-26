package com.thunsaker.rapido.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.app.BaseRapidoActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class GooglePlusAuthActivity extends BaseRapidoActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static GoogleApiClient mGoogleClient;
    public static final int REQUEST_CODE_GOOGLE_SIGN_IN = 6000;
    private boolean mIntentInProgress;
    private boolean mIsResolving = false;

    private ConnectionResult mGoogleSignOnConnectionResult;

    @Inject @ForApplication
    Context mContext;

    @Inject
    EventBus mBus;

    @Inject
    RapidoPrefsManager mPreferences;

    @Bind(R.id.toolbar_plus) Toolbar mToolbar;
//    @Bind(R.id.google_login_button) SignInButton mGoogleLogin;

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Explode());
        }

        setContentView(R.layout.activity_plus);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setTitle(null);

        SetupGooglePlusLogin();
    }

    private void SetupGooglePlusLogin() {
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_close) {
            if(mGoogleClient.isConnected()) {
                mGoogleClient.unregisterConnectionCallbacks(this);
                mGoogleClient.unregisterConnectionFailedListener(this);
            }

            MainActivity.mPlusEnabled = false;

            this.setResult(RESULT_CANCELED);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleClient.isConnected())
            mGoogleClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        String googleUsername = Plus.AccountApi.getAccountName(mGoogleClient);
        mPreferences
                .googleEnabled().put(true)
                .googleUsername().put(googleUsername)
                .apply();
        MainActivity.mPlusEnabled = mPreferences.googleEnabled().getOr(false);

        this.setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!mIntentInProgress) {
            mGoogleSignOnConnectionResult = connectionResult;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mGoogleSignOnConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mGoogleSignOnConnectionResult.startResolutionForResult(this, REQUEST_CODE_GOOGLE_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default state and
                // attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleClient.connect();
                Log.w("GooglePlusAutActivity", "Error sending the resolution Intent, connect() again.");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            mIntentInProgress = false;

            if(resultCode == RESULT_OK) {
                if(!mGoogleClient.isConnected() && !mGoogleClient.isConnecting()) {
                    mGoogleClient.connect();
                }
            } else {
                if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(mContext, "Sign in failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}