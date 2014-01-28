package com.thunsaker.rapido.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.Draft;
import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.util.PreferencesHelper;

import java.io.IOException;
import java.util.Calendar;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterHelper {
	private static final String TAG = "TwitterHelper";

	private static Boolean sendTweet(Context myContext, String msg, LatLng myLatLng) throws Exception {
		if(PreferencesHelper.getTwitterConnected(myContext)) {
			String token = PreferencesHelper.getTwitterToken(myContext) != null ? PreferencesHelper.getTwitterToken(myContext) : "";
			String secret = PreferencesHelper.getTwitterSecret(myContext) != null ? PreferencesHelper.getTwitterSecret(myContext) : "";
			if(token != "" && secret != "") {
				AccessToken at = new AccessToken(token, secret);

//                ConfigurationBuilder cb = new ConfigurationBuilder();
//                cb.setDebugEnabled(true);
//                TwitterFactory tf = new TwitterFactory(cb.build());
//                Twitter twitter = tf.getInstance();

				Twitter twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(AuthHelper.TWITTER_KEY, AuthHelper.TWITTER_SECRET);
				twitter.setOAuthAccessToken(at);
				StatusUpdate myStatusUpdate = new StatusUpdate(msg);

				if(myLatLng != null)
					myStatusUpdate.setLocation(new GeoLocation(myLatLng.latitude, myLatLng.longitude));

				twitter.updateStatus(myStatusUpdate);
				return true;
			}
		}
		return false;
	}

	public static class SendTweet extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		String myMessage;
		LatLng myLatLng;

		public SendTweet(Context theContext, String msg, LatLng theLatLng) {
			myContext = theContext;
			myMessage = msg;
			myLatLng = theLatLng;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				return sendTweet(myContext, myMessage, myLatLng);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			NotificationManager mNotificationManager =
					(NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

			if(result) {
				NotificationCompat.Builder mNotificationTwitterPosted =
						new NotificationCompat.Builder(myContext)
							.setSmallIcon(R.drawable.ic_stat_rapido)
							.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_twitter_large_icon))
							.setContentTitle(myContext.getString(R.string.alert_title))
							.setContentText(myContext.getString(R.string.alert_posted_twitter))
							.setContentIntent(MainActivity.genericPendingIntent);
				mNotificationManager.notify(MainActivity.TWITTER_NOTIFICATION, mNotificationTwitterPosted.getNotification());
				mNotificationManager.cancel(MainActivity.TWITTER_NOTIFICATION);
			} else {
				Intent twitterRepost = new Intent(myContext, MainActivity.class);
				twitterRepost.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", MainActivity.REPOST_TWITTER, myMessage));
				TaskStackBuilder stackBuilder = TaskStackBuilder.from(myContext);
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(twitterRepost);
				PendingIntent repostPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );

				Calendar myCalendar = Calendar.getInstance();
				Integer currentTimeInSeconds = myCalendar.get(Calendar.SECOND);

				Draft myDraft = new Draft(myMessage);
				myDraft.setKey(MainActivity.REPOST_TWITTER);
				myDraft.setDateSaved(currentTimeInSeconds.toString());
				myDraft.setTwitterPosted(false);
				myDraft.setFacebookPosted(true);
				myDraft.setGooglePlusPosted(true);
				myDraft.setFailedToPost(true);

				MainActivity.saveDraft(MainActivity.REPOST_TWITTER, myDraft, myContext);
				mNotificationManager.cancel(MainActivity.TWITTER_NOTIFICATION);
				Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				NotificationCompat.Builder mNotificationTwitterFail =
						new NotificationCompat.Builder(myContext)
							.setSmallIcon(R.drawable.ic_stat_rapido)
							.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_twitter_large_icon))
							.setContentTitle(myContext.getString(R.string.alert_fail_twitter))
							.setContentText(myContext.getString(R.string.alert_fail_retry))
							.setAutoCancel(true)
							.setContentIntent(repostPendingIntent)
							.setSound(defaultSound)
							.setDefaults(Notification.DEFAULT_ALL);
				mNotificationManager.notify(MainActivity.TWITTER_NOTIFICATION, mNotificationTwitterFail.getNotification());
			}
		}
	}

	public static class TokenFetcher extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		OAuthGetAccessToken myOauthGetAccessToken;

		public TokenFetcher(Context theContext,
				OAuthGetAccessToken theGetAccessToken) {
			myContext = theContext;
			myOauthGetAccessToken = theGetAccessToken;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			OAuthCredentialsResponse myResponse = null;
			String token = PreferencesHelper.getTwitterToken(myContext);

			if(token == null || token.equalsIgnoreCase("")) {
				try {
					myResponse = myOauthGetAccessToken.execute();
				} catch (IOException e) {
					Log.i(TAG, "IOException");
					e.printStackTrace();
				} catch (Exception e) {
					Log.i(TAG, "Exception");
					e.printStackTrace();
				}

				Log.i(TAG, "Response: " + myResponse);

				if(myResponse != null && myResponse.token != null && myResponse.tokenSecret != null) {
					PreferencesHelper.setTwitterToken(myContext, myResponse.token);
					PreferencesHelper.setTwitterSecret(myContext, myResponse.tokenSecret);
					PreferencesHelper.setTwitterEnabled(myContext, true);
					PreferencesHelper.setTwitterConnected(myContext, true);
				}
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			String resultMessage = myContext.getString(R.string.twitter_auth_failed);

			if(PreferencesHelper.getTwitterConnected(myContext))
				resultMessage = myContext.getString(R.string.twitter_auth_connected);

			Toast.makeText(myContext, resultMessage, Toast.LENGTH_SHORT).show();
		}
	}
}