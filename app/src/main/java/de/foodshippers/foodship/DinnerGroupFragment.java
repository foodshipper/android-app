package de.foodshippers.foodship;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import de.foodshippers.foodship.Bilder.AbstractImageManager;
import de.foodshippers.foodship.Bilder.GroupImageManager;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.jobs.GetGroupInformationJob;
import de.foodshippers.foodship.api.jobs.GetGroupRecipes;
import de.foodshippers.foodship.api.jobs.RecipeVoteJob;
import de.foodshippers.foodship.api.model.GroupInformation;
import de.foodshippers.foodship.api.model.Recipe;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipContract.RecipeTable;
import de.foodshippers.foodship.db.FoodshipDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static de.foodshippers.foodship.db.FoodshipContract.RecipeTable.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DinnerGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DinnerGroupFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String GROUP_ID = "groupID";
    private static final String TAG = "DinnerGroupFragment";
    private int groupID = 0;
    private RecipeAdapter adapter;

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

    static DinnerGroupFragment newInstance() {
        DinnerGroupFragment fragment = new DinnerGroupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RecipeAdapter();
        if (getArguments() != null) {
            changeGroupID(getArguments().getInt(GROUP_ID));
        } else {
            new FindLatestGroupTask().execute();
        }
        new GetGroupInfoTask().execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dinner_group, container, false);

        RecyclerView recipeList = (RecyclerView) v.findViewById(R.id.recipeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recipeList.setLayoutManager(mLayoutManager);

        recipeList.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new GetGroupInformationJob(groupID, getActivity()));
                FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new GetGroupRecipes(groupID, getActivity()));
                swipeRefreshLayout.setRefreshing(false);
                changeGroupID(groupID);
            }
        });
        return v;
    }

    public void changeGroupID(int groupID) {
        Log.d(TAG, "changeGroupID: Got new Group ID: " + groupID);
        this.groupID = groupID;
        new GetGroupInfoTask().execute();
        adapter.reloadData();

    }

    public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
        private static final String TAG = "RecipeAdapter";
        private List<Recipe> dataSource = new LinkedList<>();
        private AbstractImageManager imagman;

        RecipeAdapter() {
            super();
            imagman = GroupImageManager.getInstance(getActivity().getApplicationContext());
        }

        void reloadData() {
            new LoadGroupRecipesTask().execute();
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
         * . Since it will be re-used to display
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
         * Override  instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Recipe recipe = dataSource.get(position);
            holder.recipeBody.setText("It's a very tasty dish: " + recipe.getTitle());
            holder.recipeTitle.setText(recipe.getTitle());
            holder.recipeImage.setLocalImageBitmap(imagman.loadImage(recipe.getImage()));
            holder.vetoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new RecipeVoteJob(groupID, recipe.getId(), "veto"));
                }
            });


            holder.upvoteBtn.setEnabled(!recipe.getVeto());
            if (recipe.getVeto()) {
                holder.upvoteBtn.setColorFilter(Color.GRAY);
            } else {
                holder.upvoteBtn.setColorFilter(null);
            }

            holder.upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new RecipeVoteJob(groupID, recipe.getId(), "upvote"));
                }
            });
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
            final ImageButton upvoteBtn;
            final ImageButton vetoBtn;
            final CustomNetworkImageView recipeImage;

            ViewHolder(View itemView) {
                super(itemView);

                upvoteBtn = (ImageButton) itemView.findViewById(R.id.upvoteBtn);
                vetoBtn = (ImageButton) itemView.findViewById(R.id.vetoBtn);
                recipeTitle = (TextView) itemView.findViewById(R.id.recipeTitle);
                recipeBody = (TextView) itemView.findViewById(R.id.recipeBody);
                recipeImage = (CustomNetworkImageView) itemView.findViewById(R.id.recipeImage);
            }
        }


        private class LoadGroupRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {
            private static final String TAG = "LoadGroupRecipesTask";

            @Override
            protected List<Recipe> doInBackground(Void... voids) {

                SQLiteDatabase db = new FoodshipDbHelper(getActivity()).getReadableDatabase();
                Cursor cursor = db.query(RecipeTable.TABLE_NAME,
                        new String[]{RecipeTable.CN_ID, RecipeTable.CN_IMG, RecipeTable.CN_DESC, RecipeTable.CN_TITLE, RecipeTable.CN_UPVOTES, RecipeTable.CN_VETO, CN_CHEAP, CN_VEGAN, CN_VEGETARIAN},
                        RecipeTable.CN_GROUP + "=?",
                        new String[]{String.valueOf(groupID)},
                        null,
                        null,
                        RecipeTable.CN_UPVOTES + " DESC"
                );
                List<Recipe> recipes = new LinkedList<>();
                while (cursor.moveToNext()) {
                    recipes.add(new Recipe(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                            cursor.getInt(5) == 1, cursor.getInt(0), cursor.getInt(6) == 1, cursor.getInt(7) == 1, cursor.getInt(8) == 1));
                }
                cursor.close();

                return recipes;
            }

            @Override
            protected void onPostExecute(List<Recipe> recipes) {
                dataSource = recipes;
                Log.d(TAG, "onPostExecute: Fetched " + recipes.size() + " recipes");
                RecipeAdapter.this.notifyDataSetChanged();
            }
        }
    }

    private class GetGroupInfoTask extends AsyncTask<Void, Void, GroupInformation> {
        private static final String TAG = "GetGroupInfoTask";
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

    private class FindLatestGroupTask extends AsyncTask<Void, Void, Integer> {
        private static final String TAG = "FindLatestGroupTask";

        @Override
        protected Integer doInBackground(Void... voids) {
            SQLiteDatabase db = new FoodshipDbHelper(getActivity()).getReadableDatabase();
            Cursor cursor = db.query(FoodshipContract.GroupTable.TABLE_NAME,
                    new String[]{FoodshipContract.GroupTable.CN_ID},
                    null,
                    null,
                    null,
                    null,
                    FoodshipContract.GroupTable.CN_DAY + " DESC",
                    "0,1");
            int id = 0;
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            } else {
                Log.i(TAG, "doInBackground: No Group Information found in database");
            }
            cursor.close();

            return id;
        }

        @Override
        protected void onPostExecute(Integer groupId) {
            DinnerGroupFragment.this.changeGroupID(groupId);
        }
    }
}
