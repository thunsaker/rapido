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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.Draft;
import com.thunsaker.rapido.classes.foursquare.CompactVenue;
import com.thunsaker.rapido.ui.LocationActivity;
import com.thunsaker.rapido.ui.LocationSearchResultsActivity;
import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.util.PreferencesHelper;
import com.thunsaker.rapido.util.Util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FoursquareHelper {
	public static String FOURSQUARE_BASE_URL = "https://api.foursquare.com/v2/";
	public static String FOURSQUARE_VENUE_SEARCH_SUFFIX = "venues/search";
	public static String FOURSQUARE_CHECKIN_SUFFIX = "checkins/add";

    public static class GetClosestVenues extends AsyncTask<Void, Integer, List<CompactVenue>> {
		Context myContext;
		LatLng myLatLng;
		String mySearchQuery;
		ListView myListView;
		String myAccessToken;
		String myClientId;
		String myClientSecret;
		LocationActivity myLocationCaller;
		LocationSearchResultsActivity myLocationSearchCaller;

		public GetClosestVenues(Context theContext, LatLng theLatLng, ListView theListView, LocationActivity theCaller) {
			myContext = theContext;
			myLatLng = theLatLng;
			mySearchQuery = "";
			myListView = theListView;
			myLocationCaller = theCaller;
		}

		public GetClosestVenues(Context theContext, LatLng theLatLng, ListView theListView, LocationSearchResultsActivity theCaller, String theSearchQuery) {
			myContext = theContext;
			mySearchQuery = theSearchQuery;
			myListView = theListView;
			myLocationSearchCaller = theCaller;
		}

		@Override
		protected List<CompactVenue> doInBackground(Void... params) {
			try {
				myAccessToken = PreferencesHelper.getFoursquareToken(myContext) != "" ? PreferencesHelper.getFoursquareToken(myContext) : "";
				myClientId = AuthHelper.FOURSQUARE_CLIENT_ID;
				myClientSecret = AuthHelper.FOURSQUARE_CLIENT_SECRET;

				List<CompactVenue> nearbyVenues = FoursquareHelper.GetClosestVenuesWithLatLng(myLatLng, mySearchQuery, myListView, myAccessToken, myClientId, myClientSecret);
				return nearbyVenues != null ? nearbyVenues : null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CompactVenue> result) {
			super.onPostExecute(result);

			List<CompactVenue> updatedList = new ArrayList<CompactVenue>();

			if(result != null) {
				if(mySearchQuery != "") {
					// Add the elements to the search result window...
					if(LocationSearchResultsActivity.currentVenueResults == null)
						LocationSearchResultsActivity.currentVenueResults = new ArrayList<CompactVenue>();

					LocationSearchResultsActivity.currentVenueResults = result;

					if(LocationActivity.currentVenueListAdapter.items == null)
						LocationActivity.currentVenueListAdapter.items = new ArrayList<CompactVenue>();

					LocationActivity.currentVenueListAdapter.items.addAll(result);
					LocationActivity.currentVenueListAdapter.notifyDataSetChanged();

					updatedList = LocationSearchResultsActivity.currentVenueResults;
					myListView.setAdapter(myLocationCaller.new VenueListAdapter(myContext, R.layout.list_venue_item, updatedList));
					myListView.setVisibility(View.VISIBLE);

				} else {
					if(LocationActivity.currentVenueList == null)
						LocationActivity.currentVenueList = new ArrayList<CompactVenue>();

					LocationActivity.currentVenueList = result;

					if(LocationActivity.currentVenueListAdapter.items == null)
						LocationActivity.currentVenueListAdapter.items = new ArrayList<CompactVenue>();

					LocationActivity.currentVenueListAdapter.items.addAll(result);
					LocationActivity.currentVenueListAdapter.notifyDataSetChanged();

					updatedList = LocationActivity.currentVenueList;
					myListView.setAdapter(myLocationCaller.new VenueListAdapter(myContext, R.layout.list_venue_item, updatedList));
					myListView.setVisibility(View.VISIBLE);

					TextView loadingText = (TextView)myLocationCaller.findViewById(R.id.textViewLoading);
					loadingText.setVisibility(View.GONE);
				}
			}
		}
	}

	public static List<CompactVenue> GetClosestVenuesWithLatLng(LatLng currentLatLng, String searchQuery, ListView listView, String accessToken, String clientId, String clientSecret) {
		List<CompactVenue> myVenues = new ArrayList<CompactVenue>();

		try {
			String venueRequestUrl;
			if(accessToken != null && accessToken.length() > 0) {
				venueRequestUrl = String.format("%s%s?ll=%s,%s&oauth_token=%s&v=%s",
					FOURSQUARE_BASE_URL,
					FOURSQUARE_VENUE_SEARCH_SUFFIX,
					currentLatLng.latitude,
					currentLatLng.longitude,
					accessToken,
					"20130211");
			} else {
				venueRequestUrl = String.format("%s%s?ll=%s,%s&client_id=%s&client_secret=%s&v=%s",
					FOURSQUARE_BASE_URL,
					FOURSQUARE_VENUE_SEARCH_SUFFIX,
					currentLatLng.latitude,
					currentLatLng.longitude,
					clientId,
					clientSecret,
					"20130211");
			}

			String jsonVenueRequestResponse = Util.getHttpResponse(
					venueRequestUrl,
					"", "");

			try {
				if(jsonVenueRequestResponse != null) {
					JsonParser jParser = new JsonParser();
					JsonObject jObject = (JsonObject) jParser.parse(jsonVenueRequestResponse);

					if(jObject != null) {
						JsonObject jObjectMeta = jObject.getAsJsonObject("meta");
						if(jObjectMeta != null) {
							if(Integer.parseInt(jObjectMeta.get("code").toString()) == 200) {
								JsonObject jObjectResponses = jObject.getAsJsonObject("response");
								if(jObjectResponses != null) {
									JsonArray jArrayVenues = jObjectResponses.getAsJsonArray("venues");
									if(jArrayVenues != null) {
										for (JsonElement jsonElement : jArrayVenues) {
											CompactVenue myParsedVenue = new CompactVenue();
											myParsedVenue = CompactVenue.GetCompactVenueFromJson(jsonElement.getAsJsonObject());
											myVenues.add(myParsedVenue);
										}
										return myVenues;
									} else {
										Log.e("FoursquareHelper", "Failed to parse the venues json");
									}
								} else {
									Log.e("FoursquareHelper", "Failed to parse the response json");
								}
							} else {
								Log.e("FoursquareHelper", "Failed to return a 200, meaning there was an error with the call");
							}
						} else {
							Log.e("FoursquareHelper", "Failed to parse the meta section");
						}
					} else {
						Log.e("FoursquareHelper", "Failed to parse main response");
					}
				} else {
					Log.e("FoursquareHelper", "Problem fetching the data");
				}

				Log.e("FoursquareHelper", "Failed for some other reason...");
				return null;
			} catch (Exception e) {
				Log.e("FoursquareHelper", "GetClosestVenuesWithLatLng: " + e.getMessage());
				return null;
			}

		} catch (Exception e) {
			Log.e("FoursquareHelper", "GetClosestVenuesWithLatLng: " + e.getMessage());
			return null;
		}
	}

	public static class PostUserCheckin extends AsyncTask<Void, Integer, Boolean> {
		Context myContext;
		LatLng myCurrentLatLng;
		String myVenueId;
		String myMessage;

		public PostUserCheckin(Context theContext, LatLng theCurrentLatLng, String theFoursquareVenueId, String theMsg) {
			myContext = theContext;
			myCurrentLatLng = theCurrentLatLng;
			myVenueId = theFoursquareVenueId;
			myMessage = theMsg;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean myResult = false;
			myResult = postUserCheckin(myContext, myCurrentLatLng, myVenueId, myMessage);
			return myResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			NotificationManager mNotificationManager =
					(NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

			if(result) {
				NotificationCompat.Builder mNotificationFoursquarePosted =
					new NotificationCompat.Builder(myContext)
						.setSmallIcon(R.drawable.ic_stat_rapido)
						.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_foursquare_large_icon))
						.setContentTitle(myContext.getString(R.string.alert_title))
						.setContentText(myContext.getString(R.string.alert_posted_foursquare))
						.setContentIntent(MainActivity.genericPendingIntent);
				mNotificationManager.notify(MainActivity.FOURSQUARE_NOTIFICATION, mNotificationFoursquarePosted.getNotification());
				mNotificationManager.cancel(MainActivity.FOURSQUARE_NOTIFICATION);
			} else {
				Intent foursquareRepost = new Intent(myContext, MainActivity.class);
				foursquareRepost.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", MainActivity.REPOST_FOURSQUARE, myMessage));
				TaskStackBuilder stackBuilder = TaskStackBuilder.from(myContext);
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(foursquareRepost);
				PendingIntent repostPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );

				Calendar myCalendar = Calendar.getInstance();
				Integer currentTimeInSeconds = myCalendar.get(Calendar.SECOND);

				Draft myDraft = new Draft(myMessage);
				myDraft.setKey(MainActivity.REPOST_FOURSQUARE);
				myDraft.setDateSaved(currentTimeInSeconds.toString());
				myDraft.setTwitterPosted(true);
				myDraft.setFacebookPosted(true);
				myDraft.setFoursquarePosted(false);
				myDraft.setGooglePlusPosted(true);
				myDraft.setFailedToPost(true);

				MainActivity.saveDraft(MainActivity.REPOST_FOURSQUARE, myDraft, myContext);
				mNotificationManager.cancel(MainActivity.FOURSQUARE_NOTIFICATION);
				Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				NotificationCompat.Builder mNotificationFoursquareFail =
						new NotificationCompat.Builder(myContext)
							.setSmallIcon(R.drawable.ic_stat_rapido)
							.setLargeIcon(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.ic_stat_foursquare_large_icon))
							.setContentTitle(myContext.getString(R.string.alert_fail_foursquare))
							.setContentText(myContext.getString(R.string.alert_fail_retry))
							.setAutoCancel(true)
							.setContentIntent(repostPendingIntent)
							.setSound(defaultSound)
							.setDefaults(Notification.DEFAULT_ALL);
				mNotificationManager.notify(MainActivity.FOURSQUARE_NOTIFICATION, mNotificationFoursquareFail.getNotification());
			}
		}

	}

	public static Boolean postUserCheckin(Context myContext, LatLng myCurrentLatLng, String myVenueId, String myShout) {
		try {
			if(PreferencesHelper.getFoursquareConnected(myContext)) {
				String token = PreferencesHelper.getFoursquareToken(myContext);
				String myUrlEncodedShout = URLEncoder.encode(myShout.trim(), Util.ENCODER_CHARSET);

				String checkinRequestUrl =
						String.format("%s%s?ll=%s,%s&oauth_token=%s&venueId=%s&shout=%s&v=%s",
								FOURSQUARE_BASE_URL,
								FOURSQUARE_CHECKIN_SUFFIX,
								myCurrentLatLng.latitude,
								myCurrentLatLng.longitude,
								token,
								myVenueId,
								myUrlEncodedShout,
								"20130211");

				String jsonCheckinResponse =
						Util.getHttpResponse(checkinRequestUrl, true, "", "");

				try {
					if(jsonCheckinResponse != null) {
						JsonParser jParser = new JsonParser();
						JsonObject jObject = (JsonObject) jParser.parse(jsonCheckinResponse);

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

    public static String makeFoursquareUrl(String myVenueId) {
        if(myVenueId.length() > 0)
            return String.format("https://foursquare.com/v/%s", myVenueId);
        return "";
    }
}