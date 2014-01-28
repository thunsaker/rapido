package com.thunsaker.rapido.services;

import android.app.Activity;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.Draft;
import com.thunsaker.rapido.ui.MainActivity;

import java.util.Calendar;


public class GooglePlusHelper {
	// Old Way
	public static void PopUserMoment(Context myContext, String myUpdate, LatLng myLatLng, String myPrimaryLink, Activity myActivity) {
		Intent shareIntent = new PlusShare.Builder(myActivity)
			.setType("text/plain")
			.setText(myUpdate)
			.getIntent();

        // Setting the content link removes the text... hrmmm
        //.setContentUrl(myPrimaryLink.length() > 0 ? Uri.parse(myPrimaryLink) : null)
        //Log.i("GooglePlusHelper - PopUserMoment", "My Update " + myUpdate);
		myActivity.startActivityForResult(shareIntent, 0);
	}

	// New Way
	public static class PostUserCheckin extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		String myUpdate;
		LatLng myLatLng;
		String myPrimaryLink;
		PlusClient myPlusClient;

		public PostUserCheckin(Context theContext, String theUpdate, LatLng theLatLng, String thePrimaryLink, PlusClient thePlusClient) {
			myContext = theContext;
			myUpdate = theUpdate;
			myLatLng = theLatLng;
			myPrimaryLink = thePrimaryLink;
			myPlusClient = thePlusClient;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ItemScope location = new ItemScope.Builder()
					.setType("http://schemas.google.com/CheckIn")
					.setLatitude(myLatLng.latitude)
					.setLongitude(myLatLng.longitude)
					.setText(myUpdate)
					.setContentUrl(myPrimaryLink.length() > 0 ? myPrimaryLink : null)
					.build();

				Moment moment = new Moment.Builder()
					.setType("http://schemas.google.com/CheckInActivity")
					.setTarget(location)
					.build();

				myPlusClient.connect();
				if(myPlusClient.isConnected()) {
					myPlusClient.writeMoment(moment);
					return true;
				} else {
					return false;
				}
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
				NotificationCompat.Builder mNotificationGooglePlusPosted =
						new NotificationCompat.Builder(myContext)
							.setSmallIcon(R.drawable.ic_stat_rapido)
							.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_google_plus_large_icon))
							.setContentTitle(myContext.getString(R.string.alert_title))
							.setContentText(myContext.getString(R.string.alert_posted_google_plus))
							.setContentIntent(MainActivity.genericPendingIntent);
					mNotificationManager.notify(MainActivity.GOOGLE_PLUS_NOTIFICATION, mNotificationGooglePlusPosted.getNotification());
					mNotificationManager.cancel(MainActivity.GOOGLE_PLUS_NOTIFICATION);
			} else {
				Intent googlePlusRepost = new Intent(myContext, MainActivity.class);
				googlePlusRepost.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", MainActivity.REPOST_GOOGLE_PLUS, myUpdate));
				TaskStackBuilder stackBuilder = TaskStackBuilder.from(myContext);
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(googlePlusRepost);
				PendingIntent repostPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );

				Calendar myCalendar = Calendar.getInstance();
				Integer currentTimeInSeconds = myCalendar.get(Calendar.SECOND);

				Draft myDraft = new Draft(myUpdate);
				myDraft.setKey(MainActivity.REPOST_GOOGLE_PLUS);
				myDraft.setDateSaved(currentTimeInSeconds.toString());
				myDraft.setTwitterPosted(true);
				myDraft.setFacebookPosted(true);
				myDraft.setFoursquarePosted(true);
				myDraft.setGooglePlusPosted(false);
				myDraft.setFailedToPost(true);

				MainActivity.saveDraft(MainActivity.REPOST_GOOGLE_PLUS, myDraft, myContext);
				mNotificationManager.cancel(MainActivity.GOOGLE_PLUS_NOTIFICATION);
				Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				NotificationCompat.Builder mNotificationGooglePlusFail =
						new NotificationCompat.Builder(myContext)
							.setSmallIcon(R.drawable.ic_stat_rapido)
							.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_google_plus_large_icon))
							.setContentTitle(myContext.getString(R.string.alert_fail_google_plus))
							.setContentText(myContext.getString(R.string.alert_fail_retry))
							.setAutoCancel(true)
							.setContentIntent(repostPendingIntent)
							.setSound(defaultSound)
							.setDefaults(Notification.DEFAULT_ALL);
				mNotificationManager.notify(MainActivity.GOOGLE_PLUS_NOTIFICATION, mNotificationGooglePlusFail.getNotification());
			}
		}
	}
}
