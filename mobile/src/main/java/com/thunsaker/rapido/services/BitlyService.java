package com.thunsaker.rapido.services;

import com.thunsaker.rapido.data.api.ShortenResponse;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface BitlyService {
    /*
        Shorten Link
        http://dev.bitly.com/links.html#v3_shorten
        /v3/shorten
    */
    @GET("/v3/shorten")
    // Async w/RxJava
    Observable<ShortenResponse> createBitmark(
            @Query("access_token") String access_token,
            @Query("longUrl") String longUrl,
            @Query("domain") String domain);

//     Async w/callback
//     void createBitmark(
//             @Query("access_token") String access_token,
//             @Query("longUrl") String longUrl,
//             @Query("domain") String domain,
//             Callback<ShortenResponse> callback);

    @POST("/oauth/access_token")
    Observable<Response> getAccessToken(
            @Body String body,
            @Query("code") String code,
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("redirect_uri") String redirect_uri);
}