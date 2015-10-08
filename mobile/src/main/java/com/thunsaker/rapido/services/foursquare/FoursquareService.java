package com.thunsaker.rapido.services.foursquare;

import com.thunsaker.rapido.data.api.VenueSearchResponse;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface FoursquareService {
    /**
     *  Search for Venues!
     *  https://developer.foursquare.com/docs/venues/search/v2/venues/search
     *
     * @param oauth_token Auth Token {@link String}
     * @param latLong     Current GPS Coords from Device
     * @param query       Search query {@link String}
     * @param limit       Max number of search results to return {@link int}
     * @param intent      Either {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_BROWSE} or
     *                    {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_GLOBAL} or
     *                    {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN} or
     *                    {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_MATCH}
     * @param radius      Area to search within {@link int}
     * @return List of venues contained within {@link VenueSearchResponse}
     */
    @GET("/venues/search")
    Observable<VenueSearchResponse> searchVenuesNearby(
            @Query("oauth_token") String oauth_token,
            @Query("ll") String latLong,
            @Query("query") String query,
            @Query("limit") int limit,
            @Query("intent") String intent,
            @Query("radius") int radius);

    /**
     *  Search for Venues without Foursquare Login!
     *  https://developer.foursquare.com/docs/venues/search/v2/venues/search
     *
     * @param client_id
     * @param client_secret
     * @param query         Search query
     * @param near          Text search location instead of using device GPS
     * @param limit         Max number of search results to return {@link int}
     * @param intent        Either {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_BROWSE} or
     *                      {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_GLOBAL} or
     *                      {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN} or
     *                      {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_MATCH}
     * @param radius        Area to search within {@link int}
     * @return List of venues contained within {@link com.thunsaker.rapido.data.api.model.VenueSearchResponse}
     */
    @GET("/venues/search")
    Observable<VenueSearchResponse> searchVenuesNearbyUserless(
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("ll") String near,
            @Query("query") String query,
            @Query("limit") int limit,
            @Query("intent") String intent,
            @Query("radius") int radius);

    /**
     *  Search for Venues without Foursquare Login!
     *  https://developer.foursquare.com/docs/venues/search/v2/venues/search
     *
     * @param oauth_token Auth Token {@link String}
     * @param query       Search query
     * @param near        Text search location instead of using device GPS
     * @param limit       Max number of search results to return {@link int}
     * @param intent      Either {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_BROWSE} or
     *                    {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_GLOBAL} or
     *                    {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN} or
     *                    {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_MATCH}
     * @param radius      Area to search within {@link int}
     * @return List of venues contained within {@link com.thunsaker.rapido.data.api.model.VenueSearchResponse}
     */
    @GET("/venues/search")
    Observable<VenueSearchResponse> searchVenuesNearbyNoLatLng(
            @Query("oauth_token") String oauth_token,
            @Query("query") String query,
            @Query("near") String near,
            @Query("limit") int limit,
            @Query("intent") String intent,
            @Query("radius") int radius);

    /**
     *  Search for Venues without Foursquare Login!
     *  https://developer.foursquare.com/docs/venues/search/v2/venues/search
     *
     * @param client_id     Auth Token {@link String}
     * @param client_secret
     * @param query         Search query
     * @param near          Text search location instead of using device GPS
     * @param limit         Max number of search results to return {@link int}
     * @param intent        Either {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_BROWSE} or
     *                      {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_GLOBAL} or
     *                      {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_CHECKIN} or
     *                      {@link com.thunsaker.rapido.services.foursquare.FoursquarePrefs.FOURSQUARE_SEARCH_INTENT_MATCH}
     * @param radius        Area to search within {@link int}
     * @return List of venues contained within {@link com.thunsaker.rapido.data.api.model.VenueSearchResponse}
     */
    @GET("/venues/search")
    Observable<VenueSearchResponse> searchVenuesNearbyNoLatLngUserless(
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("query") String query,
            @Query("near") String near,
            @Query("limit") int limit,
            @Query("intent") String intent,
            @Query("radius") int radius);
}