package de.foodshippers.foodship.api.jobs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import de.foodshippers.foodship.Bilder.FoodImageManager;
import de.foodshippers.foodship.Bilder.GroupImageManager;
import de.foodshippers.foodship.api.ServerErrorThrowable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;

/**
 * Created by hannes on 05.12.16.
 */
public class DownloadImageJob extends Job {

    private final String URL;

//    Problem:
//    Was passiert wenn hier kein Internet ist?
//    Der Job bricht womöglich ab und wird nie wieder gestartet


    public DownloadImageJob(String url) {
        super(new Params(0).singleInstanceBy(url).requireNetwork().requireNetwork());
        this.URL = url;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if (URL.contains("recipeImages")) {
            GroupImageManager.getInstance(getApplicationContext()).saveToInternalStorage(URL, getBitmapFromURL(URL));
        } else
            FoodImageManager.getInstance(getApplicationContext()).saveToInternalStorage(URL, getBitmapFromURL(URL));

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    public Bitmap getBitmapFromURL(String src) throws IOException {
        java.net.URL url = new java.net.URL(src);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        return myBitmap;

    }

    @Override
    protected int getRetryLimit() {
        return 10;
    }


    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        if (throwable instanceof ServerErrorThrowable || throwable instanceof SocketException) {
            return RetryConstraint.createExponentialBackoff(1, 5000);
        }
        return RetryConstraint.RETRY;
    }
}
