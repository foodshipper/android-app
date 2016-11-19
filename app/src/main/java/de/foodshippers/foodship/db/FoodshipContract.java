package de.foodshippers.foodship.db;

import android.provider.BaseColumns;

/**
 * Created by soenke on 11.11.16.
 */
public final class FoodshipContract {

    private FoodshipContract() {}

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

        public static final class ProductTypeTable implements BaseColumns {
            public static final String TABLE_NAME = "product_types";
            public static final String CN_NAME = "name";

            public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                    ProductTypeTable._ID + " INTEGER PRIMARY KEY," +
                    ProductTypeTable.CN_NAME + " TEXT)";

            public static final String SQL_DELETE = "DROP TABLE " + TABLE_NAME;

        }
}