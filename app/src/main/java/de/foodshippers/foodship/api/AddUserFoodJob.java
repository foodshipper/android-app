package de.foodshippers.foodship.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.TagConstraint;
import de.foodshippers.foodship.CommunicationManager;
import de.foodshippers.foodship.FoodFragment.FoodViewDataBase;
import de.foodshippers.foodship.api.model.Product;
import retrofit2.Call;

/**
 * Created by hannes on 29.11.16.
 */
public class AddUserFoodJob extends Job {

    private final Product p;
    private static final String TAG = AddUserFoodJob.class.getSimpleName();
    private boolean unique;

    public AddUserFoodJob(Product p) {
        super(new Params(0).setPersistent(true).requireNetwork().addTags("ADD-".concat(p.getEan())));
        this.p = p;
    }


    @Override
    public void onAdded() {
        Log.d(TAG, "ADDED");
        FoodshipJobManager.getInstance(getApplicationContext()).cancelJobsInBackground(null, TagConstraint.ANY, "DELETE-".concat(p.getEan()));
        this.unique = FoodViewDataBase.getInstance(getApplicationContext()).addFood(p);
    }

    @Override
    public void onRun() throws Throwable {
        Call call = RestClient.getInstance().getFridgeService().addItem(p.getEan(), CommunicationManager.getUserId(getApplicationContext()));
        retrofit2.Response response = call.execute();
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() < 500) {
                Log.d(TAG, "onRun: Server returned invalid arguments or similar: " + response.code());
            } else if (response.code() >= 500 && response.code() < 600) {
                throw new ServerErrorThrowable(response.code(), null);
            }
        } else {
            Log.d(TAG, "onRun: Call was successfull!");
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "Ich sterbe :| ".concat(p.getEan()));
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
