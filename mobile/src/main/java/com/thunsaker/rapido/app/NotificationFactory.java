package com.thunsaker.rapido.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.services.UpdateService;

import javax.inject.Inject;

public class NotificationFactory {

    @Inject
    @ForApplication
    Context mContext;

    public UpdateService mService = UpdateService.NONE;
    private int mSmallIcon;
    private int mLargeIcon;
    private int mColor;
    private String mTitle;
    private String mMessage;
    private boolean mBigTextStyle;
    private PendingIntent mContentIntent;
    private boolean mLowPriority;

    public NotificationFactory(Context mContext) {
        RapidoApp app = (RapidoApp)mContext;
        app.inject(this);
    }

    public Notification buildNotification() {
        return makeNotification().build();
    }

    public NotificationCompat.Builder makeNotification() {
        if(mLargeIcon == 0) {
            switch (mService) {
                case SERVICE_TWITTER:
                    mLargeIcon = R.drawable.ic_stat_twitter_circle;
                    break;
                case SERVICE_FACEBOOK:
                    mLargeIcon = R.drawable.ic_stat_facebook_circle;
                    break;
                case SERVICE_FOURSQUARE:
                    mLargeIcon = R.drawable.ic_stat_foursquare_circle;
                    break;
                case SERVICE_GOOGLE_PLUS:
                    mLargeIcon = R.drawable.ic_stat_google_plus_circle;
                    break;
            }
        }

        if(mTitle.length() == 0)
            mTitle = mContext.getString(R.string.alert_title_posting);

        if(mSmallIcon == 0)
            mSmallIcon = R.drawable.ic_stat_rapido_bolt;

        if(mColor == 0)
            mColor = R.color.primary;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setContentTitle(mTitle)
                        .setSmallIcon(mSmallIcon)
                        .setColor(mContext.getResources().getColor(mColor));

        if(mLargeIcon > 0)
            builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), mLargeIcon));

        if(mMessage.length() > 0)
            builder.setContentText(mMessage);

        if(mContentIntent != null)
            builder.setContentIntent(mContentIntent);

        if(mLowPriority) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                builder.setPriority(Notification.PRIORITY_LOW);

            builder.setSound(null);
            builder.setVibrate(null);
        }

        return builder;
    }

    public NotificationFactory withService(UpdateService service) {
        mService = service;
        reset();
        return this;
    }

    private void reset() {
        mSmallIcon = 0;
        mLargeIcon = 0;
        mMessage = "";
        mTitle = "";
        mLowPriority = false;
        mContentIntent = null;
    }

    public NotificationFactory withTitle(String title) {
        mTitle = title;
        return this;
    }

    public NotificationFactory withMessage(String message) {
        mMessage = message;
        return this;
    }

    public NotificationFactory withSmallIcon(int smallIcon) {
        mSmallIcon = smallIcon;
        return this;
    }

    public NotificationFactory withLargeIcon(int largeIcon) {
        mLargeIcon = largeIcon;
        return this;
    }

    public NotificationFactory withColor(int color) {
        mColor = color;
        return this;
    }

    public NotificationFactory withContentIntent(PendingIntent contentIntent) {
        mContentIntent = contentIntent;
        return this;
    }

    public NotificationFactory withLowPriority() {
        mLowPriority = true;
        return this;
    }
}