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
import org.json.JSONObject;
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
    final private Set<OnFoodChangesListener> nodes;
    final private List<Product> liste;
    private static FoodViewReFresher instance;

    @Override
    public void onResponse(Call<Product[]> call, Response<Product[]> response) {
        SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
        typeDatabase.execSQL("DELETE From ".concat(FoodshipContract.ProductTable.TABLE_NAME));
        liste.clear();
        ContentValues values = new ContentValues();
        for (Product o : response.body()) {
            values.put(FoodshipContract.ProductTable.CN_TYPE, o.getType());
            values.put(FoodshipContract.ProductTable.CN_EAN, o.getEan());
            values.put(FoodshipContract.ProductTable.CN_PUSHED, 0);
            typeDatabase.insert(FoodshipContract.ProductTable.TABLE_NAME, FoodshipContract.ProductTable.CN_EAN, values);
            liste.add(o);
        }
        notifyAllListeners();
//        Cursor query = typeDatabase.query(FoodshipContract.ProductTable.TABLE_NAME, null, null, null, null, null, null);
//        System.out.println(query.getCount());
//        while (query.moveToNext()) {
//            System.out.println(query.getString(0) + " " + query.getString(1) + " " + query.getString(2) + " " + query.getString(3));
//        }
        System.out.println("Updated Types");
    }

    @Override
    public void onFailure(Call<Product[]> call, Throwable t) {
        System.out.println(":( " + t.getMessage());
    }

    public interface OnFoodChangesListener {
        void onFoodChanges();
    }

    public void add(OnFoodChangesListener me) {
        nodes.add(me);
    }

    private void notifyAllListeners() {
        for (OnFoodChangesListener list : nodes) {
            list.onFoodChanges();
        }
    }

    public List<Product> getListe() {
        return liste;
    }

    private FoodViewReFresher(Context c) {
        this.c = c;
        liste = new ArrayList<>();
        nodes = new HashSet<>();
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
