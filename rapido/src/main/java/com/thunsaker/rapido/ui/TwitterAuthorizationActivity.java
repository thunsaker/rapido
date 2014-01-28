package com.thunsaker.rapido.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.services.TwitterHelper;
import com.thunsaker.rapido.util.PreferencesHelper;
import com.thunsaker.rapido.util.QueryStringParser;

public class TwitterAuthorizationActivity extends ActionBarActivity {
	final String TAG = "TwitterAuthorizationActivity";

	private boolean useLogo = true;
    private boolean showHomeUp = true;

	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	public ProgressDialog loadingDialog;
	public Boolean isTempTokenSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(showHomeUp);
        ab.setDisplayUseLogoEnabled(useLogo);
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

		loadingDialog = ProgressDialog.show(
			TwitterAuthorizationActivity.this, getString(R.string.dialog_please_wait),
			String.format(getString(R.string.dialog_loading), getString(R.string.twitter)),
			true, // Undefined progress
			true, // Allow canceling of operation
			new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					Toast.makeText(
							getApplicationContext(),
							getString(R.string.auth_cancelled),
							Toast.LENGTH_SHORT).show();
				}
			});

		new TempTokenFetcher(TwitterAuthorizationActivity.this, this).execute();
	}

	public void SetupTwitterAuthorization(OAuthHmacSigner theSigner, OAuthCredentialsResponse theTempCredentials){
		try {
			WebView webView = new WebView(this);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setClickable(true);
			webView.setVisibility(View.VISIBLE);
			setContentView(webView);
			final OAuthHmacSigner mySigner = theSigner;

			OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(AUTHORIZE_URL);
			authorizeUrl.temporaryToken = theTempCredentials.token;
			String authorizationUrl = authorizeUrl.build();
			webView.setWebViewClient(new WebViewClient(){
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					if(url.startsWith(AuthHelper.TWITTER_CALLBACK_URL)){
						try {
							if(url.indexOf("oauth_token=") != -1) {
								PreferencesHelper.setTwitterToken(getApplicationContext(), "");
								PreferencesHelper.setTwitterSecret(getApplicationContext(), "");

								String requestToken = extractParamFromUrl(url, "oauth_token");
								String verifier = extractParamFromUrl(url, "oauth_verifier");
								mySigner.clientSharedSecret = AuthHelper.TWITTER_SECRET;

								OAuthGetAccessToken accessToken = new OAuthGetAccessToken(ACCESS_URL);
								accessToken.transport = new ApacheHttpTransport();
								accessToken.temporaryToken = requestToken;
								accessToken.signer = mySigner;
								accessToken.consumerKey = AuthHelper.TWITTER_KEY;
								accessToken.verifier = verifier;

								new TwitterHelper.TokenFetcher(TwitterAuthorizationActivity.this, accessToken).execute();
								finish();
							} else if (url.indexOf("error=") != -1) {
								view.setVisibility(View.INVISIBLE);
								Log.i(TAG, "No match: " + url);
								finish();
							}
						} catch (Exception e) {
							Log.i(TAG, "IOException: " + e.getMessage());
							e.printStackTrace();
						}
					}
				}
			});

			webView.loadUrl(authorizationUrl);
			webView.requestFocus();
			loadingDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	private String extractParamFromUrl(String url,String paramName) {
		String queryString = url.substring(url.indexOf("?", 0)+1,url.length());
		QueryStringParser queryStringParser = new QueryStringParser(queryString);
		return queryStringParser.getQueryParamValue(paramName);
	}

	public class TempTokenFetcher extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		TwitterAuthorizationActivity myCaller = null;
		final OAuthHmacSigner signer = new OAuthHmacSigner();
		OAuthCredentialsResponse tempCredentials;

		public TempTokenFetcher(Context theContext, TwitterAuthorizationActivity theCaller) {
			myContext = theContext;
			myCaller = theCaller;
		}


		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				signer.clientSharedSecret = AuthHelper.TWITTER_SECRET;

				OAuthGetTemporaryToken tempToken = new OAuthGetTemporaryToken(REQUEST_URL);
				tempToken.transport = new ApacheHttpTransport();
				tempToken.signer = signer;
				tempToken.consumerKey = AuthHelper.TWITTER_KEY;
				tempToken.callback = AuthHelper.TWITTER_CALLBACK_URL;

				tempCredentials = tempToken.execute();
				signer.tokenSharedSecret = tempCredentials.tokenSecret;
			} catch (Exception e) {
				e.printStackTrace();
				isTempTokenSet = false;
			}
			return isTempTokenSet;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			myCaller.SetupTwitterAuthorization(signer, tempCredentials);
		}
	}
}