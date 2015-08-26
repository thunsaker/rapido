package com.thunsaker.rapido.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.data.api.model.CompactVenue;
import com.thunsaker.rapido.data.api.model.FoursquareCategory;
import com.thunsaker.rapido.data.api.model.FoursquareImage;
import com.thunsaker.rapido.services.foursquare.FoursquareUtils;

import java.util.List;

public class VenueListAdapter extends ArrayAdapter<CompactVenue> {
    public List<CompactVenue> mItems;
    private LayoutInflater mInflater;
    public int mResource;
    private Context mContext;

    public VenueListAdapter(Context context, List<CompactVenue> listItems) {
        this(context, R.layout.list_venue_item, listItems);
    }

    public VenueListAdapter(Context context, int resource, List<CompactVenue> listItems) {
        super(context, resource, listItems);
        mInflater = LayoutInflater.from(context);
        mItems = listItems;
        mResource = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(mResource, null);
        }
        try {
            final CompactVenue venue = mItems.get(position);
            if (venue != null) {
                final String myVenueName = venue.name != null ? venue.name : "";
                final String myVenueAddress = venue.location.address != null
                        ? venue.location.address : "";

                final TextView nameTextView = (TextView) v.findViewById(R.id.textViewVenueName);
                if (nameTextView != null)
                    nameTextView.setText(myVenueName);

                final TextView addressTextView =
                        (TextView) v.findViewById(R.id.textViewVenueAddress);
                if (addressTextView != null)
                    addressTextView.setText(myVenueAddress);

                final ImageView primaryCategoryImageView =
                        (ImageView) v.findViewById(R.id.imageViewVenueCategory);
                final ImageView primaryCategoryImageViewBackground =
                        (ImageView) v.findViewById(R.id.viewVenueCategory);
                List<FoursquareCategory> myCategories = venue.categories;
                if (myCategories != null && myCategories.size() > 0) {
                    final FoursquareCategory primaryCategory = myCategories.get(0) != null
                            ? myCategories.get(0)
                            : null;
                    if (primaryCategoryImageView != null && primaryCategory != null) {
                        String imageUrl = primaryCategory.icon
                                .getFoursquareLegacyImageUrl(FoursquareImage.SIZE_EXTRA_GRANDE, false);

                        Picasso mPicasso = Picasso.with(mContext);
                        mPicasso.load(imageUrl)
                                .placeholder(R.drawable.foursquare_generic_category_icon)
                                .into(primaryCategoryImageView);
                        Character myChar = primaryCategory.name.charAt(0);

                        Drawable circle =
                                primaryCategoryImageViewBackground.getDrawable();
                        circle = DrawableCompat.wrap(circle);
                        DrawableCompat.setTint(circle,
                                FoursquareUtils.GetCategoryColor(myChar, mContext));
                    }
                } else {
                    primaryCategoryImageView.setImageResource(
                            R.drawable.foursquare_generic_category_icon);

                    Drawable circle =
                            primaryCategoryImageViewBackground.getDrawable();
                    circle = DrawableCompat.wrap(circle);
                    DrawableCompat.setTint(circle,
                            mContext.getResources().getColor(R.color.gray_light));
                }

                final TextView distanceTextView = (TextView) v.findViewById(R.id.textViewVenueDistance);
                if(venue.location.distance > 0)
                    distanceTextView.setText(String.format(mContext.getString(R.string.distance_format_template), venue.location.distance));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}