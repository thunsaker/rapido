package com.thunsaker.rapido.services.foursquare;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.app.RapidoApp;
import com.thunsaker.rapido.data.api.model.CompactVenue;
import com.thunsaker.rapido.data.events.VenueListEvent;
import com.thunsaker.rapido.ui.MainActivity;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FoursquareTasks {
    @Inject
    EventBus mBus;

    @Inject @ForApplication
    Context mContext;

    @Inject
    RapidoPrefsManager mPreference;

    @Inject
    FoursquareService mFoursquareService;

    @Inject
    SwarmService mSwarmService;

    public FoursquareTasks(RapidoApp app) {
        app.inject(this);
    }

    public class GetClosestVenuesNew extends AsyncTask<Void, Integer, List<CompactVenue>> {
        Location myLatLng;
        String mySearchQuery;
        String mySearchQueryLocation;
        String myDuplicateSearchId;
        int myListType;

        public GetClosestVenuesNew(String theSearchQuery, String theSearchQueryLocation, String theDuplicateSearchId, int theListType) {
            myLatLng = MainActivity.currentLocation;
            mySearchQuery = theSearchQuery;
            mySearchQueryLocation = theSearchQueryLocation;
            myDuplicateSearchId = theDuplicateSearchId;
            myListType = theListType;
        }

        @Override
        protected List<CompactVenue> doInBackground(Void... params) {
            try {
//                List<CompactVenue> nearbyVenues = new ArrayList<CompactVenue>();
//
//                String mAccessToken = mPreference.foursquareToken().getOr("");
//
//                VenueSearchResponse response;
//                if(mySearchQueryLocation != null && mySearchQueryLocation.length() > 0) {
////                    response =
////                            mFoursquareService.searchVenues(
////                                    mAccessToken,
////                                    mySearchQuery, mySearchQueryLocation,
////                                    FoursquarePrefs.DEFAULT_SEARCH_LIMIT,
////                                    FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN,
////                                    FoursquarePrefs.DEFAULT_SEARCH_RADIUS);
//                } else {
//                    response =
//                            mFoursquareService.searchVenuesNearby(
//                                    mAccessToken, String.format("%s,%s", myLatLng.latitude, myLatLng.longitude),
//                                    mySearchQuery,
//                                    FoursquarePrefs.DEFAULT_SEARCH_LIMIT,
//                                    FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN,
//                                    FoursquarePrefs.DEFAULT_SEARCH_RADIUS);
//                }
//
//                if(response != null) {
//                    if(response.meta.code == 200 && response.response.venues != null) {
//                        for (FoursquareCompactVenueResponse compact : response.response.venues) {
//                            nearbyVenues.add(FoursquareCompactVenueResponse.ConvertFoursquareCompactVenueResponseToCompactVenue(compact));
//                        }
//                    } else if (response.meta.code == 503) {
//                        // TODO: pop error
////                        ShowServerErrorToast(response.meta);
//                    } else {
//                        Log.i("FoursquareTasks", "Failure! Code: " + response.meta.code + " Error: " + response.meta.errorType + " - " + response.meta.errorDetail);
//                        nearbyVenues = null;
//                    }
//                }
//
//                return nearbyVenues;
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CompactVenue> result) {
            super.onPostExecute(result);

            if(result != null) {
                mBus.post(new VenueListEvent(true, "", result, mySearchQuery, mySearchQueryLocation, myDuplicateSearchId, myListType));
            } else {
                mBus.post(new VenueListEvent(false, "", null, mySearchQuery, mySearchQueryLocation, myDuplicateSearchId, myListType));
            }
        }
    }
}