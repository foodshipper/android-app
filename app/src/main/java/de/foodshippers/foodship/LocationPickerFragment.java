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
import com.google.android.gms.maps.model.LatLng;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.SetUserLocationJob;

import static android.app.Activity.RESULT_OK;

/**
 * Creates A Dialog for the User to pick his home location
 */
public class LocationPickerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String TAG = "LocationPickerFragment";
    private int PLACE_PICKER_REQUEST = 1;
    private Button mLaterBtn;

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

        Button mPickBtn = (Button) v.findViewById(R.id.pickLocationBtn);
        mPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start place picker
                mLaterBtn.setVisibility(View.GONE);
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
        mLaterBtn = (Button) v.findViewById(R.id.pickLocationLaterBtn);
        mLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentInteraction();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                LatLng latLng = place.getLatLng();
                SharedPreferences.Editor sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
                sharedPreferences = Utils.putDouble(sharedPreferences, "latitude", latLng.latitude);
                sharedPreferences = Utils.putDouble(sharedPreferences, "longitude", latLng.latitude);
                sharedPreferences.apply();

                Context context = getActivity().getApplicationContext();
                FoodshipJobManager.getInstance(context).addJobInBackground(new SetUserLocationJob(CommunicationManager.getUserId(context), latLng.latitude, latLng.longitude));
                mListener.onFragmentInteraction();
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

    interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
