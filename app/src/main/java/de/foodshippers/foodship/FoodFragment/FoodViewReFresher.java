package de.foodshippers.foodship.FoodFragment;

import android.content.Context;
import de.foodshippers.foodship.CommunicationManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Product;
import de.foodshippers.foodship.api.service.FridgeService;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hannes on 23.11.16.
 */
public class FoodViewReFresher implements Callback<Product[]> {

    final private Context c;

    @Override
    public void onResponse(Call<Product[]> call, Response<Product[]> response) {
        for (Product o : response.body()) {
            System.out.println(o.toString());
        }
    }

    @Override
    public void onFailure(Call<Product[]> call, Throwable t) {
        System.out.println(":( " + t.getMessage());
    }

    public interface OnFoodChangesListener {
        void onFoodChanges();
    }

    private static FoodViewReFresher instance;

    private FoodViewReFresher(Context c) {
        this.c = c;

    }

    public void refreshFood() {
        FridgeService frides = RestClient.getInstance().getFridgeService();
        Call<Product[]> items = frides.getItems(CommunicationManager.getUserId(c));
        System.out.println(items.request().url());
        items.enqueue(this);


    }

    public static FoodViewReFresher getInstance(Context c) {
        if (instance == null) {
            instance = new FoodViewReFresher(c);
        }
        return instance;
    }
}
