package de.foodshippers.foodship.api.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import de.foodshippers.foodship.api.ServerErrorThrowable;
import retrofit2.Call;

/**
 * Created by hannes on 04.12.16.
 */
public abstract class SimpleNetworkJob<T> extends Job {

    protected final String TAG;


    protected SimpleNetworkJob(Params param, Class TagClass) {
        super(param);
        TAG = TagClass.getSimpleName();
    }

    protected SimpleNetworkJob(Class TagClass) {
        this(new Params(0).setRequiresNetwork(true).setPersistent(true), TagClass);
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Call<T> call = getAPICall();
        retrofit2.Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() < 500) {
                Log.d(TAG, "onRun: Server returned invalid arguments or similar: " + response.code());
            } else if (response.code() >= 500 && response.code() < 600) {
                throw new ServerErrorThrowable(response.code(), null);
            }
        } else {
            Log.d(TAG, "onRun: Call was successfull!");
            onSuccessFullRun(response.body());
        }
    }

    protected abstract Call<T> getAPICall();

    protected void onSuccessFullRun(T result) {

    }

    protected void onSuccessFullRun() {

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "Ich sterbe :| ");
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
