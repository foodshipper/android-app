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
public class GroupDataManager {

    private boolean inActivGroup;
    private static GroupDataManager instance;
    private GroupInformations infos;
    private int groupId;
    private List<Recipe> possibleRecipies;
    private final Context c;

    public GroupDataManager(Context c) {
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

    public static GroupDataManager getInstance(Context c) {
        if (instance == null) {
            instance = new GroupDataManager(c);
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
            System.out.println(execute1.message());
            System.out.println(execute1.code());
            System.out.println(execute1.errorBody());
            System.out.println(execute1.raw().request().url());
            System.out.println(execute1.body().length);
            possibleRecipies = Arrays.asList(execute1.body());
            final GroupImageManager grouppics = GroupImageManager.getInstance(c);
            for (Recipe r : possibleRecipies) {
                System.out.println(r);
                grouppics.downloadifNeeded(r.getImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
