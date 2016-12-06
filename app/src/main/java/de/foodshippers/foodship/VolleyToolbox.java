package de.foodshippers.foodship;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by soenke on 28.04.16.
 */
public class VolleyToolbox {
    private static VolleyToolbox mInstance;
    private static RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;
    private static Context mCtx;

    private VolleyToolbox(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue(context);

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleyToolbox getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyToolbox(context);
        }
        return mInstance;
    }

    public static RequestQueue getRequestQueue(Context ctx) {
        mCtx = ctx;
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}