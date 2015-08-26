package com.thunsaker.rapido.services;

import android.content.Context;
import android.os.AsyncTask;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.app.RapidoApp;
import com.thunsaker.rapido.data.events.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class TwitterTasks {
    @Inject
    EventBus mBus;

    @Inject @ForApplication
    Context mContext;

    @Inject
    Twitter mTwitter;

    @Inject
    RapidoPrefsManager mPreferences;

    public TwitterTasks(RapidoApp app) {
        app.inject(this);
    }

    public class PostStatusUpdate extends AsyncTask<Void, Integer, Status> {
        String text;
        String errorText;
        UpdateServiceResult errorResult;

        public PostStatusUpdate(String text) {
            this.text = text;
        }

        @Override
        protected twitter4j.Status doInBackground(Void... params) {
            try {
                String token = mPreferences.twitterAuthToken().getOr("");
                String secret = mPreferences.twitterAuthSecret().getOr("");
                if(token.length() > 0 && secret.length() > 0) {
                    mTwitter.setOAuthAccessToken(new AccessToken(token, secret));
                    StatusUpdate statusUpdate = new StatusUpdate(text);
                    // TODO: Add Location Info
                    // statusUpdate.setPlaceId();

                    return mTwitter.updateStatus(statusUpdate);
                } else {
                    errorText = String.format(mContext.getResources().getString(R.string.account_reauth), mContext.getResources().getString(R.string.accounts_twitter));
                    return null;
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                errorText = e.getErrorMessage();
                if(e.getStatusCode() == 403)
                    errorResult = UpdateServiceResult.RESULT_DUPLICATE;
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                errorText = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {
            super.onPostExecute(status);

            List<UpdateService> serviceList = new ArrayList<UpdateService>();
            serviceList.add(UpdateService.SERVICE_TWITTER);

            if(status != null) {
                boolean success = status.getId() > 0;
                mBus.post(new UpdateEvent(success, "", UpdateServiceResult.RESULT_SUCCESS, text, serviceList));
            } else {
                String errorString = mContext.getString(R.string.error_posting_update);
                if(errorText != null && errorText.length() > 0) {
                    errorString = String.format(mContext.getString(R.string.error_template), errorText);
                }
                mBus.post(new UpdateEvent(false, errorString,
                        errorResult != null ? errorResult : UpdateServiceResult.RESULT_FAILURE,
                        text, serviceList));
            }
        }
    }
}