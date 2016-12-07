package de.foodshippers.foodship.Bilder;

import android.content.Context;
import android.graphics.Bitmap;
import de.foodshippers.foodship.CacheMap;

/**
 * Created by hannes on 07.12.16.
 */
public abstract class CachedImageManager extends AbstractImageManager {

    private final CacheMap cache;


    public CachedImageManager(Context c, String Ordner, int BufferSize) {
        super(c, Ordner);
        this.cache = new CacheMap(BufferSize);

    }

    @Override
    public void saveToInternalStorage(String url, Bitmap bitmapImage) {
        super.saveToInternalStorage(url, bitmapImage);
        cache.put(getFileName(url), bitmapImage);
    }

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
}
