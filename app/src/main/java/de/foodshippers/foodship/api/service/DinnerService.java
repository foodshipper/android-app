package de.foodshippers.foodship.api.service;

import de.foodshippers.foodship.api.model.GroupInformation;
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

    @GET("/v1/dinner/{group_id}")
    Call<GroupInformation> getGroupInformation(@Path("group_id") int groupid, @Query("token") String token);

    @PUT("/v1/dinner/{group_id}")
    Call<Void> acceptInvite(@Path("group_id") int groupid, @Query("token") String token, @Query("accept") boolean accept);

    @GET("/v1/dinner/{group_id}/recipes")
    Call<Recipe[]> getRecipes(@Path("group_id") int groupid, @Query("token") String token);

    @PUT("/v1/dinner/{group_id}/recipes")
    Call<Void> voteRecipe(@Path("group_id") int groupid, @Query("token") String token, @Query("recipe_id") int recipe_id, @Query("action") String action);
}
