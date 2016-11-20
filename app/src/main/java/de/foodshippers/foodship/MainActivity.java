package de.foodshippers.foodship;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.foodshippers.foodship.FoodFragment.FoodViewFragment;
import de.foodshippers.foodship.FoodFragment.GridViewAdapter;
import de.foodshippers.foodship.db.FoodshipDbHelper;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CommunicationManager conMan;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private FoodshipDbHelper databse;
    private int currentView;
    private final String CURRENT_VIEW_KEY = "currentView";
    private Fragment currentFragment;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        System.out.println("Secure " + CommunicationManager.getUserId(getApplicationContext()));

        //Floating Button

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).setOrientationLocked(true).setBeepEnabled(false)
                        .setDesiredBarcodeFormats(Arrays.asList("EAN_8", "EAN_13"))
                        .initiateScan();

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
        conMan = new CommunicationManager(getApplicationContext());
        //NetworkReceiver
        NetworkChangeReceiver.newInstance(conMan, getApplicationContext());

        //Create initial fragment
        if (savedInstanceState != null) {
            this.onNavigationItemSelected(savedInstanceState.getInt(CURRENT_VIEW_KEY, R.id.nav_groceries));
        } else {
            this.onNavigationItemSelected(R.id.nav_groceries);
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_VIEW_KEY, currentView);
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
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
//                conMan.sendFood(result.getContents());
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://api.foodshipper.de/v1/".concat("product/".concat(result.getContents())),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 404) {
                            UnknownFoodDialog newFragment = UnknownFoodDialog.newInstance(result.getContents());
                            newFragment.show(getFragmentManager(), "dialog");
                        } else {
                            Toast.makeText(getApplicationContext(), "BIG Fehler" + error.getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println(error);
                        }
                    }
                });
                queue.add(stringRequest);
                queue.start();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        return onNavigationItemSelected(id);
    }

    private boolean onNavigationItemSelected(Integer id) {
        if (currentView != id) {
            currentView = id;
            if (id == R.id.nav_groceries) {
                Log.d(TAG, "onNavigationItemSelected: Selected Grocery View");
                currentFragment = new FoodViewFragment();
            } else if (id == R.id.nav_dev) {
                Log.d(TAG, "onNavigationItemSelected: Selected Developer View");
                currentFragment = new DeveloperFragment();
            } else if (id == R.id.nav_contact) {
                Log.d(TAG, "onNavigationItemSelected: Selected Contact View");
                currentFragment = new ContactFragment();
            }
            getFragmentManager().beginTransaction().replace(R.id.main_placeholder, currentFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetworkChangeReceiver.unregister();
    }



}
