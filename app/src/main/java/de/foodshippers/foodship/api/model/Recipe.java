package de.foodshippers.foodship.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hannes on 06.12.16.
 */
public class Recipe {

    @SerializedName("image")
    int image;
    @SerializedName("desc")
    int desc;
    @SerializedName("title")
    int title;
    @SerializedName("upvotes")
    int upvotes;
    @SerializedName("veto")
    int veto;
    @SerializedName("id")
    int id;

    public int getDesc() {
        return desc;
    }

    public int getId() {
        return id;
    }

    public int getImage() {
        return image;
    }

    public int getTitle() {
        return title;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getVeto() {
        return veto;
    }

}
