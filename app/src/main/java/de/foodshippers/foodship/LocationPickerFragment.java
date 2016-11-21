package de.foodshippers.foodship;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.service.UserLocationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Creates A Dialog for the User to pick his home location
 */
public class LocationPickerFragment extends Fragment implements Callback {
    private OnFragmentInteractionListener mListener;
    private static final String TAG = "LocationPickerFragment";
    private int PLACE_PICKER_REQUEST = 1;

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
                UserLocationService userLocationService = new RestClient().getUserLocationService();
                Call call = userLocationService.setHomeLocation(CommunicationManager.getUserId(getActivity().getApplicationContext()), place.getLatLng().latitude, place.getLatLng().longitude);
                call.enqueue(this);

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
    public void onResponse(Call call, Response response) {
        if(response.isSuccessful()) {
            Log.d(TAG, "onResponse: Successfully set location");
            mListener.onFragmentInteraction();
        } else {
            Log.d(TAG, "onResponse: Could not set location");
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
    public void onFailure(Call call, Throwable t) {

    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
