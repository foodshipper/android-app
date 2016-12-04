package de.foodshippers.foodship.api.service;

import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by soenke on 21.11.16.
 */
public interface ProductService {

    @GET("/v1/product/{ean}")
    Call<Product> getProduct(@Path("ean") String ean);

    @PUT("/v1/product/{ean}")
    Call<Void> addProduct(@Path("ean") String ean, @Query("name") String name, @Query("type") int type);
}
