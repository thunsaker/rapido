package com.thunsaker.rapido.data.api;

public class BaseFoursquareResponse {
    public FoursquareResponseMeta meta;
    public Object[] notifications;

    public class FoursquareResponseMeta extends FoursquareResponseError {
        public int code;
    }

    public class FoursquareResponseError {
        public String errorType;
        public String errorDetail;
    }
}