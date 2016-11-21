package de.foodshippers.foodship.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.foodshippers.foodship.api.service.ProductService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by soenke on 21.11.16.
 */
public class RestClient {
    private static final String BASE_URL = "http://api.foodshipper.de/";
    private ProductService productService;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        productService = retrofit.create(ProductService.class);
    }

    public ProductService getProductService()
    {
        return productService;
    }
}
