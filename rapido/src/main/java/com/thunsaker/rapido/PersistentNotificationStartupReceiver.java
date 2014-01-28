package com.thunsaker.rapido;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.util.PreferencesHelper;

public class PersistentNotificationStartupReceiver extends BroadcastReceiver {

	public static Intent genericIntent;
	public static PendingIntent genericPendingIntent;

	NotificationCompat.Builder mNotificationRapidoPersistent;
	NotificationManager mNotificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(PreferencesHelper.getPersistentNotificationEnabled(context)) {
			mNotificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			genericIntent = new Intent(context.getApplicationContext(), MainActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.from(context.getApplicationContext());
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(genericIntent);
			genericPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );

			SetupPersistentNotification(context);
		} else {
			Log.i("PersistentNotification", "Persistent Notification no being displayed");
		}
	}

	private void SetupPersistentNotification(Context context) {
		mNotificationRapidoPersistent =
				new NotificationCompat.Builder(context.getApplicationContext())
					.setSmallIcon(R.drawable.ic_stat_rapido)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
					.setContentTitle(context.getString(R.string.alert_persistent_notification_title))
					.setContentText(context.getString(R.string.alert_persistent_notification))
					.setContentIntent(genericPendingIntent)
					.setAutoCancel(true)
					.setOngoing(true);

		if(PreferencesHelper.getPersistentNotificationEnabled(context.getApplicationContext())){
			mNotificationManager.notify(MainActivity.RAPIDO_NOTIFICATION_PERSISTENT, mNotificationRapidoPersistent.getNotification());
		} else {
			mNotificationManager.cancel(MainActivity.RAPIDO_NOTIFICATION_PERSISTENT);
		}
	}

}
