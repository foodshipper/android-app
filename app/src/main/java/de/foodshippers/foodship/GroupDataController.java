package de.foodshippers.foodship;

import android.content.Context;
import android.util.Log;
import de.foodshippers.foodship.Bilder.GroupImageManager;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.jobs.DinnerResponseJob;
import de.foodshippers.foodship.api.jobs.GetGroupInformationJob;
import de.foodshippers.foodship.api.model.GroupInformation;
import de.foodshippers.foodship.api.model.Recipe;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hannes on 06.12.16.
 */
public class GroupDataController {

    private boolean inActivGroup;
    private static GroupDataController instance;
    private GroupInformation infos;
    private int groupId;
    private List<Recipe> possibleRecipies;
    private final Context c;
    private static final String TAG = "GroupDataController";

    public GroupDataController(Context c) {
        this.c = c;
    }

    public void acceptGroup() {
        inActivGroup = true;
        Log.d(TAG, "acceptGroup: Accept Group");
        FoodshipJobManager.getInstance(c).addJobInBackground(new DinnerResponseJob(groupId, true));
        RestClient.getInstance().getDinnerService().acceptInvite(groupId, Utils.getUserId(c), true);
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public int getGroupId() {
        return groupId;
    }

    public void cancel() {
        FoodshipJobManager.getInstance(c).addJobInBackground(new DinnerResponseJob(groupId, false));
        inActivGroup = false;
    }

    public static GroupDataController getInstance(Context c) {
        if (instance == null) {
            instance = new GroupDataController(c);
        }
        return instance;
    }

    public boolean isInGroup() {
        return inActivGroup;
    }

    public GroupInformation getInfos() {
        return infos;
    }

    public List<Recipe> getPossibleRecipies() {
        return possibleRecipies;
    }

    public void prefetch() {
        try {
            FoodshipJobManager.getInstance(c).addJobInBackground(new GetGroupInformationJob(groupId, c));
            Response<GroupInformation> execute = RestClient.getInstance().getDinnerService().getGroupInformation(getGroupId(), Utils.getUserId(c)).execute();
            infos = execute.body();
            Response<Recipe[]> execute1 = RestClient.getInstance().getDinnerService().getRecipes(groupId, Utils.getUserId(c)).execute();
            possibleRecipies = Arrays.asList(execute1.body());
            final GroupImageManager grouppics = GroupImageManager.getInstance(c);
            for (Recipe r : possibleRecipies) {
                grouppics.downloadImage(r.getImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
