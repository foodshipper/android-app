package de.foodshippers.foodship.Bilder;

import android.content.Context;
import android.graphics.Bitmap;
import de.foodshippers.foodship.CacheMap;

/**
 * Created by hannes on 07.12.16.
 * Extention of the {@link AbstractImageManager} with a primitive Caching
 */
public abstract class CachedImageManager extends AbstractImageManager {

    private final CacheMap cache;


    /**
     * @param c
     * @param Ordner     Folder to save the images to
     * @param BufferSize the Size of the Cache
     */
    public CachedImageManager(Context c, String Ordner, int BufferSize) {
        super(c, Ordner);
        this.cache = new CacheMap(BufferSize);

    }

    /**
     * saves the Bitmap to interal storage and puts it in the cache     *
     *
     * @param url         the url to the image, needed to generate the filename
     * @param bitmapImage the bitmap to save
     */
    @Override
    public void saveToInternalStorage(String url, Bitmap bitmapImage) {
        super.saveToInternalStorage(url, bitmapImage);
        cache.put(getFileName(url), bitmapImage);
    }

    /**
     * loads the image from cache if possible else it will be loaded from internal storage
     *
     * @param url url to the file
     * @return
     */
    @Override
    public Bitmap loadImage(String url) {
        String filename = getFileName(url);
        Object o = cache.get(filename);
        if (o == null) {
            Bitmap storage = super.loadImageFromStorage(url);
            if (storage != null) {
                cache.put(filename, storage);
            }
            return storage;
        } else {
            return (Bitmap) o;
        }
    }

    /**
     * clears all images from internal storage and from cache
     */
    @Override
    public void clear() {
        super.clear();
        cache.clear();
    }
}
