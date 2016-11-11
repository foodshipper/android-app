package de.foodshippers.foodship.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by soenke on 11.11.16.
 */
public class FoodshipDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Foodship.db";

    public FoodshipDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FoodshipContract.ProductTable.SQL_CREATE);
        db.execSQL(FoodshipContract.ProductTypeTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FoodshipContract.ProductTable.SQL_DELETE);
        db.execSQL(FoodshipContract.ProductTypeTable.SQL_DELETE);
        onCreate(db);
    }
}
