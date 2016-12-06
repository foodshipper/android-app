package de.foodshippers.foodship.api.service;

import de.foodshippers.foodship.api.model.GroupInformations;
import de.foodshippers.foodship.api.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hannes on 06.12.16.
 */
public interface DinnerService {

    @GET("/v1//dinner/{dinner}")
    Call<GroupInformations> getGroupInformation(@Path("dinner") int groupid, @Query("token") String token);

    @PUT("/v1//dinner/{dinner}")
    Call<Void> acceptInvite(@Path("dinner") int groupid, @Query("token") String token, @Query("token") boolean accept);

    @GET("/v1//dinner/{dinner}/recipes")
    Call<Recipe[]> putReinvite(@Path("dinner") int groupid, @Query("token") String token);
}
