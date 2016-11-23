package de.foodshippers.foodship.api.service;

import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by hannes on 23.11.16.
 */
public interface FridgeService {

    @GET("/v1/items")
    Call<Product[]> getItems(@Query("token") String token);

    @PUT("/v1/items/{ean}")
    Call<Product> addItem(@Path("ean") String ean, @Query("token") String token);

    @DELETE("/v1/items/{ean}")
    Call<Product> removeItem(@Path("ean") String ean, @Query("token") String token);
}
