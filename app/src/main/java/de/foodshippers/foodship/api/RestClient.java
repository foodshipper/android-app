package de.foodshippers.foodship.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.foodshippers.foodship.api.service.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by soenke on 21.11.16.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();
    private static final String BASE_URL = "http://foodship.hsht.de/";
    private ProductService productService;
    private UserLocationService userLocationService;
    private UserFirebaseTokenService userFirebaseTokenService;
    private UserNameService userNameService;
    private TypeService typeService;
    private FridgeService fridgeService;
    private DinnerService DinnerService;

    public TriggerInvitationService getTriggerInvitationService() {
        return triggerInvitationService;
    }

    private TriggerInvitationService triggerInvitationService;
    private static RestClient instance = null;


    private RestClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.productService = retrofit.create(ProductService.class);
        this.userLocationService = retrofit.create(UserLocationService.class);
        this.typeService = retrofit.create(TypeService.class);
        this.fridgeService = retrofit.create(FridgeService.class);
        this.userNameService = retrofit.create(UserNameService.class);
        this.userFirebaseTokenService = retrofit.create(UserFirebaseTokenService.class);
        this.triggerInvitationService = retrofit.create(TriggerInvitationService.class);
        this.DinnerService = retrofit.create(DinnerService.class);
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

    public FridgeService getFridgeService() {
        return fridgeService;
    }

    public TypeService getTypeService() {
        return typeService;
    }

    public UserNameService getUserNameService() {
        return userNameService;
    }

    public UserFirebaseTokenService getUserFirebaseTokenService() {
        return userFirebaseTokenService;
    }

    public de.foodshippers.foodship.api.service.DinnerService getDinnerService() {
        return DinnerService;
    }
}
