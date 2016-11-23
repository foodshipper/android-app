package de.foodshippers.foodship.api.service;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by soenke on 21.11.16.
 */
public interface UserLocationService {
    @PUT("/v1/user/home-location")
    Call<Void> setHomeLocation(@Query("token") String token, @Query("lat") double latitude, @Query("lon") double longitude);

}
