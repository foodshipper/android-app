package de.foodshippers.foodship.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hannes on 06.12.16.
 */
public class Recipe {

    @SerializedName("image")
    String image;
    @SerializedName("desc")
    String desc;
    @SerializedName("title")
    String title;
    @SerializedName("upvotes")
    int upvotes;
    @SerializedName("veto")
    boolean veto;
    @SerializedName("id")
    int id;

    public String getDesc() {
        return desc;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public boolean getVeto() {
        return veto;
    }

}