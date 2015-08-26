package com.thunsaker.rapido.data.api.model;

import com.thunsaker.rapido.data.api.BaseFoursquareResponse;

public class PostUserCheckinResponse extends BaseFoursquareResponse {
    public CheckinResponse response;

    public class CheckinResponse {
        public Checkin checkin;
    }
}
