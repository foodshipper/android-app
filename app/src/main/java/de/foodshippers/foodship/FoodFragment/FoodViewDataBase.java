package de.foodshippers.foodship.FoodFragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.foodshippers.foodship.Utils;
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
public class FoodViewDataBase implements Callback<Product[]> {

    final private Context c;
    final private Set<OnFoodChangesListener> observer;
    final private List<Product> foodList;
    private static FoodViewDataBase instance;
    private boolean initialisiert = false;
    private static final String TAG = FoodViewDataBase.class.getSimpleName();

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
        while (query.moveToNext()) {
            Log.d(TAG, query.getString(0) + " " + query.getString(1) + " " + query.getString(2) + " " + query.getString(3));
        }
        Log.d(TAG, "Loaded Food from DataBase");
        typeDatabase.close();
    }

    @Override
    public void onFailure(Call<Product[]> call, Throwable t) {
        Log.d(TAG, "No internet or other error while refreshing: " + t.getMessage());
        loadFromDataBase();
        notifyAllListeners();
    }

    private void loadFromDataBase() {
        SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getReadableDatabase();
        Cursor query = typeDatabase.query(FoodshipContract.ProductTable.TABLE_NAME, null, null, null, null, null, null);
        foodList.clear();
        initialisiert = true;
        while (query.moveToNext()) {
            Log.d(TAG, query.getString(0) + " " + query.getString(1) + " " + query.getString(2) + " " + query.getString(3));
            Product p = new Product("", query.getString(1), query.getString(2));
            foodList.add(p);
        }
        Log.d(TAG, "Loaded Food from DataBase");
        typeDatabase.close();
    }


    public interface OnFoodChangesListener {
        void onFoodChangesNotyfi();
    }

    public void add(OnFoodChangesListener me) {
        observer.add(me);
    }

    public void remove(OnFoodChangesListener me) {
        observer.remove(me);
    }

    private void notifyAllListeners() {
        ((Activity) c).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (OnFoodChangesListener list : observer) {
                    list.onFoodChangesNotyfi();
                }
            }
        });
    }

    public List<Product> getFoodList() {
        if (initialisiert == false) {
            refreshFood();
        }
        return foodList;
    }

    private FoodViewDataBase(Context c) {
        this.c = c;
        foodList = new ArrayList<>();
        observer = new HashSet<>();
    }

    public void refreshFood() {
        FridgeService frides = RestClient.getInstance().getFridgeService();
        Call<Product[]> items = frides.getItems(Utils.getUserId(c));
        items.enqueue(this);
    }

    public boolean addFood(Product p) {
        SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
        Cursor cursor = typeDatabase.rawQuery("SELECT * From ".concat(FoodshipContract.ProductTable.TABLE_NAME).concat(" WHERE ").concat(FoodshipContract.ProductTable.CN_EAN).concat(" = ".concat(p.getEan())), null);
        System.out.println(cursor.getCount());
        if (cursor.getCount() != 0) {
            typeDatabase.close();
            return false;
        } else {
            System.out.println("Hinzu");
            ContentValues values = new ContentValues();
            values.put(FoodshipContract.ProductTable.CN_TYPE, p.getType());
            values.put(FoodshipContract.ProductTable.CN_EAN, p.getEan());
            values.put(FoodshipContract.ProductTable.CN_PUSHED, 0);
            typeDatabase.insert(FoodshipContract.ProductTable.TABLE_NAME, FoodshipContract.ProductTable.CN_EAN, values);
            foodList.add(p);
            notifyAllListeners();
            return true;
        }
    }

    public boolean deleteFood(Product p) {
        SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
        Cursor cursor = typeDatabase.rawQuery("SELECT * From ".concat(FoodshipContract.ProductTable.TABLE_NAME).concat(" WHERE ").concat(FoodshipContract.ProductTable.CN_EAN).concat(" = ".concat(p.getEan())), null);
        System.out.println(cursor.getCount());
        if (cursor.getCount() != 0) {
            typeDatabase.execSQL("DELETE From ".concat(FoodshipContract.ProductTable.TABLE_NAME).concat(" WHERE ").concat(FoodshipContract.ProductTable.CN_EAN).concat(" = ".concat(p.getEan())));
            loadFromDataBase();
            notifyAllListeners();
            return true;
        } else {
            return false;
        }


    }

    public static FoodViewDataBase getInstance(Context c) {
        if (instance == null) {
            synchronized (FoodViewDataBase.class) {
                if (instance == null) {
                    instance = new FoodViewDataBase(c);
                }
            }
        }
        return instance;
    }

}
