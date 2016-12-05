package de.foodshippers.foodship.api.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.annotations.SerializedName;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;

import java.io.Serializable;

import static de.foodshippers.foodship.db.FoodshipContract.ProductTypeTable.*;

/**
 * Created by soenke on 04.12.16.
 */
public class Type implements Serializable{
    int id;
    String name;
    String category;
    @SerializedName("image")
    String imageUrl;

    public Type(int id, String name, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Type getTypeFromName(Context context, String name) {
        SQLiteDatabase db = new FoodshipDbHelper(context).getReadableDatabase();
        Cursor c = db.query(FoodshipContract.ProductTypeTable.TABLE_NAME,
                new String[]{CN_ID, CN_NAME, CN_CATEGORY, CN_IMAGEURL},
                CN_NAME + " = ?",
                new String[]{name},
                null,
                null,
                null);
        if (!c.moveToFirst()) {
            return null;
        } else {
            return new Type(c.getInt(c.getColumnIndex(CN_ID)),
                    c.getString(c.getColumnIndex(CN_NAME)),
                    c.getString(c.getColumnIndex(CN_CATEGORY)),
                    c.getString(c.getColumnIndex(CN_IMAGEURL)));
        }
    }

    @Override
    public String toString() {
        return " id:" + id + " name: " + name + " cat: " + category + " url: " + imageUrl;
    }

    public static Type getTypeFromId(Context context, int id) {
        SQLiteDatabase db = new FoodshipDbHelper(context).getReadableDatabase();
        Cursor c = db.query(FoodshipContract.ProductTypeTable.TABLE_NAME,
                new String[]{CN_ID, CN_NAME, CN_CATEGORY, CN_IMAGEURL},
                CN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if (!c.moveToFirst()) {
            return null;
        } else {
            return new Type(c.getInt(c.getColumnIndex(CN_ID)),
                    c.getString(c.getColumnIndex(CN_NAME)),
                    c.getString(c.getColumnIndex(CN_CATEGORY)),
                    c.getString(c.getColumnIndex(CN_IMAGEURL)));
        }
    }
}
