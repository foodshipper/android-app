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
public class DinnerResponseJob extends SimpleNetworkJob<Void> {

    private int groupId;
    private boolean accept;

    public DinnerResponseJob(int groupId, boolean accept) {
        super(new Params(0).setPersistent(true).requireNetwork(), DinnerResponseJob.class);
        this.groupId = groupId;
        this.accept = accept;
    }


    @Override
    protected Call<Void> getAPICall() {
        return RestClient.getInstance().getDinnerService().acceptInvite(groupId, Utils.getUserId(getApplicationContext()), accept);
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
