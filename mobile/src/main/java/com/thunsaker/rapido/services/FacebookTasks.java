package com.thunsaker.rapido.services;

import android.content.Context;
import android.os.AsyncTask;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.app.RapidoApp;
import com.thunsaker.rapido.data.events.UpdateEvent;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

@Deprecated
public class FacebookTasks {
    @Inject
    EventBus mBus;

    @Inject @ForApplication
    Context mContext;

    public String LOG_TAG = "FacebookTasks";

    public FacebookTasks(RapidoApp app) {
        app.inject(this);
    }

    public class PostStatusUpdate extends AsyncTask<Void, Integer, Boolean> {
        String text;
        String errorText;
        UpdateServiceResult errorResult;

        public PostStatusUpdate(String text) { this.text = text ;}

        @Override
        protected Boolean doInBackground(Void... params) {
            final List<UpdateService> serviceList = Arrays.asList(UpdateService.SERVICE_FACEBOOK);
//            if(MainFragment.mFacebookSession != null) {
//                Bundle requestParams = new Bundle();
//                requestParams.putString("message", text);
//                requestParams.putString("description", "Posting from Rápido for Android");
//
//                // TODO: Add location
//
//                // TODO: Add link callout
//                Request request = new Request(MainFragment.mFacebookSession, "me/feed", requestParams, HttpMethod.POST,
//                    new Request.Callback() {
//                        @Override
//                        public void onCompleted(Response response) {
//                            GraphObject graphObject = response.getGraphObject();
//                            FacebookRequestError error = response.getError();
//                            try {
//                                if(graphObject != null) {
//                                    JSONObject postResponse = graphObject.getInnerJSONObject() != null ? graphObject.getInnerJSONObject() : null;
//                                    assert postResponse != null;
//                                    String postId = postResponse.getString("id");
//                                    if (postId != null)
//                                        mBus.post(
//                                                new UpdateEvent(
//                                                        true, "", UpdateServiceResult.RESULT_SUCCESS,
//                                                        text, serviceList));
//                                }
//                            } catch (JSONException e) {
//                                Log.e(LOG_TAG, "JSON error " + e.getMessage());
//                                errorText = "Problem parsing response";
//                            } catch (Exception e) {
//                                Log.e(LOG_TAG, "Error " + e.getMessage());
//                                errorText = "Error posting update";
//                            }
//
//                            if(error != null && error.getErrorMessage() != null) {
//                                errorText = error.getErrorMessage();
//
//                                if(error.getErrorCode() == 506)
//                                    errorResult = UpdateServiceResult.RESULT_DUPLICATE;
//                            }
//                        }
//                    });
//                request.executeAndWait();
//                return true;
//            } else {
                return false;
//            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            List<UpdateService> serviceList = Arrays.asList(UpdateService.SERVICE_FACEBOOK);

            String errorString = mContext.getString(R.string.error_posting_update);

            if(errorText != null && errorText.length() > 0) {
                errorString =
                        String.format(mContext.getString(R.string.error_template), errorText);

                mBus.post(
                        new UpdateEvent(
                                false, errorString,
                                errorResult != null ? errorResult : UpdateServiceResult.RESULT_FAILURE,
                                text, serviceList));
            } else {
                mBus.post(
                        new UpdateEvent(
                                true, "", UpdateServiceResult.RESULT_SUCCESS,
                                text, serviceList));
            }
        }
    }
}
