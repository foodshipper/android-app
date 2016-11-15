package de.foodshippers.foodship.ownedFood;

import android.graphics.Bitmap;

/**
 * Created by hannes on 09.11.16.
 */
public class FoodItem {
    private Bitmap image;
    private String title;

    public FoodItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}