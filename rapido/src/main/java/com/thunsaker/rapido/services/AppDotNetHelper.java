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

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.Draft;
import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.util.PreferencesHelper;
import com.thunsaker.rapido.util.Util;

import java.util.Calendar;

public class AppDotNetHelper {
	public static String APP_DOT_NET_BASE_URL = "https://alpha-api.app.net/stream/0/";
	public static String APP_DOT_NET_POST_SUFFIX = "posts";
	public static String APP_DOT_NET_INCLUDE_POST_ANNOTATIONS = "include_post_annotations=1";

	public static class SendPost extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		LatLng myCurrentLatLng;
		String myVenueId;
		String myMessage;

		public SendPost(Context theContext, LatLng theCurrentLatLng, String theFoursquareVenueId, String theMsg) {
			myContext = theContext;
			myCurrentLatLng = theCurrentLatLng;
			myVenueId = theFoursquareVenueId;
			myMessage = theMsg;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean myResult = false;
			myResult = sendPost(myContext, myCurrentLatLng, myVenueId, myMessage);
			return myResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			NotificationManager mNotificationManager =
					(NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

			if(result) {
				NotificationCompat.Builder mNotificationAppDotNetPosted =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_appnet_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posted_app_dot_net))
						.setContentIntent(MainActivity.genericPendingIntent);
				mNotificationManager.notify(MainActivity.APP_DOT_NET_NOTIFICATION, mNotificationAppDotNetPosted.getNotification());
				mNotificationManager.cancel(MainActivity.APP_DOT_NET_NOTIFICATION);
			} else {
				Intent appDotNetRepost = new Intent(myContext, MainActivity.class);
				appDotNetRepost.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", MainActivity.REPOST_APP_DOT_NET, myMessage));
				TaskStackBuilder stackBuilder = TaskStackBuilder.from(myContext);
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(appDotNetRepost);
				PendingIntent repostPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );

				Calendar myCalendar = Calendar.getInstance();
				Integer currentTimeInSeconds = myCalendar.get(Calendar.SECOND);

				Draft myDraft = new Draft(myMessage);
				myDraft.setKey(MainActivity.REPOST_APP_DOT_NET);
				myDraft.setDateSaved(currentTimeInSeconds.toString());
				myDraft.setTwitterPosted(true);
				myDraft.setFacebookPosted(true);
				myDraft.setFoursquarePosted(true);
				myDraft.setGooglePlusPosted(true);
				myDraft.setAppDotNetPosted(false);
				myDraft.setFailedToPost(true);

				MainActivity.saveDraft(MainActivity.REPOST_APP_DOT_NET, myDraft, myContext);
				mNotificationManager.cancel(MainActivity.APP_DOT_NET_NOTIFICATION);
				Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				NotificationCompat.Builder mNotificationAppDotNetFail =
						new NotificationCompat.Builder(myContext)
							.setSmallIcon(R.drawable.ic_stat_rapido)
							.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_appnet_large_icon))
							.setContentTitle(myContext.getString(R.string.alert_fail_app_dot_net))
							.setContentText(myContext.getString(R.string.alert_fail_retry))
							.setAutoCancel(true)
							.setContentIntent(repostPendingIntent)
							.setSound(defaultSound)
							.setDefaults(Notification.DEFAULT_ALL);
				mNotificationManager.notify(MainActivity.APP_DOT_NET_NOTIFICATION, mNotificationAppDotNetFail.getNotification());
			}
		}

	}

	public static Boolean sendPost(Context myContext, LatLng myCurrentLatLng, String myVenueId, String myText) {
		try {
			if(PreferencesHelper.getAppDotNetConnected(myContext)) {
				String token = PreferencesHelper.getAppDotNetToken(myContext);

				String sendPostUrl =
						String.format("%s%s?access_token=%s&%s",
								APP_DOT_NET_BASE_URL,
								APP_DOT_NET_POST_SUFFIX,
								token,
								APP_DOT_NET_INCLUDE_POST_ANNOTATIONS);
//				Log.i("AppDotNetHelper", "Post Url: " + sendPostUrl);

				String sendPostJsonDataExtras = "";

				if(myCurrentLatLng != null) {
					sendPostJsonDataExtras =
						String.format(",\"annotations\":[{\"type\":\"net.app.core.geolocation\",\"value\":{\"latitude\":%s,\"longitude\":%s}}]",
						myCurrentLatLng.latitude, myCurrentLatLng.longitude);
				}

				String sendPostJsonData = String.format("{\"text\":\"%s\"%s}", myText, sendPostJsonDataExtras);
				Log.i("AppDotNetHelper", "Post Data: " + sendPostJsonData.toString());

				String jsonPostResponse =
						Util.getHttpResponseWithData(sendPostUrl, true, "", "", sendPostJsonData);

				Log.i("AppDotNetHelper", "Post Response: " + jsonPostResponse);

				try {
					if(jsonPostResponse != null) {
						JsonParser jParser = new JsonParser();
						JsonObject jObject = (JsonObject) jParser.parse(jsonPostResponse);

						if(jObject != null) {
							JsonObject jObjectMeta = jObject.getAsJsonObject("meta");
							if(jObjectMeta != null) {
								if(Integer.parseInt(jObjectMeta.get("code").toString()) == 200) {
									return true;
								}
							}
						}
					}
				} catch (Exception e) {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}