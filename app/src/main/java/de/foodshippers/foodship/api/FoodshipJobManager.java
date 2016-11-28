package de.foodshippers.foodship.api;

import android.content.Context;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

/**
 * Created by soenke on 21.11.16.
 */
public class FoodshipJobManager extends JobManager {
    private static final String TAG = FoodshipJobManager.class.getSimpleName();
    private static FoodshipJobManager mManager = null;

    /**
     * Creates a JobManager with the given configuration
     *
     * @param c The context to be used for the JobManager
     * @see Configuration.Builder
     */
    private FoodshipJobManager(Context c) {
        super(new Configuration.Builder(c).build());

    }

    public static FoodshipJobManager getInstance(Context c) {
        if (mManager == null) {
            mManager = new FoodshipJobManager(c);
        }
        return mManager;
    }
}
   