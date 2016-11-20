package de.foodshippers.foodship;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeveloperFragment extends Fragment {


    public DeveloperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_developer, container, false);
        Button resetLocation = (Button) v.findViewById(R.id.resetLocationBtn);
        resetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .edit();
                Utils.putDouble(e, "latitude", 0);
                Utils.putDouble(e, "longitude", 0);
                e.apply();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
            }
        });

        Button refreshFoodTypes = (Button) v.findViewById(R.id.refreshFoodTypesBtn);
        refreshFoodTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.downloadFoodTypes(getActivity().getApplicationContext(),false);
            }
        });
        return v;
    }

}
