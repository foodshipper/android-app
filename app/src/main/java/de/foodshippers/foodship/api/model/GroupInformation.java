package de.foodshippers.foodship.api.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.annotations.SerializedName;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;

import java.io.Serializable;

import static de.foodshippers.foodship.db.FoodshipContract.GroupTable.*;

/**
 * Created by hannes on 06.12.16.
 */
public class GroupInformation implements Serializable {
    @SerializedName("id")
    int id;
    @SerializedName("invited")
    int invited;
    @SerializedName("accepted")
    int accepted;
    @SerializedName("day")
    String day;

    public GroupInformation(int id, int invited, int accepted, String day) {
        this.id = id;
        this.invited = invited;
        this.accepted = accepted;
        this.day = day;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getInvited() {
        return invited;
    }

    public String getDay() {
        return day;
    }

    public static GroupInformation getGroupInfoFromId(Context context, int id) {
        SQLiteDatabase db = new FoodshipDbHelper(context).getReadableDatabase();
        Cursor c = db.query(FoodshipContract.GroupTable.TABLE_NAME,
                new String[]{CN_ID, CN_ACCEPTED, CN_INVITED, CN_DAY},
                CN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        GroupInformation result = null;
        if (c.moveToFirst()) {
            result = new GroupInformation(c.getInt(c.getColumnIndex(CN_ID)),
                    c.getInt(c.getColumnIndex(CN_INVITED)),
                    c.getInt(c.getColumnIndex(CN_ACCEPTED)),
                    c.getString(c.getColumnIndex(CN_DAY)));

        }
        c.close();
        db.close();
        return result;
    }

}
