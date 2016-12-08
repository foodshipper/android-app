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

    private final Context appContext;
    private final File imageFolder;
    private final Bitmap whiteBitMap;


    /**
     * @param c
     * @param Ordner Name of the Folder to Save the Images
     */
    public AbstractImageManager(Context c, String Ordner) {
        this.appContext = c;
        ContextWrapper cw = new ContextWrapper(appContext);
        this.imageFolder = cw.getDir(Ordner, Context.MODE_PRIVATE);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        this.whiteBitMap = Bitmap.createBitmap(100, 100, conf);
    }

    /**
     * Checks if the Image is already persisted, if not a {@link DownloadImageJob} with the provided Url is created.
     *
     * @param url url to the image to download
     * @return returns true when image gets downloaded
     */
    public boolean downloadImage(String url) {
        if (url == null || url.equals("")) {
            return false;
        }
        File mypath = new File(this.imageFolder, getFileName(url));
        if (!mypath.exists()) {
            togglNewJob(url);
            return true;
        }
        return false;
    }

    private void togglNewJob(String url) {
        FoodshipJobManager.getInstance(appContext)
                .addJobInBackground(new DownloadImageJob(url));
    }

    /**
     * Saves the given Bitmap in internal storage
     *
     * @param url         the url to the image, needed to generate the filename
     * @param bitmapImage the bitmap to save
     */
    public void saveToInternalStorage(String url, Bitmap bitmapImage) {
        if (url == null || url.equals("")) {
            return;
        }
        ContextWrapper cw = new ContextWrapper(appContext);
        // path to /data/data/yourapp/app_data/imageDir
        File mypath = new File(this.imageFolder, getFileName(url));

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

    /**
     * Loads the Image referenced with the url from internal storage
     *
     * @param url url to the file
     * @return returns the image to the url, if there is no image a white bitmap gets returned
     */
    public Bitmap loadImage(String url) {
        if (url == null || url.equals("")) {
            return whiteBitMap;
        }
        Bitmap bitmap = loadImageFromStorage(url);
        if (bitmap == null) {
            return whiteBitMap;
        }
        return bitmap;
    }

    protected Bitmap loadImageFromStorage(String url) {
        try {
            File f = new File(this.imageFolder, getFileName(url));
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
        } catch (Exception ex) {
        }
        togglNewJob(url);
        return null;
    }

    protected String getFileName(Type File) {
        return getFileName(File.getImageUrl());
    }

    protected String getFileName(String File) {
        String fileName = File.substring(File.lastIndexOf('/') + 1, File.length());
        return fileName;
    }

    /**
     * Delets all Images from internal Storage
     */
    public void clear() {
        for (File f : imageFolder.listFiles()) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }
}
