package de.foodshippers.foodship.api.jobs;

import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import de.foodshippers.foodship.FoodFragment.FoodViewDataBase;
import de.foodshippers.foodship.Utils;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;

/**
 * Created by hannes on 29.11.16.
 */
public class DeleteUserFoodJobSimple extends SimpleNetworkJob {


    private final Product p;

    public DeleteUserFoodJobSimple(Product p) {
        super(new Params(0).setPersistent(true).requireNetwork().addTags("DELETE-".concat(p.getEan())), DeleteUserFoodJobSimple.class);
        this.p = p;
    }


    @Override
    public void onAdded() {
        Log.d(TAG, "Deleted");
        FoodshipJobManager.getInstance(getApplicationContext()).cancelJobsInBackground(null, TagConstraint.ANY, "ADD-".concat(p.getEan()));
        FoodshipJobManager.getInstance(getApplicationContext()).cancelJobsInBackground(null, TagConstraint.ANY, "TYPE-".concat(p.getEan()));
        FoodViewDataBase.getInstance(getApplicationContext()).deleteFood(p);
    }


    @Override
    protected Call getAPICall() {
        return RestClient.getInstance().getFridgeService().removeItem(p.getEan(), Utils.getUserId(getApplicationContext()));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "Ich sterbe :| ".concat(p.getEan()));
    }

    @Override
    protected void onSuccessFullRun() {
        FoodViewDataBase.getInstance(getApplicationContext()).refreshFood();
    }
}
