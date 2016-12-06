package de.foodshippers.foodship.FoodFragment;

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
public class ImageManager {

    private static ImageManager instance;
    private final Context AppContext;
    private final File ordner;

    public static ImageManager getInstance(Context c) {
        if (instance == null) {
            synchronized (ImageManager.class) {
                if (instance == null) {
                    instance = new ImageManager(c);
                }
            }
        }
        return instance;
    }

    public ImageManager(Context c) {
        this.AppContext = c;
        ContextWrapper cw = new ContextWrapper(AppContext);
        this.ordner = cw.getDir("foodpics", Context.MODE_PRIVATE);
    }

    public void downloadifNeeded(Type p) {
        if (p.getImageUrl() == null || p.getImageUrl().equals("")) {
            return;
        }
        System.out.println(p.toString());
        File mypath = new File(this.ordner, getFileName(p));
        if (!mypath.exists()) {
            FoodshipJobManager.getInstance(AppContext)
                    .addJobInBackground(new DownloadImageJob(p));
        }
    }

    public void saveToInternalStorage(Type p, Bitmap bitmapImage) {
        if (p.getImageUrl() == null || p.getImageUrl().equals("")) {
            return;
        }
        ContextWrapper cw = new ContextWrapper(AppContext);
        // path to /data/data/yourapp/app_data/imageDir
        File mypath = new File(this.ordner, getFileName(p));

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

    public Bitmap loadImageFromStorage(Type p) {
        if (p.getImageUrl() == null || p.getImageUrl().equals("")) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            return bmp;
        }
        try {
            File f = new File(this.ordner, getFileName(p));
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
        } catch (Exception ex) {
        } finally {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            return bmp;
        }
    }

    private String getFileName(Type File) {
        String fileName = File.getImageUrl().substring(File.getImageUrl().lastIndexOf('/') + 1, File.getImageUrl().length());
        return fileName;
    }
}
