package de.foodshippers.foodship.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import de.foodshippers.foodship.FoodFragment.FoodViewDataBase;
import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;

/**
 * Created by hannes on 29.11.16.
 */
public class SendProductTypeJob extends Job {
    private final String ean;
    private final int type;
    private static final String TAG = SendProductTypeJob.class.getSimpleName();

    public SendProductTypeJob(String ean, int type) {
        super(new Params(0).setPersistent(true).requireNetwork());
        this.ean = ean;
        this.type = type;
    }


    @Override
    public void onAdded() {
        Log.d(TAG, "ADDED");
        //Hack
       FoodViewDataBase.getInstance(getApplicationContext()).addFood(new Product("",ean,type));
    }

    @Override
    public void onRun() throws Throwable {
        Call call = RestClient.getInstance().getProductService().addProduct(this.ean, "", this.type);
        retrofit2.Response response = call.execute();
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() < 500) {
                Log.d(TAG, "onRun: Server returned invalid arguments or similar: " + response.code());
            } else if (response.code() >= 500 && response.code() < 600) {
                throw new ServerErrorThrowable(response.code(), null);
            }
        } else {
            Log.d(TAG, "onRun: Call was successfull!");
            FoodshipJobManager.getInstance(getApplicationContext()).addJobInBackground(new AddUserFoodJob(new Product("", ean, type)));
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        if (throwable instanceof ServerErrorThrowable) {
            if (((ServerErrorThrowable) throwable).getResponseCode() >= 500 && ((ServerErrorThrowable) throwable).getResponseCode() < 600) {
                return RetryConstraint.createExponentialBackoff(runCount, 5000);
            }
        }
        return RetryConstraint.RETRY;
    }
}
