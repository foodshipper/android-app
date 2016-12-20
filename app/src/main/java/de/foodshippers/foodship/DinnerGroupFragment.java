package de.foodshippers.foodship;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import de.foodshippers.foodship.Bilder.AbstractImageManager;
import de.foodshippers.foodship.Bilder.GroupImageManager;
import de.foodshippers.foodship.api.model.GroupInformation;
import de.foodshippers.foodship.api.model.Recipe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DinnerGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DinnerGroupFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String GROUP_ID = "groupID";
    private static final String TAG = "DinnerGroupFragment";
    private int groupID;
    private GroupDataController manager;

    public DinnerGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DinnerGroupFragment.
     */
    static DinnerGroupFragment newInstance(int groupId) {
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
        } else {
            throw new IllegalArgumentException("DinnerGroupFragment has to get Group ID as argument");
        }
        new GetGroupInfoTask().execute();

        manager = GroupDataController.getInstance(getActivity().getApplicationContext());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dinner_group, container, false);

        RecyclerView recipeList = (RecyclerView) v.findViewById(R.id.recipeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recipeList.setLayoutManager(mLayoutManager);
        recipeList.setAdapter(new RecipeAdapter());

        recipeList.setAdapter(new RecipeAdapter());
        if (!manager.isInGroup()) {
            ((TextView) v.findViewById(R.id.recipeText)).setText("Aktuell in keiner Gruppe! DummyData");
        }

        return v;
    }

    public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

        private List<Recipe> dataSource = new LinkedList<>();
        private AbstractImageManager imagman;

        RecipeAdapter() {
            super();

            imagman = GroupImageManager.getInstance(getActivity().getApplicationContext());
            if (manager.isInGroup()) {
                dataSource.addAll(manager.getPossibleRecipies());
                this.notifyDataSetChanged();
            }
        }

        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(ViewHolder, int)
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_item, parent, false);

            return new ViewHolder(v);
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p>
         * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Recipe recipe = dataSource.get(position);
            holder.recipeBody.setText("It's a very tasty dish: " + recipe.getTitle());
            holder.recipeTitle.setText(recipe.getTitle());

            holder.recipeImage.setLocalImageBitmap(imagman.loadImage(recipe.getImage()));
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            if (dataSource == null) return 0;
            return dataSource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView recipeTitle;
            final TextView recipeBody;
            final CustomNetworkImageView recipeImage;

            ViewHolder(View itemView) {
                super(itemView);

                recipeTitle = (TextView) itemView.findViewById(R.id.recipeTitle);
                recipeBody = (TextView) itemView.findViewById(R.id.recipeBody);
                recipeImage = (CustomNetworkImageView) itemView.findViewById(R.id.recipeImage);
            }
        }

    }

    private class GetGroupInfoTask extends AsyncTask<Void, Void, GroupInformation> {

        @Override
        protected GroupInformation doInBackground(Void... voids) {
            return GroupInformation.getGroupInfoFromId(getActivity(), groupID);
        }

        @Override
        protected void onPostExecute(GroupInformation groupInformation) {
            View v = getView();
            if (v != null && groupInformation != null) {
                TextView dinnerDay = (TextView) v.findViewById(R.id.dinnerDate);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    dinnerDay.setText(getString(R.string.dinnerTitle, dateFormat.parse(groupInformation.getDay())));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TextView body = (TextView) v.findViewById(R.id.invitationsInfoBody);
                body.setText(getString(R.string.invitationsBody, groupInformation.getAccepted(), groupInformation.getInvited() - groupInformation.getAccepted()));
                TextView title = (TextView) v.findViewById(R.id.invitationsInfoHeadline);
                title.setText(getString(R.string.invitationsTitle, groupInformation.getInvited()));
            } else {
                Log.w(TAG, "onPostExecute: Could not fetch Group Information");
            }
        }
    }
}
