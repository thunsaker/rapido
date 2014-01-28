package com.thunsaker.rapido.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.PlusClient;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.ShortUrl;
import com.thunsaker.rapido.services.AppDotNetHelper;
import com.thunsaker.rapido.services.BitlyHelper;
import com.thunsaker.rapido.services.FacebookHelper;
import com.thunsaker.rapido.services.FoursquareHelper;
import com.thunsaker.rapido.services.GooglePlusHelper;
import com.thunsaker.rapido.services.TwitterHelper;
import com.thunsaker.rapido.ui.MainActivity;
import com.twitter.Extractor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Util {
	private static final String LOG_TAG = "Util";
	public static final int FACEBOOK_UPDATE = 0;
	public static final int TWITTER_UPDATE = 1;
	public static final int FOURSQUARE_UPDATE = 2;
	public static final int GOOGLEPLUS_UPDATE = 3;
	public static final int APPDOTNET_POST = 4;

	public static final String ENCODER_CHARSET = "UTF-8";

	public static final int CHAR_LIMIT_FACEBOOK = 1000;
	public static final int CHAR_LIMIT_TWITTER = 140;
	public static final int CHAR_LIMIT_APP_DOT_NET = 256;

	public static String contentType = "json/application";

	public static ShortUrl ShortenUrl(String urlToShorten, Context myContext) {
		try {
			if (Util.HasInternet(myContext)) {
				//String longUrl = URLEncoder.encode(urlToShorten);
				String longUrl = urlToShorten;
				ShortUrl theShortenedUrl = new ShortUrl();

				if (longUrl != "") {
					String token = PreferencesHelper.getBitlyToken(myContext);
					theShortenedUrl = BitlyHelper.getShortUrl(longUrl, token);

					if (theShortenedUrl != null && theShortenedUrl.getUrl() != "") {
						// Url shortened properly
						return theShortenedUrl;
					} else {
						// Error shorten
					}
				} else {
					// Error no url to shorten
				}
			} else {
				// Error
			}

			return null;
		} catch (Exception ex) {
			Log.e(LOG_TAG + ".shortenUrl",
					"shortenUrl() - Exception: " + ex.getMessage());
			return null;
		}
	}

	public static String getHttpResponse(String url, String contentType, String accepts) {
		return getHttpResponse(url, false, contentType, accepts);
	}

	public static String getHttpResponse(String url, Boolean isHttpPost, String contentType, String accepts) {
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response;

		try {
			if(isHttpPost)
				response = httpclient.execute(httpPost);
			else
				response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}
		} catch (ClientProtocolException e) {
			Log.e(LOG_TAG, "There was a protocol based error", e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "There was an IO Stream related error", e);
		}
		return result;
	}

	public static String getHttpResponseWithData(String url, Boolean isHttpPost, String contentType, String accepts, String data) {
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response;

		try {
			httpPost.setHeader("Content-Type","application/json");
			httpPost.setEntity(new StringEntity(data, Util.ENCODER_CHARSET));

			if(isHttpPost)
				response = httpclient.execute(httpPost);
			else
				response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(LOG_TAG, "There was a protocol based error", e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "There was an IO Stream related error", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("static-access")
	public static Boolean HasInternet(Context myContext) {
		Boolean HasConnection = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) myContext
				.getSystemService(myContext.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			State myState = activeNetworkInfo.getState();
			if (myState == State.CONNECTED || myState == State.CONNECTING) {
				HasConnection = true;
			}
		}
		return HasConnection;
	}

	public static List<String> GetLinksInText(String textToCheck) {
		Extractor twitterExtractor = new Extractor();
		List<String> myLinks = twitterExtractor.extractURLs(textToCheck);

		if(myLinks != null && !myLinks.isEmpty()) {
			return myLinks;
		}

		return null;
	}

	public static List<String> GetHashtagsInText(String textToCheck) {
		Extractor twitterExtractor = new Extractor();
		List<String> myHashtags = twitterExtractor.extractHashtags(textToCheck);

		if(myHashtags != null && !myHashtags.isEmpty()) {
			return myHashtags;
		}

		return null;
	}
	/*
	 *	Rapido Update Task
	 *
	 */
	public static class UpdateStatus extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		String myUpdate;
		Boolean myFacebook;
		Boolean myTwitter;
		Boolean myBitly;
		Boolean myFoursquare;
		Integer myService;
		LatLng myLatLng;
		String myVenueId;
		Boolean myGooglePlus;
		String myPrimaryLink;
		Activity myActivity;
		PlusClient myPlusClient;
		Boolean myAppDotNet;

//		/**
//		 * Facebook Update
//		 */
//		public UpdateStatus(Context theContext, Integer theService, String theUpdate, Boolean bitly, String theVenueId, LatLng theLatLng) {
//			myContext = theContext;
//			myUpdate = theUpdate;
//			myService = theService;
//			myLatLng = theLatLng;
//
//			myTwitter = false;
//
//			myFacebook = true;
//
//			myBitly = bitly;
//
//			myFoursquare = false;
//			myVenueId = "";
//
//			myGooglePlus = false;
//			myPrimaryLink = "";
//			myActivity = null;
//			myPlusClient = null;
//
//			myAppDotNet = false;
//		}

		/**
		 * Twitter Update
		 */
		public UpdateStatus(Context theContext, Integer theService, String theUpdate, Boolean bitly, LatLng theLatLng) {
			myContext = theContext;
			myUpdate = theUpdate;
			myService = theService;
			myLatLng = theLatLng;

			myTwitter = true;

			myFacebook = false;

			myBitly = bitly;

			myFoursquare = false;
            myVenueId = "";

			myGooglePlus = false;
			myPrimaryLink = "";
			myActivity = null;
			myPlusClient = null;

			myAppDotNet = false;
		}

		/**
		 * Facebook, Foursquare & app.net Update
		 */
		public UpdateStatus(Context theContext, Integer theService, String theUpdate, Boolean bitly, LatLng theLatLng, String theVenueId) {
			myContext = theContext;
			myUpdate = theUpdate;
			myService = theService;
			myLatLng = theLatLng;

			myTwitter = false;
            myFoursquare = false;
            myAppDotNet = false;
            myFacebook = false;

			myBitly = bitly;

            switch (theService) {
                case FOURSQUARE_UPDATE : {
                    myFoursquare = true;
                    break;
                }
                case FACEBOOK_UPDATE : {
                    myFacebook = true;
                    break;
                }
                case APPDOTNET_POST : {
                    myAppDotNet = true;
                    break;
                }
            }

			myVenueId = theVenueId;

			myGooglePlus = false;
			myPrimaryLink = "";
			myActivity = null;
			myPlusClient = null;
		}

		/*
		 * Google Plus Update
		 */
		public UpdateStatus(Context theContext, Integer theService, String theUpdate, Boolean bitly, LatLng theLatLng, String thePrimaryLink, Activity theActivity, PlusClient thePlusClient) {
			myContext = theContext;
			myUpdate = theUpdate;
			myService = theService;
			myLatLng = theLatLng;

			myTwitter = false;

			myFacebook = false;

			myBitly = bitly;

			myFoursquare = false;
			myVenueId = "";

			myGooglePlus = true;
			myPrimaryLink = thePrimaryLink;
			myActivity = theActivity;
			myPlusClient = thePlusClient;

			myAppDotNet = false;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			NotificationManager mNotificationManager =
					(NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

			NotificationCompat.Builder mNotificationFacebook =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_facebook_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posting_facebook))
						.setContentIntent(MainActivity.genericPendingIntent);

			NotificationCompat.Builder mNotificationTwitter =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_twitter_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posting_twitter))
						.setContentIntent(MainActivity.genericPendingIntent);

			NotificationCompat.Builder mNotificationBitly =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_bitly_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posting_shortening_bitly))
						.setContentIntent(MainActivity.genericPendingIntent);

			NotificationCompat.Builder mNotificationFoursquare =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_foursquare_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posting_foursquare))
						.setContentIntent(MainActivity.genericPendingIntent);

			NotificationCompat.Builder mNotificationGooglePlus =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_google_plus_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posting_google_plus))
						.setContentIntent(MainActivity.genericPendingIntent);

			NotificationCompat.Builder mNotificationAppDotNet =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_appnet_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posting_app_dot_net))
						.setContentIntent(MainActivity.genericPendingIntent);

			if(myBitly) {
				mNotificationManager.notify(MainActivity.BITLY_NOTIFICATION, mNotificationBitly.getNotification());
				new BitlyHelper.ShortenAndSend(myContext, myService, myUpdate, null, myLatLng, myVenueId, myActivity).execute();
			} else {
				if (myFacebook) {
					try {
						mNotificationManager.notify(MainActivity.FACEBOOK_NOTIFICATION, mNotificationFacebook.getNotification());
						new FacebookHelper.UpdateStatus(myContext, myUpdate, myVenueId).execute();
                        return true;
					} catch (Exception e) {
						Toast.makeText(myContext, "Facebook message not sent, try again", Toast.LENGTH_SHORT).show();
						Log.i(LOG_TAG, "Exception: " + e.getMessage());
					}
				}

				if (myTwitter) {
					try {
						mNotificationManager.notify(MainActivity.TWITTER_NOTIFICATION, mNotificationTwitter.getNotification());
						new TwitterHelper.SendTweet(myContext, myUpdate, myLatLng).execute();
						return true;
					} catch (Exception e) {
						Toast.makeText(myContext, "Twitter message not sent, try again", Toast.LENGTH_SHORT).show();
						Log.i(LOG_TAG, "Exception: " + e.getMessage());
					}
				}

				if(myFoursquare) {
					try {
						mNotificationManager.notify(MainActivity.FOURSQUARE_NOTIFICATION, mNotificationFoursquare.getNotification());
						new FoursquareHelper.PostUserCheckin(myContext, myLatLng, myVenueId, myUpdate).execute();
						return true;
					} catch (Exception e) {
						Toast.makeText(myContext, "Foursquare checkin failed, try again", Toast.LENGTH_SHORT).show();
						Log.i(LOG_TAG, "Exception: " + e.getMessage());
					}
				}

				if(myGooglePlus) {
					try {
//						mNotificationManager.notify(MainActivity.GOOGLE_PLUS_NOTIFICATION, mNotificationGooglePlus.getNotification());
//						if(myLatLng == null) {
							GooglePlusHelper.PopUserMoment(myContext, myUpdate, myLatLng, myPrimaryLink, myActivity);
//						} else {
//							new GooglePlusHelper.PostUserCheckin(myContext, myUpdate, myLatLng, myPrimaryLink, myPlusClient).execute();
//						}
					} catch (Exception e) {
						Toast.makeText(myContext, "Google Plus update failed, try again", Toast.LENGTH_SHORT).show();
						Log.i(LOG_TAG, "Exception: " + e.getMessage());
					}
				}

				if (myAppDotNet) {
					try {
						mNotificationManager.notify(MainActivity.APP_DOT_NET_NOTIFICATION, mNotificationAppDotNet.getNotification());
						new AppDotNetHelper.SendPost(myContext, myLatLng, myVenueId, myUpdate).execute();
						return true;
					} catch (Exception e) {
						Toast.makeText(myContext, "App.net post not sent, try again", Toast.LENGTH_SHORT).show();
						Log.i(LOG_TAG, "Exception: " + e.getMessage());
					}
				}
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// TODO: Move this....
			//Toast.makeText(myContext, result ? "Message posted successfully!" : myContext.getString(R.string.error_not_posted), Toast.LENGTH_SHORT).show();
		}
	}

	public static Boolean isPro() {
		return false;
	}
}