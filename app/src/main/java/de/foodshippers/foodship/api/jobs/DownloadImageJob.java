package de.foodshippers.foodship.api.jobs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import de.foodshippers.foodship.FoodFragment.ImageManager;
import de.foodshippers.foodship.api.ServerErrorThrowable;
import de.foodshippers.foodship.api.model.Type;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by hannes on 05.12.16.
 */
public class DownloadImageJob extends Job {

    private final Type typep;

//    Problem:
//    Was passiert wenn hier kein Internet ist?
//    Der Job bricht womöglich ab und wird nie wieder gestartet


    public DownloadImageJob(Type p) {
        super(new Params(0).requireNetwork().requireNetwork());
        this.typep = p;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        ImageManager.getInstance(getApplicationContext()).saveToInternalStorage(typep, getBitmapFromURL(typep.getImageUrl()));

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
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