package de.foodshippers.foodship.FoodFragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.foodshippers.foodship.CommunicationManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Product;
import de.foodshippers.foodship.api.service.FridgeService;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hannes on 23.11.16.
 */
public class FoodViewReFresher implements Callback<Product[]> {

    final private Context c;
    final private Set<OnFoodChangesListener> observer;
    final private List<Product> foodList;
    private static FoodViewReFresher instance;
    boolean initialisiert = false;

    @Override
    public void onResponse(Call<Product[]> call, Response<Product[]> response) {
        SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
        typeDatabase.execSQL("DELETE From ".concat(FoodshipContract.ProductTable.TABLE_NAME));
        foodList.clear();
        initialisiert = true;
        ContentValues values = new ContentValues();
        if (response.body() != null) {
            for (Product o : response.body()) {
                values.put(FoodshipContract.ProductTable.CN_TYPE, o.getType());
                values.put(FoodshipContract.ProductTable.CN_EAN, o.getEan());
                values.put(FoodshipContract.ProductTable.CN_PUSHED, 0);
                typeDatabase.insert(FoodshipContract.ProductTable.TABLE_NAME, FoodshipContract.ProductTable.CN_EAN, values);
                foodList.add(o);
            }
        }
        notifyAllListeners();
        Cursor query = typeDatabase.query(FoodshipContract.ProductTable.TABLE_NAME, null, null, null, null, null, null);
        System.out.println(query.getCount());
        while (query.moveToNext()) {
            System.out.println(query.getString(0) + " " + query.getString(1) + " " + query.getString(2) + " " + query.getString(3));
        }
        System.out.println("Loaded Food from DataBase");
    }

    @Override
    public void onFailure(Call<Product[]> call, Throwable t) {
        System.out.println("Kein Internet / anderer Fehler " + t.fillInStackTrace());
        if (!initialisiert) {
            SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getReadableDatabase();
            Cursor query = typeDatabase.query(FoodshipContract.ProductTable.TABLE_NAME, null, null, null, null, null, null);
            System.out.println(query.getCount());
            foodList.clear();
            initialisiert = true;
            while (query.moveToNext()) {
                System.out.println(query.getString(0) + " " + query.getString(1) + " " + query.getString(2) + " " + query.getString(3));
                Product p = new Product("", query.getString(1), query.getString(2));
                foodList.add(p);
            }
            System.out.println("Loaded Food from DataBase");
        }
        notifyAllListeners();
    }


    public interface OnFoodChangesListener {
        void onFoodChangesNotyfi();
    }

    public void add(OnFoodChangesListener me) {
        //Hack
        observer.clear();
        observer.add(me);
    }

    private void notifyAllListeners() {
        System.out.println("Notfy" + observer.size());
        for (OnFoodChangesListener list : observer) {
            list.onFoodChangesNotyfi();
        }
    }

    public List<Product> getFoodList() {
        if(initialisiert == false){
            refreshFood();
        }
        return foodList;
    }

    private FoodViewReFresher(Context c) {
        this.c = c;
        foodList = new ArrayList<>();
        observer = new HashSet<>();
    }

    public void refreshFood() {
        FridgeService frides = RestClient.getInstance().getFridgeService();
        Call<Product[]> items = frides.getItems(CommunicationManager.getUserId(c));
        items.enqueue(this);
    }

    public void deleteFood(Product p) {
        Call<Product> productCall = RestClient.getInstance().getFridgeService().removeItem(p.getEan(), CommunicationManager.getUserId(c));
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    refreshFood();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

    }

    public static FoodViewReFresher getInstance(Context c) {
        if (instance == null) {
            instance = new FoodViewReFresher(c);
        }
        return instance;
    }
}
