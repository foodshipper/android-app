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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.foodshippers.foodship.FoodFragment.FoodViewFragment;
import de.foodshippers.foodship.FoodFragment.FoodViewReFresher;
import de.foodshippers.foodship.FoodFragment.GridViewAdapter;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Product;
import de.foodshippers.foodship.api.service.ProductService;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Callback<Product> {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private FoodshipDbHelper databse;
    private int currentView;
    private final String CURRENT_VIEW_KEY = "currentView";
    private Fragment currentFragment;
    private static final String TAG = "MainActivity";
    private FoodshipJobManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManager = FoodshipJobManager.getInstance(getApplicationContext());
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Log.d(TAG, CommunicationManager.getUserId(getApplicationContext()));

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
//        conMan = new CommunicationManager(getApplicationContext());
//        //NetworkReceiver
//        NetworkChangeReceiver.newInstance(conMan, getApplicationContext());

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
                ProductService pService = RestClient.getInstance().getProductService();
                Call<Product> getProductCall = pService.getProduct(result.getContents());
                getProductCall.enqueue(this);
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


    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link Response#isSuccessful()} to determine if the response indicates success.
     *
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call<Product> call, Response<Product> response) {
        Log.d(TAG, "onResponse: Got Response");
        if (call.request().url().toString().contains("items")) {
            Toast.makeText(getApplicationContext(), "und erfolgreich Hinzugefügt", Toast.LENGTH_LONG).show();
            FoodViewReFresher.getInstance(getApplicationContext()).refreshFood();
        } else if (response.isSuccessful()) {
            Log.d(TAG, "onResponse: Product is known");
            Product p = response.body();
            Toast.makeText(getApplicationContext(), p.getType() + " with name " + p.getName(), Toast.LENGTH_LONG).show();
            Call<Product> productCall = RestClient.getInstance().getFridgeService().addItem(p.getEan(), CommunicationManager.getUserId(getApplicationContext()));
            productCall.enqueue(this);
        } else {
            Log.d(TAG, "onResponse: Product is unknown or different error");
            if (response.code() == 404) {
                Log.d(TAG, "onResponse: Product is unknown");
                List<String> pathNames = call.request().url().encodedPathSegments();
                String ean = pathNames.get(pathNames.size() - 1);
                Log.d(TAG, "onResponse: EAN: " + ean);
                UnknownFoodDialog newFragment = UnknownFoodDialog.newInstance(ean);
                newFragment.show(getFragmentManager(), "dialog");
            } else {
                Log.d(TAG, "onResponse: Got " + response.code() + " Response");
            }
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     *
     * @param call
     * @param t
     */
    @Override
    public void onFailure(Call<Product> call, Throwable t) {
        Log.d(TAG, "onFailure: Got Failure");
    }
}
