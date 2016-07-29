package com.example.administrator.jamme.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface{

    @GET("getData")
    Call<EventReponse> getEventMap();

    @GET("maps/api/place/autocomplete/json")
    Call<PredictionReponse> getPrediction(@Query("key") String key, @Query("components") String components,
                                          @Query("input") String input);

    @POST("post")
    Call<Event> postEvent(@Body Event event);
}

