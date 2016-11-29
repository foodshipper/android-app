package de.foodshippers.foodship.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import retrofit2.Call;

/**
 * Created by hannes on 29.11.16.
 */
public class SendProductTypeJob extends Job {
    private final String ean;
    private final String type;
    private static final String TAG = AddUserFoodJob.class.getSimpleName();

    public SendProductTypeJob(String ean, String Type) {
        super(new Params(0).setPersistent(true).requireNetwork());
        this.ean = ean;
        this.type = Type;
    }


    @Override
    public void onAdded() {
        Log.d(TAG, "ADDED");
        //Add in DATABASE
    }

    @Override
    public void onRun() throws Throwable {
        Call call = RestClient.getInstance().getProductService().addProduct(ean, "", this.type);
        retrofit2.Response response = call.execute();
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() < 500) {
                Log.d(TAG, "onRun: Server returned invalid arguments or similar: " + response.code());
            } else if (response.code() >= 500 && response.code() < 600) {
                throw new ServerErrorThrowable(response.code(), null);
            }
        } else {
            Log.d(TAG, "onRun: Call was successfull!");
            FoodshipJobManager.getInstance(getApplicationContext()).addJobInBackground(new AddUserFoodJob(ean));
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
