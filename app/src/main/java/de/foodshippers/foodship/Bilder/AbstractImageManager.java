package de.foodshippers.foodship.Bilder;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.jobs.DownloadImageJob;
import de.foodshippers.foodship.api.model.Type;

import java.io.*;

/**
 * Created by hannes on 05.12.16.
 */
public abstract class AbstractImageManager {

    private final Context AppContext;
    private final File ordner;


    public AbstractImageManager(Context c, String Ordner) {
        this.AppContext = c;
        ContextWrapper cw = new ContextWrapper(AppContext);
        this.ordner = cw.getDir(Ordner, Context.MODE_PRIVATE);
    }

    public void downloadifNeeded(String url) {
        if (url == null || url.equals("")) {
            return;
        }
        File mypath = new File(this.ordner, getFileName(url));
        if (!mypath.exists()) {
            togglNewJob(url);
        }
    }

    private void togglNewJob(String url) {
        FoodshipJobManager.getInstance(AppContext)
                .addJobInBackground(new DownloadImageJob(url));
    }

    public void saveToInternalStorage(String url, Bitmap bitmapImage) {
        if (url == null || url.equals("")) {
            return;
        }
        ContextWrapper cw = new ContextWrapper(AppContext);
        // path to /data/data/yourapp/app_data/imageDir
        File mypath = new File(this.ordner, getFileName(url));

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
    }

    public Bitmap loadImageFromStorage(String url) {
        if (url == null || url.equals("")) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            return bmp;
        }
        try {
            File f = new File(this.ordner, getFileName(url));
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
        } catch (Exception ex) {
        }
        togglNewJob(url);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
        return bmp;

    }

    private String getFileName(Type File) {
        return getFileName(File.getImageUrl());
    }

    private String getFileName(String File) {
        String fileName = File.substring(File.lastIndexOf('/') + 1, File.length());
        return fileName;
    }
}
