package de.foodshippers.foodship.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by soenke on 11.11.16.
 */
public class FoodshipDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Foodship.db";

    public FoodshipDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FoodshipContract.ProductTable.SQL_CREATE);
        db.execSQL(FoodshipContract.ProductTypeTable.SQL_CREATE);
        db.execSQL(FoodshipContract.GroupTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3 && newVersion >= 3) {
            db.execSQL(FoodshipContract.GroupTable.SQL_CREATE);
        } else if (oldVersion < 4 && newVersion >= 4) {
            db.execSQL("ALTER TABLE groups ADD COLUMN " + FoodshipContract.GroupTable.CN_SELF_ACCEPTED + " INTEGER");
        } else if (oldVersion < 5 && newVersion >= 5) {
            db.execSQL(FoodshipContract.RecipeTable.SQL_CREATE);
        } else if (oldVersion < 6 && newVersion >= 6) {
            db.execSQL("ALTER TABLE recipes ADD COLUMN " + FoodshipContract.RecipeTable.CN_GROUP + " INTEGER");
        } else {
            dropAll(db);
            onCreate(db);
        }
    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL(FoodshipContract.ProductTable.SQL_DELETE);
        db.execSQL(FoodshipContract.ProductTypeTable.SQL_DELETE);
        db.execSQL(FoodshipContract.GroupTable.SQL_DELETE);
        db.execSQL(FoodshipContract.RecipeTable.SQL_DELETE);
    }
}
