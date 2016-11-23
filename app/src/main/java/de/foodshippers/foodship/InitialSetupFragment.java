package de.foodshippers.foodship;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.SetUserLocationJob;
import de.foodshippers.foodship.api.SetUserNameJob;

import static android.app.Activity.RESULT_OK;

/**
 * Creates A Dialog for the User to pick his home location
 */
public class InitialSetupFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String TAG = "InitialSetupFragment";
    private int PLACE_PICKER_REQUEST = 1;
    private EditText mName;
    private ProgressBar mProgress;

    public InitialSetupFragment() {
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
        View v = inflater.inflate(R.layout.fragment_initial_setup, container, false);
        mProgress = (ProgressBar) v.findViewById(R.id.initialSetupProgress);

        final Button mPickBtn = (Button) v.findViewById(R.id.initialSetupConfirmBtn);
        mPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString().trim();
                name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

                SharedPreferences.Editor sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
                sharedPreferences.putString("name", name);
                sharedPreferences.apply();
                FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new SetUserNameJob(CommunicationManager.getUserId(getActivity()), name));


                Toast.makeText(getActivity(), getString(R.string.thanks_name, name), Toast.LENGTH_LONG).show();
                mProgress.setEnabled(true);
                mProgress.setVisibility(View.VISIBLE);
                //Start place picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(InitialSetupFragment.this.getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: Please install Google Play Services");
                    e.printStackTrace();
                }
            }
        });

        mName = (EditText) v.findViewById(R.id.nameEditText);
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mName.getText().toString().equals(getString(R.string.name))) {
                    mName.setText("");
                }
            }
        });
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mName.getText().toString().length() > 2 && !mName.getText().toString().equals(getString(R.string.name))) {
                    mPickBtn.setEnabled(true);
                } else {
                    mPickBtn.setEnabled(false);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && data != null) {
            mProgress.setEnabled(false);
            mProgress.setVisibility(View.GONE);
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
