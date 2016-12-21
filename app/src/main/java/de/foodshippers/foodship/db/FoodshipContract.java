package de.foodshippers.foodship.db;

import android.provider.BaseColumns;

/**
 * Created by soenke on 11.11.16.
 */
public final class FoodshipContract {

    private FoodshipContract() {
    }

    public static final class ProductTable implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String CN_EAN = "ean";
        public static final String CN_TYPE = "type";
        public static final String CN_PUSHED = "pushed";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                ProductTable._ID + " INTEGER PRIMARY KEY," +
                ProductTable.CN_EAN + " TEXT," +
                ProductTable.CN_TYPE + " TEXT," +
                ProductTable.CN_PUSHED + " INTEGER)";

        public static final String SQL_DELETE = "DROP TABLE " + TABLE_NAME;
    }

    public static final class GroupTable implements BaseColumns {
        public static final String TABLE_NAME = "groups";
        public static final String CN_ID = "id";
        public static final String CN_INVITED = "invited";
        public static final String CN_ACCEPTED = "accepted";
        public static final String CN_SELF_ACCEPTED = "self_accepted";
        public static final String CN_DAY = "day";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                GroupTable._ID + " INTEGER PRIMARY KEY," +
                GroupTable.CN_ID + " INTEGER," +
                GroupTable.CN_INVITED + " INTEGER," +
                GroupTable.CN_ACCEPTED + " INTEGER," +
                GroupTable.CN_SELF_ACCEPTED + " INTEGER," +
                GroupTable.CN_DAY + " DATE)";

        public static final String SQL_DELETE = "DROP TABLE " + TABLE_NAME;
    }

    public static final class RecipeTable implements BaseColumns {
        public static final String TABLE_NAME = "recipes";
        public static final String CN_ID = "id";
        public static final String CN_IMG = "img";
        public static final String CN_DESC = "desc";
        public static final String CN_TITLE = "title";
        public static final String CN_UPVOTES = "upvotes";
        public static final String CN_VETO = "veto";
        public static final String CN_GROUP = "group_id";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                RecipeTable._ID + " INTEGER PRIMARY KEY," +
                RecipeTable.CN_ID + " INTEGER," +
                RecipeTable.CN_IMG + " TEXT," +
                RecipeTable.CN_DESC + " TEXT," +
                RecipeTable.CN_TITLE + " TEXT," +
                RecipeTable.CN_UPVOTES + " INTEGER," +
                RecipeTable.CN_VETO + " INTEGER," +
                RecipeTable.CN_GROUP + " INTEGER)";

        public static final String SQL_DELETE = "DROP TABLE " + TABLE_NAME;

    }

    public static final class ProductTypeTable implements BaseColumns {
        public static final String TABLE_NAME = "product_types";
        public static final String CN_NAME = "name";
        public static final String CN_ID = "id";
        public static final String CN_CATEGORY = "category";
        public static final String CN_IMAGEURL = "imageurl";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                ProductTypeTable._ID + " INTEGER PRIMARY KEY," +
                ProductTypeTable.CN_ID + " INTEGER," +
                ProductTypeTable.CN_NAME + " TEXT," +
                ProductTypeTable.CN_CATEGORY + " TEXT," +
                ProductTypeTable.CN_IMAGEURL + " TEXT" +
                ")";

        public static final String SQL_DELETE = "DROP TABLE " + TABLE_NAME;

    }

}
