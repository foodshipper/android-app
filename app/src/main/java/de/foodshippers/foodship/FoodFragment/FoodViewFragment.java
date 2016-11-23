package de.foodshippers.foodship.FoodFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import de.foodshippers.foodship.R;
import de.foodshippers.foodship.api.model.Product;

import java.util.ArrayList;

/**
 * Created by hannes on 15.11.16.
 */
public class FoodViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private GridViewAdapter gridAdapter;
    private SwipeRefreshLayout swipeLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_view, parent, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this.getActivity(), R.layout.grid_item_layout, new ArrayList());
        gridView.setAdapter(gridAdapter);
        System.out.println("Schonwieder");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Object itemAtPosition = parent.getItemAtPosition(position);
                if (itemAtPosition != null) {
                    FoodViewReFresher.getInstance(getActivity()).deleteFood((Product) itemAtPosition);
                }
//                Toast.makeText(getActivity(), "" + position + v.findViewById(id) + "Hannes" + id,
//                        Toast.LENGTH_SHORT).show();
//                showDialog();
            }

        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public void onRefresh() {
        System.out.println("Refresh");
        FoodViewReFresher.getInstance(getActivity()).refreshFood();
        swipeLayout.setRefreshing(false);
    }


}
