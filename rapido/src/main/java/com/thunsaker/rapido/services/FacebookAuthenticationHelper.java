package com.thunsaker.rapido.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.Facebook.ServiceListener;
import com.facebook.android.FacebookError;
import com.thunsaker.rapido.util.PreferencesHelper;

import java.io.IOException;
import java.net.MalformedURLException;

public class FacebookAuthenticationHelper {
	// TODO: Update to latest Facebook SDK
	final String TAG = getClass().getName();
	public static Facebook facebook = new Facebook(AuthHelper.FACEBOOK_AUTH_ID);
	//Session facebookSession;
	private Activity myActivity;
	private Context myContext;

	public FacebookAuthenticationHelper(Activity activity, Context context) {
		myActivity = activity;
		myContext = context;
		/*facebookSession = new Session(context);*/
	}

	// Authentication
	public void AuthenticateWithFacebook() {


		// TODO: Add "publish_checkin" permission or let the Facebook developer console handle it.
		facebook.authorize(myActivity, new String[] { "publish_stream" },
			new DialogListener() {
	            @Override
	            public void onComplete(Bundle values) {
	            	PreferencesHelper.setFacebookKey(myContext, facebook.getAccessToken());
	            	PreferencesHelper.setFacebookExpiration(myContext, facebook.getAccessExpires());
	            	PreferencesHelper.setFacebookEnabled(myContext, true);
	            	PreferencesHelper.setFacebookConnected(myContext, true);
	            }

	            // Add handling for a user rejecting the permissions
	            @Override
	            public void onFacebookError(FacebookError error) {
	            	PreferencesHelper.setFacebookEnabled(myContext, false);
	            	PreferencesHelper.setFacebookConnected(myContext, false);
	            	Log.d(TAG, error.getMessage());
	            }

	            @Override
	            public void onError(DialogError e) {
	            	PreferencesHelper.setFacebookEnabled(myContext, false);
	            	PreferencesHelper.setFacebookConnected(myContext, false);
	            	Log.d(TAG, e.getMessage());
	            }

	            @Override
	            public void onCancel() {
	            	PreferencesHelper.setFacebookEnabled(myContext, false);
	            	PreferencesHelper.setFacebookConnected(myContext, false);
	            	Log.d(TAG, "Cancelled Facebook Auth");
	            }
	        });
	}

    public void SetAccessToken(String key) {
		facebook.setAccessToken(key);
	}

    public void SetAuthorizeCallback(int requestCode, int resultCode, Intent data){
    	facebook.authorizeCallback(requestCode, resultCode, data);
    }

    public void SetAccessExpires(Long expires) {
    	facebook.setAccessExpires(expires);
    }

    public void ExtendAccessTokenIfNeeded(ServiceListener myServiceListener){
    	facebook.extendAccessTokenIfNeeded(myContext, myServiceListener);
    }

    public Boolean IsSessionValid(){
    	return facebook.isSessionValid();
    }

	public static class SignOut extends AsyncTask<Void, Integer, String> {
		Context mySignOutContext;
		FacebookAuthenticationHelper mySignOutFbAuth;
		public SignOut(Context theContext) {
			mySignOutContext = theContext;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				facebook.logout(mySignOutContext);
				PreferencesHelper.setFacebookExpiration(mySignOutContext, Long.valueOf("0"));
				PreferencesHelper.setFacebookEnabled(mySignOutContext, false);
				PreferencesHelper.setFacebookKey(mySignOutContext, "");
				return "You have been signed out of Facebook";
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return "Malformed URL, try again";
			} catch (IOException e) {
				e.printStackTrace();
				return "A problem occured while signing out, try again";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Toast.makeText(mySignOutContext, result, Toast.LENGTH_SHORT).show();
		}
	}
}
