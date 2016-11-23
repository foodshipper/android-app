package de.foodshippers.foodship.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import retrofit2.Call;

/**
 * Created by soenke on 21.11.16.
 */
public class SetUserNameJob extends Job {
    private String name;
    private String token;
    private static final String TAG = "SetUserNameJob";

    public SetUserNameJob(String token, String name) {
        super(new Params(0).setRequiresNetwork(true).setPersistent(true));
        this.name = name;
        this.token = token;
    }

    /**
     * Called when the job is added to disk and committed.
     * This means job will eventually run. This is a good time to update local database and dispatch events.
     * <p>
     * Changes to this class will not be preserved if your job is persistent !!!
     * <p>
     * Also, if your app crashes right after adding the job, {@code onRun} might be called without an {@code onAdded} call
     * <p>
     * Note that this method is called on JobManager's thread and will block any other action so
     * it should be fast and not make any web requests (File IO is OK).
     */
    @Override
    public void onAdded() {

    }

    /**
     * The actual method that should to the work.
     * It should finish w/o any exception. If it throws any exception,
     * {@link #shouldReRunOnThrowable(Throwable, int, int)} will be called to
     * decide either to dismiss the job or re-run it.
     *
     * @throws Throwable Can throw and exception which will mark job run as failed
     */
    @Override
    public void onRun() throws Throwable {
        Call call = RestClient.getInstance().getUserNameService().setUserName(token, name);
        retrofit2.Response response = call.execute();
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() < 500) {
                Log.d(TAG, "onRun: Server returned invalid arguments or similar: " + response.code());
            } else if (response.code() >= 500 && response.code() < 600) {
                Log.d(TAG, "onRun: Server Error: " + response.code());
                throw new ServerErrorThrowable(response.code(), response.message());
            }
        } else {
            Log.d(TAG, "onRun: Call was successfull!");
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    /**
     * If {@code onRun} method throws an exception, this method is called.
     * <p>
     * If you simply want to return retry or cancel, you can use {@link RetryConstraint#RETRY} or
     * {@link RetryConstraint#CANCEL}.
     * <p>
     * You can also use a custom {@link RetryConstraint} where you can change the Job's priority or
     * add a delay until the next run (e.g. exponential back off).
     * <p>
     * Note that changing the Job's priority or adding a delay may alter the original run order of
     * the job. So if the job was added to the queue with other jobs and their execution order is
     * important (e.g. they use the same groupId), you should not change job's priority or add a
     * delay unless you really want to change their execution order.
     *
     * @param throwable   The exception that was thrown from {@link #onRun()}
     * @param runCount    The number of times this job run. Starts from 1.
     * @param maxRunCount The max number of times this job can run. Decided by {@link #getRetryLimit()}
     * @return A {@link RetryConstraint} to decide whether this Job should be tried again or not and
     * if yes, whether we should add a delay or alter its priority. Returning null from this method
     * is equal to returning {@link RetryConstraint#RETRY}.
     */
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
