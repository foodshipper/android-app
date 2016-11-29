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
    private String type;

    @SerializedName("ean")
    private String ean;

    @SerializedName("name")
    private String name;

    public Product() {

    }

    public Product(String name, String ean, String type) {
        setEan(ean);
        setName(name);
        setType(type);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
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
