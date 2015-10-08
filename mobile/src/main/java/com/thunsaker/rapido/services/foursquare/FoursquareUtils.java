package com.thunsaker.rapido.services.foursquare;

import android.content.Context;

import com.thunsaker.rapido.R;

public class FoursquareUtils {
    public static int GetCategoryColor(Character mChar, Context mContext) {
        int color;
        switch (Character.getNumericValue(mChar) % 5) {
            case 0:
                color = mContext.getResources().getColor(R.color.category_travel);
                break;
            case 1:
                color = mContext.getResources().getColor(R.color.category_art);
                break;
            case 2:
                color = mContext.getResources().getColor(R.color.category_night);
                break;
            case 3:
                color = mContext.getResources().getColor(R.color.category_school);
                break;
            case 4:
                color = mContext.getResources().getColor(R.color.category_food);
                break;
            default:
                color = mContext.getResources().getColor(R.color.category_event);
                break;
        }
        return color;
    }
}
