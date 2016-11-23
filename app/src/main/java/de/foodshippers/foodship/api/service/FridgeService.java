package de.foodshippers.foodship.api.service;

import de.foodshippers.foodship.api.model.Product;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by hannes on 23.11.16.
 */
public interface FridgeService {

    @GET("/v1/items")
    Call<Product[]> getItems(@Query("user_id") String userid);

    @PUT("/v1/items/{ean}")
    Call<Product> addItem(@Path("ean") String ean, @Query("user_id") String userid);

    @DELETE("/v1/items/{ean}")
    Call<Product> removeItem(@Path("ean") String ean, @Query("user_id") String userid);
}
