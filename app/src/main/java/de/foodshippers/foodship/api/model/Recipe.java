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
    @SerializedName("cheap")
    boolean cheap;
    @SerializedName("vegan")
    boolean vegan;
    @SerializedName("vegetarian")
    boolean vegetarian;

    public Recipe(String image, String desc, String title, int upvotes, boolean veto, int id, boolean cheap, boolean vegan, boolean vegetarian) {
        this.image = image;
        this.desc = desc;
        this.title = title;
        this.upvotes = upvotes;
        this.veto = veto;
        this.id = id;
        this.cheap = cheap;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
    }


    public boolean isVeto() {
        return veto;
    }

    public boolean isCheap() {
        return cheap;
    }

    public boolean isVegan() {
        return vegan;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

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
