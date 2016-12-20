package de.foodshippers.foodship.api.jobs;

import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Params;
import de.foodshippers.foodship.Utils;
import de.foodshippers.foodship.api.RestClient;
import retrofit2.Call;

/**
 * Created by hannes on 29.11.16.
 */
public class RecipeVoteJob extends SimpleNetworkJob<Void> {

    private int groupId;
    private int recipe_id;
    private String action;

    public RecipeVoteJob(int groupId, int recipe_id, String action) {
        super(new Params(0).setPersistent(true).requireNetwork(), RecipeVoteJob.class);
        this.groupId = groupId;
        this.action = action;
        this.recipe_id = recipe_id;
    }


    @Override
    protected Call<Void> getAPICall() {
        return RestClient.getInstance().getDinnerService().voteRecipe(groupId, Utils.getUserId(getApplicationContext()), recipe_id, action);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel: Job was cancelled");
    }

    @Override
    protected void onSuccessFullRun() {
        Log.d(TAG, "onSuccessFullRun: Responded to Dinner Invitation");
    }
}
