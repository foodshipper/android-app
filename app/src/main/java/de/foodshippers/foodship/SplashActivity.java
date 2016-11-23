package de.foodshippers.foodship;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.service.TypeService;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by hannes on 15.11.16.
 */

public class SplashActivity extends AppCompatActivity implements InitialSetupFragment.OnFragmentInteractionListener {

    private static final String TAG = "SplashActivity";

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
//        FoodshipDbHelper helper = new FoodshipDbHelper(c);
//        RequestQueue que = Volley.newRequestQueue(c);
//        Uri.Builder uriBuilder = new Uri.Builder().scheme("http")
//                .authority("api.foodshipper.de")
//                .appendPath("v1")
//                .appendPath("types");
//
//        StringRequest rq = new StringRequest(Request.Method.GET, uriBuilder.build().toString(), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
//                typeDatabase.execSQL("DELETE From ".concat(FoodshipContract.ProductTypeTable.TABLE_NAME));
//                try {
//                    JSONArray arr = new JSONArray(response);
//                    ContentValues values = new ContentValues();
//                    for (int i = 0; i < arr.length(); i++) {
//                        values.put(FoodshipContract.ProductTypeTable.CN_NAME, (String) arr.get(i));
//                        typeDatabase.insert(FoodshipContract.ProductTypeTable.TABLE_NAME, FoodshipContract.ProductTypeTable.CN_NAME, values);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Updated Types");
////                Cursor query = typeDatabase.query(FoodshipContract.ProductTypeTable.TABLE_NAME, null, null, null, null, null, null);
////                System.out.println(query.getCount());
////                while (query.moveToNext()) {
////                    System.out.println(query.getString(0) + " " + query.getString(1));
////                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // TODO - Do somthing on error. Kill or restart as long there is no I-NetConnection
//
//            }
//        });
//        que.add(rq);
//        que.start();

        TypeService typeService = RestClient.getInstance().getTypeService();
        Call<String[]> gettypes = typeService.gettypes();
        gettypes.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, retrofit2.Response<String[]> response) {
                SQLiteDatabase typeDatabase = new FoodshipDbHelper(c).getWritableDatabase();
                typeDatabase.execSQL("DELETE From ".concat(FoodshipContract.ProductTypeTable.TABLE_NAME));
                ContentValues values = new ContentValues();
                for (String type : response.body()) {
                    values.put(FoodshipContract.ProductTypeTable.CN_NAME, type);
                    typeDatabase.insert(FoodshipContract.ProductTypeTable.TABLE_NAME, FoodshipContract.ProductTypeTable.CN_NAME, values);
                }
                System.out.println("Updated Types");
//                Cursor query = typeDatabase.query(FoodshipContract.ProductTypeTable.TABLE_NAME, null, null, null, null, null, null);
//                System.out.println(query.getCount());
//                while (query.moveToNext()) {
//                    System.out.println(query.getString(0) + " " + query.getString(1));
//                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                System.out.println("No internet or api down :|" + t.getMessage() + t.getStackTrace());
            }
        });


    }

}