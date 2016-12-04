package de.foodshippers.foodship.api.jobs;

import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import de.foodshippers.foodship.FoodFragment.FoodViewDataBase;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;

/**
 * Created by hannes on 29.11.16.
 */
public class SendProductTypeJob extends SimpleNetworkJob {
    private final String ean;
    private final String type;

    public SendProductTypeJob(String ean, String Type) {
        super(new Params(0).setPersistent(true).requireNetwork().addTags("TYPE-".concat(ean)), SendProductTypeJob.class);
        this.ean = ean;
        this.type = Type;
    }


    @Override
    public void onAdded() {
        Log.d(TAG, "ADDED");
        FoodshipJobManager.getInstance(getApplicationContext()).cancelJobsInBackground(null, TagConstraint.ANY, "DELETE-".concat(ean));
        //Hack
        FoodViewDataBase.getInstance(getApplicationContext()).addFood(new Product("", ean, type));
    }


    @Override
    protected Call getAPICall() {
        return RestClient.getInstance().getProductService().addProduct(this.ean, "", this.type);

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "Ich sterbe :| ".concat(ean));
    }

}
