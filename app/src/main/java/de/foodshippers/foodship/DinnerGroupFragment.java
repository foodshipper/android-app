package de.foodshippers.foodship;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DinnerGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DinnerGroupFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GROUP_ID = "groupID";

    private int groupID;


    public DinnerGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DinnerGroupFragment.
     */
    public static DinnerGroupFragment newInstance(int groupId) {
        DinnerGroupFragment fragment = new DinnerGroupFragment();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupID = getArguments().getInt(GROUP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dinner_group, container, false);
    }

}
