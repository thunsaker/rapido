package com.thunsaker.rapido.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.BuildConfig;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.adapters.VenueListAdapter;
import com.thunsaker.rapido.app.BaseRapidoActivity;
import com.thunsaker.rapido.data.PickedLocation;
import com.thunsaker.rapido.data.api.VenueSearchResponse;
import com.thunsaker.rapido.data.api.model.CompactVenue;
import com.thunsaker.rapido.data.api.model.FoursquareCompactVenueResponse;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.services.foursquare.FoursquarePrefs;
import com.thunsaker.rapido.services.foursquare.FoursquareService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LocationPicker extends BaseRapidoActivity
        implements SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AbsListView.OnItemClickListener {

//    @Inject
//    EventBus mBus;

    @Inject
    FoursquareService mFoursquareService;

    @Inject
    RapidoPrefsManager mPreferences;

    @Bind(R.id.toolbar_location) Toolbar mToolbar;
    @Bind(R.id.location_list_container) SwipeRefreshLayout mSwipeViewVenueList;
    @Bind(R.id.location_list) ListView mListView;
    @Bind(R.id.map_wrapper) FrameLayout mMapWrapper;

    public Subscription foursquareServiceSubscription;

    public static GoogleApiClient mGoogleClient;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private VenueListAdapter currentVenueListAdapter;
    private List<CompactVenue> currentVenueList;

    protected static final float MAP_DEFAULT_ZOOM_LEVEL = 16;

    public final static String PICKED_LOCATION = "PICKED_LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setTitle(null);

        if(mSwipeViewVenueList != null) {
            mSwipeViewVenueList.setOnRefreshListener(this);
            mSwipeViewVenueList.setColorSchemeColors(R.color.accent);
            mSwipeViewVenueList.setRefreshing(true);
        }

        mListView.setOnItemClickListener(this);

        buildGoogleApiClient();
        if(!mGoogleClient.isConnecting())
            mGoogleClient.connect();

        setUpMapIfNeeded();

        // TODO: Show a loading screen while we wait for the GPS.
//        fetchNearbyVenues();
    }

    private void fetchNearbyVenues() {
        if(MainActivity.currentLocation != null) {
            mSwipeViewVenueList.setRefreshing(true);

            String latLngString = String.format("%s, %s",
                    MainActivity.currentLocation.getLatitude(),
                    MainActivity.currentLocation.getLongitude());

            String accessToken = mPreferences.foursquareToken().getOr("");
            if(accessToken != null && accessToken.length() > 0) {
                foursquareServiceSubscription =
                        mFoursquareService
                                .searchVenuesNearby(
                                        accessToken,
                                        latLngString,
                                        "",
                                        FoursquarePrefs.DEFAULT_SEARCH_LIMIT,
                                        FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN,
                                        FoursquarePrefs.DEFAULT_SEARCH_RADIUS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(MapFoursquareVenueResponse())
                                .subscribe(AddVenuesToList());
            } else {
                foursquareServiceSubscription =
                        mFoursquareService
                                .searchVenuesNearbyUserless(
                                        AuthHelper.FOURSQUARE_CLIENT_ID,
                                        AuthHelper.FOURSQUARE_CLIENT_SECRET,
                                        latLngString,
                                        "",
                                        FoursquarePrefs.DEFAULT_SEARCH_LIMIT,
                                        FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN,
                                        FoursquarePrefs.DEFAULT_SEARCH_RADIUS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(MapFoursquareVenueResponse())
                                .subscribe(AddVenuesToList());
            }
        } else
            new SnackBar.Builder(this)
                    .withMessage(getString(R.string.error_location_unknown))
                    .show();

    }

    private Action1<List<CompactVenue>> AddVenuesToList() {
        return new Action1<List<CompactVenue>>() {
            @Override
            public void call(List<CompactVenue> compactVenues) {
                assert compactVenues != null;
                AddItemsToList(compactVenues, false);
            }
        };
    }

    private Func1<VenueSearchResponse, List<CompactVenue>> MapFoursquareVenueResponse() {
        return new Func1<VenueSearchResponse, List<CompactVenue>>() {
            @Override
            public List<CompactVenue> call(VenueSearchResponse response) {
                assert response != null;
                assert response.response != null;
                assert response.response.venues != null;

                List<CompactVenue> venues = new ArrayList<CompactVenue>();

                for (FoursquareCompactVenueResponse venueResponse
                        : response.response.venues) {
                    venues.add(CompactVenue.
                            GetCompactVenueFromFoursquareCompactVenueResponse(
                                    venueResponse));
                }
                return venues;
            }
        };
    }

    private void AddItemsToList(List<CompactVenue> compactVenues, Boolean appendList) {
        if(currentVenueList == null)
            currentVenueList = new ArrayList<>();

        if(appendList)
            currentVenueList.addAll(compactVenues);
        else
            currentVenueList = compactVenues;

        if(currentVenueListAdapter == null)
            currentVenueListAdapter =
                    new VenueListAdapter(this, currentVenueList);

        currentVenueListAdapter.notifyDataSetChanged();
        mListView.setAdapter(currentVenueListAdapter);
        mSwipeViewVenueList.setRefreshing(false);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_close) {
            setResult(Activity.RESULT_CANCELED);
            ShutItDown();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShutItDown() {
        if(foursquareServiceSubscription != null)
            foursquareServiceSubscription.unsubscribe();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Skywalker Ranch - 38.061811,-122.643428
        LatLng latLng = new LatLng(38.061811, -122.643428);

        if(MainActivity.currentLocation != null) {
            latLng = new LatLng(
                    MainActivity.currentLocation.getLatitude(),
                    MainActivity.currentLocation.getLongitude());
        }

        // INFO: https://developers.google.com/maps/documentation/android/marker#add_a_marker
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.location_you_are_here))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_DEFAULT_ZOOM_LEVEL));
    }

    @Override
    public void onConnected(Bundle bundle) {
        MainActivity.currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
        if(BuildConfig.DEBUG)
            if(MainActivity.currentLocation != null)
                new SnackBar.Builder(this).withMessage("Debug: " + MainActivity.currentLocation.toString()).show();
        fetchNearbyVenues();
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        SnackBar.Builder builder =
                new SnackBar.Builder(this)
                        .withMessage(getString(R.string.error_location_unknown))
                        .withActionMessage(getString(R.string.location_search))
                        .withOnClickListener(new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable token) {
                                // TODO: Pop Search Bar
                            }
                        });
        builder.show();
    }

    @Override
    public void onRefresh() {
        fetchNearbyVenues();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CompactVenue clickedVenue = (CompactVenue) mListView.getItemAtPosition(position);
        if(clickedVenue != null && clickedVenue.id != null && clickedVenue.name != null) {
            PickedLocation pickedLocation =
                    new PickedLocation(
                            clickedVenue.location.latitude,
                            clickedVenue.location.longitude,
                            clickedVenue.name,
                            clickedVenue.id,
                            clickedVenue.location.address != null
                                    ? clickedVenue.location.address : "");
            Gson myGson = new Gson();
            Intent data = new Intent();
            data.putExtra(LocationPicker.PICKED_LOCATION, myGson.toJson(pickedLocation));
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        ShutItDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleClient = null;
    }
}