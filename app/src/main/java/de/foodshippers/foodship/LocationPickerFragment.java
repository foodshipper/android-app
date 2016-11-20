package de.foodshippers.foodship;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;

/**
 * Creates A Dialog for the User to pick his home location
 */
public class LocationPickerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String TAG = "LocationPickerFragment";
    private int PLACE_PICKER_REQUEST = 1;
    private SendLocationTask mTask;

    public LocationPickerFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location_picker, container, false);

        Button pickLocationBtn = (Button) v.findViewById(R.id.pickLocationBtn);
        pickLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start place picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(LocationPickerFragment.this.getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: Please install Google Play Services");
                    e.printStackTrace();
                }
            }
        });
        return v;
    }

    private void onInitialized() {
        Log.d(TAG, "onInitialized: Fragment is finished!");
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                SharedPreferences.Editor sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
                sharedPreferences = Utils.putDouble(sharedPreferences, "latitude", place.getLatLng().latitude);
                sharedPreferences = Utils.putDouble(sharedPreferences, "longitude", place.getLatLng().longitude);
                sharedPreferences.putBoolean("location_sent", false);
                sharedPreferences.apply();

                mTask = new SendLocationTask();
                mTask.sendLocation(place);
            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mTask != null) {
            mTask.stop();
        }
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    private class SendLocationTask {
        private ProgressBar mProgress = null;
        private Button mPickBtn = null;
        private RequestQueue queue;

        void sendLocation(final Place homelocation) {
            View v = getView();
            if(v != null) {
                mPickBtn = (Button) v.findViewById(R.id.pickLocationBtn);
                mProgress = (ProgressBar) v.findViewById(R.id.sendLocationProgress);
                mPickBtn.setEnabled(false);
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setIndeterminate(true);
                mProgress.setEnabled(true);
            }

            queue = Volley.newRequestQueue(getContext());
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .authority("api.foodshipper.de")
                    .appendPath("v1")
                    .appendPath("home-location")
                    .appendQueryParameter("user_id", CommunicationManager.getUserId(getContext()))
                    .appendQueryParameter("lon", String.valueOf(homelocation.getLatLng().longitude))
                    .appendQueryParameter("lat", String.valueOf(homelocation.getLatLng().latitude));

            StringRequest rq = new StringRequest(Request.Method.PUT, uriBuilder.build().toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SharedPreferences.Editor sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
                    sharedPreferences.putBoolean("location_sent", true);
                    sharedPreferences.apply();
                    LocationPickerFragment.this.onInitialized();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: " + error);
                    Toast.makeText(getContext(), R.string.error_set_location, Toast.LENGTH_LONG).show();

                    //Create a Try-Again. Just repeats the same request
                    if(mProgress != null) {
                        mProgress.setVisibility(View.GONE);
                    }
                    if(mPickBtn != null) {
                        mPickBtn.setText(R.string.try_again);
                        mPickBtn.setEnabled(true);
                        mPickBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SendLocationTask.this.sendLocation(homelocation);
                            }
                        });
                    }
                }
            });
            queue.add(rq);
            queue.start();
        }

        public void stop() {
            if(queue != null) {
                queue.stop();
            }
        }
    }
}
