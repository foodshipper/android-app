package de.foodshippers.foodship.ownedFood;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import de.foodshippers.foodship.R;

import java.util.ArrayList;

/**
 * Created by hannes on 15.11.16.
 */
public class FoodViewFragment extends Fragment {

    GridViewAdapter gridAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_view_fragment, parent, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this.getActivity(), R.layout.grid_item_layout, new ArrayList());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position + v + "Hannes" + id,
                        Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}
