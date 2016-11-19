package de.foodshippers.foodship;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hannes on 15.11.16.
 */

public class SplashActivity extends AppCompatActivity implements LocationPickerFragment.OnFragmentInteractionListener {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_PopupOverlay);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if(Utils.getDouble(sharedPreferences, "latitude", 0) == 0 && Utils.getDouble(sharedPreferences, "longitude", 0) == 0) {
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

}