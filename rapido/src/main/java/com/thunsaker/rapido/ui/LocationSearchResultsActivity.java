package com.thunsaker.rapido.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.PickedLocation;
import com.thunsaker.rapido.classes.foursquare.CompactVenue;
import com.thunsaker.rapido.services.FoursquareHelper;

import java.util.List;


public class LocationSearchResultsActivity extends ActionBarActivity {
	public static List<CompactVenue> currentVenueResults = null;
	public static VenueResultListAdapter currentVenueResultAdapter;
	private ListView mVenueResultView;

	private boolean useLogo = true;
    private boolean showHomeUp = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(showHomeUp);
        ab.setDisplayUseLogoEnabled(useLogo);

		setContentView(R.layout.activity_location_search);
		Log.i("LocationSearchResults", "Hey!");
		currentVenueResultAdapter = new VenueResultListAdapter(getApplicationContext(), R.layout.list_venue_item, currentVenueResults);

		if(currentVenueResults != null && currentVenueResults.size() > 0) {
			mVenueResultView.setAdapter(currentVenueResultAdapter);
			currentVenueResultAdapter.notifyDataSetChanged();
    		mVenueResultView.setVisibility(View.VISIBLE);
		}

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i("LocationSearchResults", "Ho!");
		if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i("LocationSearchResults", "Hey 2!");
			GetVenuesFromSearchQuery(query);
		}
	}

	private void GetVenuesFromSearchQuery(String query) {
		// Fire off a new Foursquare action result...
		Log.i("LocationSearchResults", "Ho 2!");
    	ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			ListView myListView = (ListView) findViewById(R.id.listViewLocations);
			if(LocationActivity.currentLocation != null) {
				new FoursquareHelper.GetClosestVenues(getApplicationContext(), LocationActivity.currentLocation, myListView, this, query).execute();
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.location_wait), Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	public class VenueResultListAdapter extends ArrayAdapter<CompactVenue> {
		List<CompactVenue> items;

		public VenueResultListAdapter(Context context, int textViewResourceId, List<CompactVenue> items) {
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
								new PickedLocation(LocationActivity.currentLocation.latitude, LocationActivity.currentLocation.longitude, myVenueName, myVenueId, myVenueAddress);
						Gson myGson = new Gson();
						Intent data = new Intent();
						data.putExtra(LocationActivity.PICKED_LOCATION, myGson.toJson(selectedLocation));
						setResult(Activity.RESULT_OK, data);
						finish();
					}
				});
			}
			return v;
		}
	}
}
