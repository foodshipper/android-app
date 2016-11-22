package de.foodshippers.foodship.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.foodshippers.foodship.api.service.ProductService;
import de.foodshippers.foodship.api.service.UserLocationService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by soenke on 21.11.16.
 */
public class RestClient {

    private static final String BASE_URL = "https://api.foodshipper.de/";
    private ProductService productService;
    private UserLocationService userLocationService;
    private static RestClient instance = null;


    private RestClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        productService = retrofit.create(ProductService.class);
        userLocationService = retrofit.create(UserLocationService.class);
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public ProductService getProductService() {
        return productService;
    }

    public UserLocationService getUserLocationService() {
        return userLocationService;
    }
}
