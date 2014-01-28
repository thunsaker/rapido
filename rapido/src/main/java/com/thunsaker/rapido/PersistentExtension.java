package com.thunsaker.rapido;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.thunsaker.rapido.ui.MainActivity;

public class PersistentExtension extends DashClockExtension {

	@Override
	protected void onUpdateData(int reason) {
		publishUpdate(new ExtensionData()
		.visible(true)
		.icon(R.drawable.white_rabbit_outline)
		.status(getString(R.string.alert_persistent_notification_title))
		.expandedTitle(getString(R.string.alert_persistent_notification))
		.expandedBody(getString(R.string.alert_persistent_notification_title))
		.clickIntent(new Intent(getApplicationContext(), MainActivity.class)));
	}
}
