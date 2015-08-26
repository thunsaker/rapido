package com.thunsaker.rapido.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.thunsaker.android.common.util.Util;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.app.BaseRapidoActivity;
import com.thunsaker.rapido.data.events.BitlyAuthEvent;
import com.thunsaker.rapido.services.*;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BitlyAuthActivity extends BaseRapidoActivity {
    public static final int REQUEST_CODE_BITLY_SIGN_IN = 8000;

    final String TAG = "BitlyAuthActivity";

    @Inject
    EventBus mBus;

	@Inject
	BitlyService mBitlyService;

    @Inject
    RapidoPrefsManager mPreferences;

    @Bind(R.id.toolbar_bitly) Toolbar mToolbar;
    @Bind(R.id.progress_bitly) ProgressBar mProgress;
    @Bind(R.id.webview_bitly) WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bitly);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setTitle(null);
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
            this.setResult(RESULT_CANCELED);
            this.finish();
	    }

        return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onResume() {
		super.onResume();

		mProgress.setVisibility(View.VISIBLE);
		mWebView.getSettings().setJavaScriptEnabled(true);

		String authUrl = String.format("%s?client_id=%s&redirect_uri=%s",
				BitlyPrefs.AUTHORIZE_URL, AuthHelper.BITLY_CLIENT_ID, AuthHelper.BITLY_REDIRECT_URL);

		try {
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					if (url.startsWith(AuthHelper.BITLY_REDIRECT_URL)) {
						try {
							if (url.contains("code=")) {
								String requestToken = Util.extractParamFromUrl(url, "code");

								mBitlyService
										.getAccessToken(
												"Non est corpus.",
												requestToken,
												AuthHelper.BITLY_CLIENT_ID,
												AuthHelper.BITLY_CLIENT_SECRET,
												AuthHelper.BITLY_REDIRECT_URL)
										.subscribeOn(Schedulers.io())
										.observeOn(AndroidSchedulers.mainThread())
										.onErrorReturn(new Func1<Throwable, Response>() {
											@Override
											public Response call(Throwable throwable) {
												mPreferences.bitlyEnabled().put(false).commit();
												mBus.post(new BitlyAuthEvent(false, ""));
												return null;
											}
										})
										.subscribe(new Action1<Response>() {
											@Override
											public void call(Response response) {
												boolean result = false;
												if (response != null) {
													String responseBody =
															new String(
																	((TypedByteArray) response.getBody()).getBytes());
													String accessToken =
															Util.extractParamFromUrl(responseBody, "access_token");
													String login = Util.extractParamFromUrl(responseBody, "login");
													// Deprecated
													String apikey = Util.extractParamFromUrl(responseBody, "apiKey");

													if (accessToken.length() > 0 && login.length() > 0) {
														mPreferences
																.bitlyEnabled().put(true)
																.bitlyToken().put(accessToken)
																.bitlyUsername().put(login)
																.bitlyApiKey().put(apikey)
																.commit();
														result = true;
													}
												}

												mBus.post(new BitlyAuthEvent(result, ""));
												MainActivity.mBitlyEnabled = result;
											}
										});

								setResult(RESULT_OK);
								finish();
							} else if (url.contains("error=")) {
								setResult(RESULT_CANCELED);
								finish();
							}
						} catch (Exception e) {
							Log.i(TAG, "IOException: " + e.getMessage());
							e.printStackTrace();
						}
					}
				}
			});

			mWebView.loadUrl(authUrl);
			mWebView.requestFocus();
			mProgress.setVisibility(View.GONE);
			mWebView.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(null);
	}
}