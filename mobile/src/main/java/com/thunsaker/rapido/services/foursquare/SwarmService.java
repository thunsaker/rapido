package com.thunsaker.rapido.services.foursquare;

import com.thunsaker.rapido.data.api.model.PostUserCheckinResponse;

import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface SwarmService {
    /**
     *
     * Check a user into a location.
     * More: https://developer.foursquare.com/docs/checkins/add
     *
     * @param oauth_token   Auth Token
     * @param venueId       Venue Id {@link java.util.UUID} for which to fetch hours
     * @param latLong     Current GPS Coords from Device
     * @return A checkin object with details about the checkin
     */
    @POST("/checkins/add")
    Observable<PostUserCheckinResponse> postUserCheckin(
            @Query("oauth_token") String oauth_token,
            @Query("venueId") String venueId,
            @Query("ll") String latLong
    );

    /**
     * Check a user into a location.
     * More: https://developer.foursquare.com/docs/checkins/add
     *
     * @param oauth_token   Auth Token
     * @param venueId       Venue Id {@link java.util.UUID} for which to fetch hours
     * @param shout         Message to accompany checkin
     * @param mentions      Users that are mentioned within the shout
     * @param latLong       Current GPS Coords from Device
     * @return A checkin object with details about the checkin
     */
    @POST("/checkins/add")
    Observable<PostUserCheckinResponse> postUserCheckinWithShout(
            @Query("oauth_token") String oauth_token,
            @Query("venueId") String venueId,
            @Query("shout") String shout,
            @Query("mentions") String mentions,
            @Query("ll") String latLong
    );
}