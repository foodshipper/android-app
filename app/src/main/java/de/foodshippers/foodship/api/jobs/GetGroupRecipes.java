package de.foodshippers.foodship.api.jobs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import com.birbit.android.jobqueue.Params;
import de.foodshippers.foodship.Utils;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.Recipe;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;

import static de.foodshippers.foodship.db.FoodshipContract.RecipeTable.*;

/**
 * Created by hannes on 29.11.16.
 */
public class GetGroupRecipes extends SimpleNetworkJob<Recipe[]> {

    private int groupId;
    private Context context;

    public GetGroupRecipes(int groupId, Context context) {
        super(new Params(0).setPersistent(false).requireNetwork(), GetGroupRecipes.class);
        this.groupId = groupId;
        this.context = context;
    }


    @Override
    protected Call<Recipe[]> getAPICall() {
        return RestClient.getInstance().getDinnerService().getRecipes(groupId, Utils.getUserId(getApplicationContext()));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel: Job was cancelled");
    }

    @Override
    protected void onSuccessFullRun(Recipe[] recipes) {
        Log.d(TAG, "onSuccessFullRun: Got " + recipes.length + " recipes of Group " + groupId);
        SQLiteDatabase db = new FoodshipDbHelper(context).getWritableDatabase();
        for (Recipe recipe : recipes) {
            Cursor c = db.query(FoodshipContract.RecipeTable.TABLE_NAME, new String[]{CN_ID}, CN_ID + "=?", new String[]{String.valueOf(recipe.getId())}, null, null, null);
            if (c.moveToFirst()) {
                db.execSQL("UPDATE " + TABLE_NAME + " SET " + CN_IMG + "=?, " +
                                CN_DESC + "=?," +
                                CN_TITLE + "=?," +
                                CN_UPVOTES + "=?," +
                                CN_VETO + "=?," +
                                CN_GROUP + "=? " +
                                " WHERE " + CN_ID + "=?",
                        new String[]{recipe.getImage(), recipe.getDesc(), recipe.getTitle(), String.valueOf(recipe.getUpvotes()), String.valueOf(recipe.getVeto()), String.valueOf(groupId), String.valueOf(recipe.getId())});
                Log.d(TAG, "onSuccessFullRun: Updated Recipe Information");
            } else {
                db.execSQL("INSERT INTO " + TABLE_NAME + " (" + CN_ID + ", " + CN_IMG + ", " + CN_DESC + ", " + CN_TITLE + ", " + CN_UPVOTES + ", " + CN_VETO + ", " + CN_GROUP + ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new String[]{String.valueOf(recipe.getId()), recipe.getImage(), recipe.getDesc(), recipe.getTitle(), String.valueOf(recipe.getUpvotes()), String.valueOf(recipe.getVeto()), String.valueOf(groupId)});
                Log.d(TAG, "onSuccessFullRun: Inserted Recipe Information");
            }
            c.close();
        }
        db.close();
    }
}
