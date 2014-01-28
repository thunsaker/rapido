package com.thunsaker.rapido.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.PickedLocation;
import com.thunsaker.rapido.classes.foursquare.CompactVenue;
import com.thunsaker.rapido.services.FoursquareHelper;

import java.util.List;

public class LocationActivity extends ActionBarActivity {

	boolean useLogo = true;
    boolean showHomeUp = true;
    private LocationManager mLocationManager;
    private GoogleMap mMap;

    ListView mVenueListView;
    TextView mMessageTextView;
    public static VenueListAdapter currentVenueListAdapter;
    public static List<CompactVenue> currentVenueList;
    public static LatLng currentLocation;
    public final double markerActionBarAdjustment = 0.003;

    public final static String PICKED_LOCATION = "PICKED_LOCATION";

    @SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(showHomeUp); // Allow the back/up button
        ab.setDisplayUseLogoEnabled(useLogo); // Allow the back/up button
        ab.setDisplayShowHomeEnabled(false); // Hide the icon, show only text
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#aa000000"))); // Set the background color

		setContentView(R.layout.activity_location);

    	mVenueListView = (ListView)findViewById(R.id.listViewLocations);
    	mMessageTextView = (TextView)findViewById(R.id.textViewLoading);

    	currentVenueListAdapter = new VenueListAdapter(getApplicationContext(), R.layout.list_venue_item, currentVenueList);

    	if(currentVenueList != null && currentVenueList.size() > 0) {
    		mVenueListView.setAdapter(currentVenueListAdapter);
    		currentVenueListAdapter.notifyDataSetChanged();
    		mVenueListView.setVisibility(View.VISIBLE);
    	} else {
//    		mMessageTextView.setText(getString(R.string.location_none));
    		mMessageTextView.setText(getString(R.string.location_load));
    		mMessageTextView.setVisibility(View.VISIBLE);
    		mVenueListView.setVisibility(View.GONE);
    	}

    	View poweredView = getLayoutInflater().inflate(R.layout.powered_by_foursquare, null);
    	mVenueListView.addFooterView(poweredView, null, false);

		// Get the current gps position
		mLocationManager =
		        (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationProvider provider =
		        mLocationManager.getProvider(LocationManager.GPS_PROVIDER);

		final boolean gpsEnabled = mLocationManager.isProviderEnabled(provider.getName());
		Location lastKnown = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

		setUpMapIfNeeded(lastKnown);

	    if (!gpsEnabled) {
	    	enableLocationSettings();
	    } else {
	    	// Update the map once
	    	mLocationManager.requestLocationUpdates(provider.getName(), 60000, 50, mLocationListener);
	    	if(currentLocation != null)
	    		RefreshVenueList();
	    }
	}

/*
		// TODO: Figure out the problem with search not working correctly.
 		final MenuItem menu_search = (MenuItem) menu
				.findItem(R.id.menu_search);
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

		menu_search.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(
					com.actionbarsherlock.view.MenuItem item) {
				return false;
			}
		});
*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
            case R.id.action_location_remove:
                setResult(Activity.RESULT_CANCELED);
                finish();
            case R.id.action_location_gps:
                Intent data = new Intent();
                PickedLocation selectedLocation =
                        new PickedLocation(currentLocation.latitude, currentLocation.longitude);
                Gson myGson = new Gson();
                data.putExtra(PICKED_LOCATION, myGson.toJson(selectedLocation));
                setResult(Activity.RESULT_OK, data);
                finish();
            case R.id.action_location_refresh:
                if(currentLocation != null)
                    RefreshVenueList();
                else
                    Toast.makeText(this, getString(R.string.location_wait), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

	private final LocationListener mLocationListener = new LocationListener() {

		@Override
	    public void onLocationChanged(Location location) {
	    	if(mMap != null) {
		    	LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
		    	Log.i("LocationActivity", "New location here: " + newLocation.latitude + ", " + newLocation.longitude);
		    	currentLocation = newLocation;
		    	mMap.clear();
		    	mMap.addMarker(new MarkerOptions()
		    		.position(currentLocation)
		    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
		    	LatLng adjustedCurrentLocation = new LatLng(currentLocation.latitude + markerActionBarAdjustment, currentLocation.longitude);
	        	mMap.moveCamera(CameraUpdateFactory.newLatLng(adjustedCurrentLocation));
		    	RefreshVenueList();
	    	} else {
	    		setUpMapIfNeeded(location);
	    	}
        }

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	};

	protected void onStop() {
	    super.onStop();
	    mLocationManager.removeUpdates(mLocationListener);
	    setResult(Activity.RESULT_CANCELED);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(Activity.RESULT_CANCELED);
	}

	private void setUpMapIfNeeded(Location myLocation) {
	    if (mMap == null) {
	        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	        if (mMap != null) {
	        	currentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		    	mMap.addMarker(new MarkerOptions()
		    		.position(currentLocation)
		    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
	        	LatLng adjustedCurrentLocation = new LatLng(currentLocation.latitude + markerActionBarAdjustment, currentLocation.longitude);
	        	mMap.moveCamera(CameraUpdateFactory.newLatLng(adjustedCurrentLocation));
	        }
	    }
	}

	public class VenueListAdapter extends ArrayAdapter<CompactVenue> {
		public List<CompactVenue> items;

		public VenueListAdapter(Context context, int textViewResourceId, List<CompactVenue> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null) {
				LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = viewInflater.inflate(R.layout.list_venue_item, null);
			}

			final CompactVenue venue = items.get(position);
			if(venue != null) {
				final String myVenueId = venue.getId() != null ? venue.getId() : "";
				final String myVenueName = venue.getName() != null ? venue.getName() : "";
				final String myVenueAddress = venue.getLocation().getAddress() != null
						? venue.getLocation().getAddress() : "";
//				final double myVenueLat = venue.getLocation().getLatitude() != null ?
//						venue.getLocation().getLatitude() : 0.00;
//				final double myVenueLng = venue.getLocation().getLongitude() != null ?
//						venue.getLocation().getLongitude() : 0.00;

				final TextView nameTextView = (TextView)v.findViewById(R.id.textViewVenueName);
				if(nameTextView != null)
					nameTextView.setText(myVenueName);

				final TextView addressTextView = (TextView)v.findViewById(R.id.textViewVenueAddress);
				if(addressTextView != null)
					addressTextView.setText(myVenueAddress);

				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PickedLocation selectedLocation =
								new PickedLocation(currentLocation.latitude, currentLocation.longitude, myVenueName, myVenueId, myVenueAddress);

//						PickedLocation selectedLocation =
//								new PickedLocation(myVenueLat != 0.00 ? myVenueLat : currentLocation.latitude,
//										myVenueLng != 0.00 ? myVenueLng : currentLocation.longitude,
//												myVenueName, myVenueId, myVenueAddress);
						Gson myGson = new Gson();
						Intent data = new Intent();
						data.putExtra(PICKED_LOCATION, myGson.toJson(selectedLocation));
						setResult(Activity.RESULT_OK, data);
						finish();
					}
				});
			}
			return v;
		}
	}

	// Refresh the Venue list
	public void RefreshVenueList() {
    	ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			ListView myListView = (ListView) findViewById(R.id.listViewLocations);
			new FoursquareHelper.GetClosestVenues(getApplicationContext(), currentLocation, myListView, this).execute();
		}
	}

	// Show the GPS alert
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
}