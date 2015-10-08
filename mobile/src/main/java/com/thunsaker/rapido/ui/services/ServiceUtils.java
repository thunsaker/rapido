package com.thunsaker.rapido.ui.services;

import com.thunsaker.rapido.R;
import com.thunsaker.rapido.services.UpdateService;

public class ServiceUtils {
    public static int GetServiceColor(UpdateService service) {
        int color = 0;
        switch (service) {
            case SERVICE_GOOGLE_PLUS:
                color = R.color.plus;
                break;
            case SERVICE_FACEBOOK:
                color = R.color.facebook;
                break;
            case SERVICE_FOURSQUARE:
                color = R.color.foursquare;
                break;
            case SERVICE_TWITTER:
                color = R.color.twitter;
                break;
            case SERVICE_BITLY:
                color = R.color.bitly;
                break;
        }
        return color;
    }

    public static int GetServiceTextColor(UpdateService service) {
        int color = R.color.white;
        switch (service) {
            case SERVICE_FOURSQUARE:
                color = R.color.body_text;
                break;
            case SERVICE_TWITTER:
                color = R.color.body_text;
                break;
        }

        return color;
    }
}