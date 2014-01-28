package com.thunsaker.rapido.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.thunsaker.rapido.util.PreferencesHelper;

public class PlusAuthorizationActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {
	private boolean useLogo = true;
	private boolean showHomeUp = true;

	private static final String TAG = "ExampleActivity";
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(showHomeUp);
		ab.setDisplayUseLogoEnabled(useLogo);

		Log.i("PlusAuthorizationActivity", "On Create, before plus client");

		mPlusClient = new PlusClient.Builder(this, this, this)
				.setActions("http://schemas.google.com/AddActivity").build();

		Log.i("PlusAuthorizationActivity", "On Create, after plusclient builder");

		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
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
	protected void onStart() {
		super.onStart();
		Log.i("PlusAuthorizationActivity", "On Start");
		if(!mPlusClient.isConnected())
			mPlusClient.connect();
		else
			Log.i("PlusAuthorizationActivity", "client is connected?");
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}

		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		} else {
			ClearGooglePlusPrefs();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		String accountName = mPlusClient.getAccountName();
		Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG)
				.show();
		PreferencesHelper.setGooglePlusConnected(getApplicationContext(), true);
		PreferencesHelper.setGooglePlusEnabled(getApplicationContext(), true);
		PreferencesHelper.setGooglePlusAccountName(getApplicationContext(), accountName);
		finish();
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "disconnected");
		ClearGooglePlusPrefs();
	}

	public void ClearGooglePlusPrefs() {
		PreferencesHelper.setGooglePlusConnected(getApplicationContext(), false);
		PreferencesHelper.setGooglePlusEnabled(getApplicationContext(), false);
		PreferencesHelper.setGooglePlusAccountName(getApplicationContext(), "");
	}
}
