package de.foodshippers.foodship.Bilder;

import android.content.Context;

/**
 * Created by hannes on 05.12.16.
 */
public class FoodImageManager extends AbstractImageManager {

    private static FoodImageManager instance;

    public FoodImageManager(Context c, String Ordner) {
        super(c, Ordner);
    }

    public static FoodImageManager getInstance(Context c) {
        if (instance == null) {
            synchronized (FoodImageManager.class) {
                if (instance == null) {
                    instance = new FoodImageManager(c, "foodship");
                }
            }
        }
        return instance;
    }

}
