package com.rideshare.app.acitivities;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GoogleDirectionsApiService {
//    @GET("maps/api/directions/json")
    @GET("https://maps.googleapis.com/maps/api/directions/json")
    Call<com.rideshare.app.models.DirectionsResponses> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String apiKey
    );
}
