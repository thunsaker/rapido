package com.thunsaker.rapido.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.thunsaker.rapido.util.PreferencesHelper;

import java.io.IOException;

public class GooglePlusAuthorizationActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private boolean useLogo = true;
	private boolean showHomeUp = true;

	// Google+ Experiment
	static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private ProgressDialog mConnectionProgressDialog;
	public static PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(showHomeUp);
		ab.setDisplayUseLogoEnabled(useLogo);
		Log.i("gplus auth", "Setup the activity.");

		mConnectionResult = null;

		SetupPlusClient();
		AttemptSignIn();
	}

	public void SetupPlusClient() {
		Log.i("gplus auth", "Setting up the plus client.");
		mPlusClient = new PlusClient.Builder(this, this, this)
			.setActions("http://schemas.google.com/AddActivity")
			.build();
		MainActivity.myPlusClient = mPlusClient;
	}

	public void AttemptSignIn() {
		if (!mPlusClient.isConnected()) {
			Log.i("gplus auth", "Attempting to sign in.");
			mConnectionProgressDialog = new ProgressDialog(this);
			mConnectionProgressDialog.setMessage("Signing in...");
			mPlusClient.disconnect();
			mPlusClient.connect();
		} else
			Log.i("gplus auth", "Already connected...");
	}

	public static void SignOut() {
		Log.i("gplus auth", "Attempting to sign out.");
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
            mPlusClient.connect();
//            mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
//				@Override
//				public void onAccessRevoked(ConnectionResult status) {
//					PreferencesHelper.setGooglePlusConnected(getApplicationContext(), false);
//				}
//			});
        }
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mPlusClient != null) {
			AttemptSignIn();
		} else {
			SetupPlusClient();
			AttemptSignIn();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		SignOut();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(Activity.RESULT_CANCELED);
		SignOut();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			mConnectionProgressDialog.dismiss();

			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (Exception e) {
					mPlusClient.connect();
				}
			}
		}

		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
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
	public void onConnected(Bundle connectionHint) {
		Log.i("Google Auth", "On connected.");
		final Context context = this.getApplicationContext();
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				//String[] scope = new String[] { Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE };
				String scope = "oauth2:" + Scopes.PLUS_LOGIN;
				Log.i("Google Auth", "Before the auth!");
				try {
					Log.i("Google Auth", "Indside the auth.");
					// We can retrieve the token to check via
					// tokeninfo or to pass to a service-side
					// application.
//					String token = GoogleAuthUtil.getToken(context, mPlusClient.getAccountName(), scope);
					String token = GoogleAuthUtil.getTokenWithNotification(context, mPlusClient.getAccountName(), scope, null);
				} catch (UserRecoverableNotifiedException e) {
					Log.i("Google Auth", "UserRecoverableNotifiedException.");
					e.printStackTrace();
//				} catch (UserRecoverableAuthException e) {
//					Log.i("Google Auth", "UserRecoverableAuthException.");
//					// This error is recoverable, so we could fix this
//					// by displaying the intent to the user.
//					e.printStackTrace();
				} catch (IOException e) {
					Log.i("Google Auth", "IOException.");
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					Log.i("Google Auth", "GoogleAuthException");
					e.printStackTrace();
				} catch (Exception e) {
					Log.i("Google Auth", "Exception");
					e.printStackTrace();
				}
		    return null;
			}
		};
		task.execute((Void) null);

		PreferencesHelper.setGooglePlusConnected(getApplicationContext(), true);
		Toast.makeText(this, "User is connected!", Toast.LENGTH_SHORT).show();
		setResult(Activity.RESULT_OK);
		finish();
	}

	@Override
	public void onDisconnected() {
		Log.i("Google Auth", "On Disconnected.");
		PreferencesHelper.setGooglePlusEnabled(getApplicationContext(), true);
		Toast.makeText(this, "User is has disconnected :(", Toast.LENGTH_SHORT)
				.show();

		Intent data = new Intent();
		data.putExtra("GOOGLE_PLUS_AUTH_FAILURE", "Google Plus failed to authorize for some reason");
		setResult(Activity.RESULT_CANCELED, data);
		finish();
	}
}
