package de.foodshippers.foodship.api.model;

import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by soenke on 21.11.16.
 */
@Parcel
public class Product implements Serializable{

    private static final String TAG = Product.class.getSimpleName();

    @SerializedName("type")
    int type;

    @SerializedName("ean")
    String ean;

    @SerializedName("name")
    String name;

    public Product() {

    }

    public Product(String name, String ean, int type) {
        setEan(ean);
        setName(name);
        setType(type);
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Type: " + type + " Ean: " + ean;
    }
}
