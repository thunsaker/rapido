package com.thunsaker.rapido.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.util.PreferencesHelper;
import com.thunsaker.rapido.util.QueryStringParser;
import com.thunsaker.rapido.util.Util;

public class FoursquareAuthorizationActivity extends ActionBarActivity {
	final String TAG = "FoursquareAuthorizationActivity";

	private boolean useLogo = true;
	private boolean showHomeUp = true;

	public static final String ACCESS_URL = "https://foursquare.com/oauth2/access_token";
	public static final String AUTHORIZE_URL = "https://foursquare.com/oauth2/authorize";

	public ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(showHomeUp);
		ab.setDisplayUseLogoEnabled(useLogo);

		loadingDialog = ProgressDialog.show(
				FoursquareAuthorizationActivity.this,
				getString(R.string.dialog_please_wait), String.format(
						getString(R.string.dialog_loading),
						getString(R.string.foursquare)), true, // Undefined progress
				true, // Allow canceling of operation
				new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.auth_cancelled),
								Toast.LENGTH_SHORT).show();
					}
				});
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

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onResume() {
		super.onResume();

		WebView webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setVisibility(View.VISIBLE);
		setContentView(webView);

		String authUrl = String.format("%s?client_id=%s&response_type=code&redirect_uri=%s",
				AUTHORIZE_URL, AuthHelper.FOURSQUARE_CLIENT_ID,
				AuthHelper.FOURSQUARE_CALLBACK_URL);

		try {
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					if (url.startsWith(AuthHelper.FOURSQUARE_CALLBACK_URL)) {
						try {
							if (url.indexOf("code=") != -1) {
								String requestToken = extractParamFromUrl(url,
										"code");

								// Do http post here...
								String accessUrl = String
										.format("%s?code=%s&client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s",
												ACCESS_URL, requestToken,
												AuthHelper.FOURSQUARE_CLIENT_ID,
												AuthHelper.FOURSQUARE_CLIENT_SECRET,
												AuthHelper.FOURSQUARE_CALLBACK_URL);

								new TokenFetcher(
										FoursquareAuthorizationActivity.this,
										accessUrl).execute();

								view.setVisibility(View.INVISIBLE);
								startActivity(new Intent(
										getApplicationContext(),
										MainActivity.class));
							} else if (url.indexOf("error=") != -1) {
								view.setVisibility(View.INVISIBLE);
								finish();
							}
						} catch (Exception e) {
							Log.i(TAG, "IOException: " + e.getMessage());
							e.printStackTrace();
						}
					}
				}
			});

			webView.loadUrl(authUrl);
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

	public class TokenFetcher extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		String myUrl;

		public TokenFetcher(Context theContext, String theUrl) {
			myContext = theContext;
			myUrl = theUrl;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = false;
			String response = Util.getHttpResponse(myUrl, true, Util.contentType, Util.contentType);
			if (response != null) {
				Log.i("FoursquareAuth", "Response: " + response);
				JsonParser jParser = new JsonParser();
				JsonObject jObject = (JsonObject) jParser.parse(response);
				String accessToken = jObject.get("access_token").getAsString();

				if (accessToken.length() > 0) {  // && login.length() > 0 && apikey.length() > 0) {
					result = true;
					PreferencesHelper.setFoursquareToken(myContext, accessToken.trim());
					PreferencesHelper.setFoursquareConnected(myContext, result);
				}
			} else {
				PreferencesHelper.setFoursquareConnected(myContext, result);
			}

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Toast.makeText(myContext, "Foursquare Account Authorized", Toast.LENGTH_SHORT).show();
		}
	}
}
