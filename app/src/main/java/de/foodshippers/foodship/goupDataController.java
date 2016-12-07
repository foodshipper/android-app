package de.foodshippers.foodship;

import android.content.Context;
import de.foodshippers.foodship.Bilder.GroupImageManager;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.model.GroupInformations;
import de.foodshippers.foodship.api.model.Recipe;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hannes on 06.12.16.
 */
public class goupDataController {

    private boolean inActivGroup;
    private static goupDataController instance;
    private GroupInformations infos;
    private int groupId;
    private List<Recipe> possibleRecipies;
    private final Context c;

    public goupDataController(Context c) {
        this.c = c;
    }

    public void acceptGroup() {
        inActivGroup = true;
        RestClient.getInstance().getDinnerService().acceptInvite(groupId, Utils.getUserId(c), true);
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public int getGroupId() {
        return groupId;
    }

    public void cancel() {
        RestClient.getInstance().getDinnerService().acceptInvite(groupId, Utils.getUserId(c), false);
        inActivGroup = false;
    }

    public static goupDataController getInstance(Context c) {
        if (instance == null) {
            instance = new goupDataController(c);
        }
        return instance;
    }

    public boolean isInGroup() {
        return inActivGroup;
    }

    public GroupInformations getInfos() {
        return infos;
    }

    public List<Recipe> getPossibleRecipies() {
        return possibleRecipies;
    }

    public void prefetch() {
        try {
            Response<GroupInformations> execute = RestClient.getInstance().getDinnerService().getGroupInformation(getGroupId(), Utils.getUserId(c)).execute();
            infos = execute.body();
            Response<Recipe[]> execute1 = RestClient.getInstance().getDinnerService().getRecipes(groupId, Utils.getUserId(c)).execute();
            possibleRecipies = Arrays.asList(execute1.body());
            final GroupImageManager grouppics = GroupImageManager.getInstance(c);
            for (Recipe r : possibleRecipies) {
                grouppics.downloadifNeeded(r.getImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
