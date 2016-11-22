package de.foodshippers.foodship.api.service;

import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hannes on 22.11.16.
 */
public interface TypeService {

    @GET("/v1/types")
    Call<String[]> gettypes();
}
