package com.thunsaker.rapido.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.Draft;
import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.util.PreferencesHelper;
import com.thunsaker.rapido.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class FacebookHelper {
    public static String updateStatus(Context theContext, String update, String theVenueId) throws Exception {
        Session session = Session.getActiveSession();
        Bundle parameters = new Bundle();
        final Context myContext = theContext;
        final String myUpdate = update;
        final String myVenueId = theVenueId;

        // Remove Hashtags Preference
        if(PreferencesHelper.getFacebookDeleteHashtags(theContext)) {
            List<String> hashtagList = Util.GetHashtagsInText(update);
            if(hashtagList != null && hashtagList.size() > 0) {
                for (String hashtag : hashtagList) {
                    int tagStart = update.indexOf("#" + hashtag);
                    update = update.substring(0, tagStart - 1) +
                            update.substring(tagStart + hashtag.length() + 1, update.length());
                }
            }
        }

        parameters.putString("message", update);
        parameters.putString("description", "Posting from Rápido for Android");
//        parameters.putString("place", FoursquareHelper.makeFoursquareUrl(myVenueId));

        List<String> linksInText = Util.GetLinksInText(update);
		// TODO: Turn this into a setting
        if(linksInText != null) {
            parameters.putString("link", linksInText.get(0));
        }

        Request request = new Request(session, "me/feed", parameters, HttpMethod.POST,
                new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                JSONObject postResponse = null;
                String postId = null;

                Log.i("FacebookHelper", "Facebook raw json response: " + response);
                try {
                    postResponse = response.getGraphObject() != null ? response.getGraphObject().getInnerJSONObject() : null;
                    Log.i("FacebookHelper", "Facebook json post response : " + (postResponse != null ? postResponse : "empty"));
                    postId = postResponse != null ? postResponse.getString("id") : "0";
                } catch (JSONException e) {
                    Log.i("FacebookHelper", "JSON error " + e.getMessage());
                } catch (Exception e) {
                    Log.i("FacebookHelper", "Error " + e.getMessage());
                }

                FacebookRequestError error = response.getError();

                NotificationManager mNotificationManager =
                        (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

                if(error != null) {
                    // Display notification that will allow reposting message...
                    Intent facebookRepost = new Intent(myContext, MainActivity.class);
                    facebookRepost.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", MainActivity.REPOST_FACEBOOK, myUpdate));
                    TaskStackBuilder stackBuilder = TaskStackBuilder.from(myContext);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(facebookRepost);
                    PendingIntent repostPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    Calendar myCalendar = Calendar.getInstance();
                    Integer currentTimeInSeconds = myCalendar.get(Calendar.SECOND);

                    Draft myDraft = new Draft(myUpdate);
                    myDraft.setKey(MainActivity.REPOST_FACEBOOK);
                    myDraft.setDateSaved(currentTimeInSeconds.toString());
                    myDraft.setTwitterPosted(true);
                    myDraft.setFacebookPosted(false);
                    myDraft.setGooglePlusPosted(true);
                    myDraft.setFailedToPost(true);

                    MainActivity.saveDraft(MainActivity.REPOST_FACEBOOK, myDraft, myContext);
                    mNotificationManager.cancel(MainActivity.FACEBOOK_NOTIFICATION);

                    NotificationCompat.Builder mNotificationFacebookFail =
                            new NotificationCompat.Builder(myContext)
                                    .setSmallIcon(R.drawable.ic_stat_rapido)
                                    .setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_facebook_large_icon))
                                    .setContentTitle(myContext.getString(R.string.alert_fail_facebook))
                                    .setContentText(myContext.getString(R.string.alert_fail_retry))
                                    .setAutoCancel(true)
                                    .setContentIntent(repostPendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL);
                    mNotificationManager.notify(MainActivity.FACEBOOK_NOTIFICATION, mNotificationFacebookFail.getNotification());

                    Log.i("FacebookHelper", "Error: " + (error.getErrorMessage() != null ? error.getErrorMessage() : "No error message found."));
                } else {
                    NotificationCompat.Builder mNotificationFacebookSuccess =
                            new NotificationCompat.Builder(myContext)
                                    .setSmallIcon(R.drawable.ic_stat_rapido)
                                    .setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_facebook_large_icon))
                                    .setContentTitle(myContext.getString(R.string.alert_title))
                                    .setContentText(myContext.getString(R.string.alert_posted_facebook))
                                    .setAutoCancel(true)
                                    .setContentIntent(MainActivity.genericPendingIntent);
                    mNotificationManager.notify(MainActivity.FACEBOOK_NOTIFICATION, mNotificationFacebookSuccess.getNotification());
                    mNotificationManager.cancel(MainActivity.FACEBOOK_NOTIFICATION);
                }
            }
        });
        request.executeAndWait();
        return "true";
    }

    public static class UpdateStatus extends AsyncTask<Void, Integer, String> {
        String myMessage;
        Context myContext;
        String myVenueId;

        public UpdateStatus(Context theContext, String message, String theVenueId) {
            myMessage = message;
            myContext = theContext;
            myVenueId = theVenueId;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return updateStatus(myContext, myMessage, myVenueId);
            } catch (JSONException e) {
                e.printStackTrace();
                return "false";
            } catch (IOException e) {
                e.printStackTrace();
                return "false";
            } catch (FacebookException e) {
                e.printStackTrace();
                return "false";
            } catch (Exception e) {
                e.printStackTrace();
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            NotificationManager mNotificationManager =
                    (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (result == null || result.equals("false")) {
                // Display notification that will allow reposting message...
                Intent facebookRepost = new Intent(myContext, MainActivity.class);
                facebookRepost.putExtra(
                        Intent.EXTRA_TEXT, String.format("%s %s",
                        MainActivity.REPOST_FACEBOOK, myMessage));
                TaskStackBuilder stackBuilder = TaskStackBuilder.from(myContext);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(facebookRepost);
                PendingIntent repostPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                Calendar myCalendar = Calendar.getInstance();
                Integer currentTimeInSeconds = myCalendar.get(Calendar.SECOND);

                Draft myDraft = new Draft(myMessage);
                myDraft.setKey(MainActivity.REPOST_FACEBOOK);
                myDraft.setDateSaved(currentTimeInSeconds.toString());
                myDraft.setTwitterPosted(true);
                myDraft.setFacebookPosted(false);
                myDraft.setGooglePlusPosted(true);
                myDraft.setFailedToPost(true);

                MainActivity.saveDraft(MainActivity.REPOST_FACEBOOK, myDraft, myContext);
                mNotificationManager.cancel(MainActivity.FACEBOOK_NOTIFICATION);

                NotificationCompat.Builder mNotificationFacebookFail =
                        new NotificationCompat.Builder(myContext)
                                .setSmallIcon(R.drawable.ic_stat_rapido)
                                .setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_facebook_large_icon))
                                .setContentTitle(myContext.getString(R.string.alert_fail_facebook))
                                .setContentText(myContext.getString(R.string.alert_fail_retry))
                                .setAutoCancel(true)
                                .setContentIntent(repostPendingIntent)
                                .setDefaults(Notification.DEFAULT_ALL);
                mNotificationManager.notify(MainActivity.FACEBOOK_NOTIFICATION, mNotificationFacebookFail.getNotification());
            } else {
                Log.i("FacebookHelper", result);

                NotificationCompat.Builder mNotificationFacebookSuccess =
                        new NotificationCompat.Builder(myContext)
                                .setSmallIcon(R.drawable.ic_stat_rapido)
                                .setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_facebook_large_icon))
                                .setContentTitle(myContext.getString(R.string.alert_title))
                                .setContentText(myContext.getString(R.string.alert_posted_facebook))
                                .setAutoCancel(true)
                                .setContentIntent(MainActivity.genericPendingIntent);
                mNotificationManager.notify(MainActivity.FACEBOOK_NOTIFICATION, mNotificationFacebookSuccess.getNotification());
                mNotificationManager.cancel(MainActivity.FACEBOOK_NOTIFICATION);
            }
        }
    }
}