package de.foodshippers.foodship;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by hannes on 15.11.16.
 */

public class SplashActivity extends AppCompatActivity implements FirstStartFragment.OnFragmentInteractionListener {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_PopupOverlay);
        switch (AppVersionStart.checkAppStart(getApplicationContext())) {
            case FIRST_TIME:
                setContentView(R.layout.activity_splash);
                break;
            case FIRST_TIME_VERSION:
            case NORMAL:
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

    private static class AppVersionStart {

        enum AppStart {
            FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
        }

        private static final String LAST_APP_VERSION = "last_app_version";

        static AppStart checkAppStart(Context context) {
            PackageInfo pInfo;
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            AppStart appStart = AppStart.NORMAL;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                int lastVersionCode = sharedPreferences
                        .getInt(LAST_APP_VERSION, -1);
                int currentVersionCode = pInfo.versionCode;
                appStart = checkAppStart(currentVersionCode, lastVersionCode);
                // Update version in preferences
                sharedPreferences.edit()
                        .putInt(LAST_APP_VERSION, currentVersionCode).apply();
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG,
                        "Unable to determine current app version from pacakge manager. Defensively assuming normal app start.");
            }
            return appStart;
        }

        private static AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
            if (lastVersionCode == -1) {
                return AppStart.FIRST_TIME;
            } else if (lastVersionCode < currentVersionCode) {
                return AppStart.FIRST_TIME_VERSION;
            } else if (lastVersionCode > currentVersionCode) {
                Log.w(TAG, "Current version code (" + currentVersionCode
                        + ") is less then the one recognized on last startup ("
                        + lastVersionCode
                        + "). Defensively assuming normal app start.");
                return AppStart.NORMAL;
            } else {
                return AppStart.NORMAL;
            }
        }
    }
}