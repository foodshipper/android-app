package de.foodshippers.foodship.Bilder;

import android.content.Context;

/**
 * Created by hannes on 05.12.16.
 */
public class GroupImageManager extends AbstractImageManager {

    private static GroupImageManager instance;

    public GroupImageManager(Context c, String Ordner) {
        super(c, Ordner);
    }

    public static GroupImageManager getInstance(Context c) {
        if (instance == null) {
            synchronized (GroupImageManager.class) {
                if (instance == null) {
                    instance = new GroupImageManager(c, "grouppics");
                }
            }
        }
        return instance;
    }

}
