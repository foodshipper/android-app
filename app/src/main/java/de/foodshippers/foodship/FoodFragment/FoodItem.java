package de.foodshippers.foodship.FoodFragment;

import android.graphics.Bitmap;
import de.foodshippers.foodship.api.model.Product;

/**
 * Created by hannes on 09.11.16.
 */
public class FoodItem {
    private Bitmap image;
    private Product p;

    public FoodItem(Bitmap image, Product p) {
        super();
        this.image = image;
        this.p = p;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return p.getType();
    }

    public void setP(Product p) {
        this.p = p;
    }
}