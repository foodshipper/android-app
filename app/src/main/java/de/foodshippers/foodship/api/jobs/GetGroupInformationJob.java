package de.foodshippers.foodship.api.jobs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Params;
import de.foodshippers.foodship.Utils;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.GroupInformation;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;

import static de.foodshippers.foodship.db.FoodshipContract.GroupTable.*;

/**
 * Created by hannes on 29.11.16.
 */
public class GetGroupInformationJob extends SimpleNetworkJob<GroupInformation> {

    private GroupInformation groupInformation;
    private int groupId;
    private Context context;

    public GetGroupInformationJob(int groupId, Context context) {
        super(new Params(0).setPersistent(false).requireNetwork(), GetGroupInformationJob.class);
        this.groupId = groupId;
        this.context = context;
    }


    @Override
    protected Call<GroupInformation> getAPICall() {
        return RestClient.getInstance().getDinnerService().getGroupInformation(groupId, Utils.getUserId(getApplicationContext()));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel: Job was cancelled");
    }

    @Override
    protected void onSuccessFullRun(GroupInformation info) {
        Log.d(TAG, "onSuccessFullRun: Got group info of " + info.getDay());
        SQLiteDatabase db = new FoodshipDbHelper(context).getWritableDatabase();
        Cursor c = db.query(FoodshipContract.GroupTable.TABLE_NAME, new String[]{CN_ID}, CN_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
        if (c.moveToFirst()) {
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + CN_ACCEPTED + "=?, " +
                    "" + CN_INVITED + "=? WHERE " + CN_ID + "=?", new String[]{String.valueOf(info.getAccepted()), String.valueOf(info.getInvited()), String.valueOf(groupId)});
            Log.d(TAG, "onSuccessFullRun: Updated Group Information");
        } else {
            db.execSQL("INSERT INTO " + TABLE_NAME + " (" + CN_ID + ", " + CN_INVITED + ", " + CN_ACCEPTED + ", " + CN_DAY + ") VALUES (?, ?, ?, ?)",
                    new String[]{String.valueOf(groupId), String.valueOf(info.getInvited()), String.valueOf(info.getAccepted()), info.getDay()});
            Log.d(TAG, "onSuccessFullRun: Inserted Group Information");
        }
        c.close();
        db.close();
    }
}
