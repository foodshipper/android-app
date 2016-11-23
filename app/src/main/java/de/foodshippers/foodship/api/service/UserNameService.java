package de.foodshippers.foodship.api.service;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by soenke on 21.11.16.
 */
public interface UserNameService {
    @PUT("/v1/user/name")
    Call<Void> setUserName(@Query("token") String token, @Query("name") String name);

}
