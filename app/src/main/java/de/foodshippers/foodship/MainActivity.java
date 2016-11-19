package de.foodshippers.foodship;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.foodshippers.foodship.FoodFragment.FoodViewFragment;
import de.foodshippers.foodship.FoodFragment.GridViewAdapter;
import de.foodshippers.foodship.db.FoodshipDbHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CommunicationManager conMan;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private FoodshipDbHelper databse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            System.out.println("Secure " + Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        //Floating Button

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                new IntentIntegrator(MainActivity.this).setOrientationLocked(true).setBeepEnabled(false)
//                        .setDesiredBarcodeFormats(Arrays.asList("EAN_8", "EAN_13"))
//                        .initiateScan();
//                JSONArray jsonObject = null;
//                try {
//                    jsonObject = new JSONArray("[\"milk\", \"water\", \"tomato\", \"flour\", \"pork\", \"chicken\", \"beef\", \"undefined\"]");
//                    for (int i = 0; i < jsonObject.length(); i++) {
//                        ContentValues values = new ContentValues();
//                        values.put(FoodshipContract.ProductTypeTable.CN_NAME, (String) jsonObject.get(i));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Cursor cursor = getApplicationContext().getContentResolver().query(new Uri.Builder().scheme("content").authority(FoodshipContract.ProductTypeTable.TABLE_NAME).build(),
//                        null, null, null, null, null);
//                System.out.println(cursor.getColumnCount());
//
//
//                conMan.getTypes();
            }
        });

        // DrawerToggle

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //DateBase
        databse = new FoodshipDbHelper(getApplicationContext());
        //Communication Manager
        conMan = new CommunicationManager(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID), getApplicationContext());
        //NetworkReceiver
        NetworkChangeReceiver netreceiver = new NetworkChangeReceiver(conMan, getApplication());
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        super.registerReceiver(netreceiver, filters);
        //FoodFragment
        FoodViewFragment frag = new FoodViewFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_placeholder, frag).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                conMan.sendFood(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
