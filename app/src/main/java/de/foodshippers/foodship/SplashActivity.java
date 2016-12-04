package de.foodshippers.foodship;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Type;
import de.foodshippers.foodship.api.service.TypeService;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;
import retrofit2.Callback;

import java.util.Arrays;

/**
 * Created by hannes on 15.11.16.
 */

public class SplashActivity extends AppCompatActivity implements InitialSetupFragment.OnFragmentInteractionListener {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_PopupOverlay);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (Utils.getDouble(sharedPreferences, "latitude", 0) == 0 && Utils.getDouble(sharedPreferences, "longitude", 0) == 0) {
            //Frist Initial Start
            downloadFoodTypes(getApplicationContext(), true);
            setContentView(R.layout.activity_splash);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFragmentInteraction() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static void downloadFoodTypes(final Context c, boolean initial) {
        TypeService typeService = RestClient.getInstance().getTypeService();
        Call<Type[]> gettypes = typeService.gettypes();
        gettypes.enqueue(new Callback<Type[]>() {
            @Override
            public void onResponse(Call<Type[]> call, retrofit2.Response<Type[]> response) {
                SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
                typeDatabase.execSQL("DELETE FROM " + FoodshipContract.ProductTypeTable.TABLE_NAME);
                ContentValues values = new ContentValues();
                for (Type t : response.body()) {
                    values.put(FoodshipContract.ProductTypeTable.CN_ID, t.getId());
                    values.put(FoodshipContract.ProductTypeTable.CN_NAME, t.getName());
                    values.put(FoodshipContract.ProductTypeTable.CN_CATEGORY, t.getCategory());
                    values.put(FoodshipContract.ProductTypeTable.CN_IMAGEURL, t.getImageUrl());
                    typeDatabase.insert(FoodshipContract.ProductTypeTable.TABLE_NAME, FoodshipContract.ProductTypeTable.CN_NAME, values);
                }
                Log.d(TAG, "Types Updated");
//                Cursor query = typeDatabase.query(FoodshipContract.ProductTypeTable.TABLE_NAME, null, null, null, null, null, null);
//                System.out.println(query.getCount());
//                while (query.moveToNext()) {
//                    System.out.println(query.getString(0) + " " + query.getString(1));
//                }
            }

            @Override
            public void onFailure(Call<Type[]> call, Throwable t) {
                Log.d(TAG, "No internet or api down :|" + t.getMessage() + Arrays.toString(t.getStackTrace()));
            }
        });


    }

}