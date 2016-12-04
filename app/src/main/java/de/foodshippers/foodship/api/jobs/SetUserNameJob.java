package de.foodshippers.foodship.api.jobs;

import de.foodshippers.foodship.api.RestClient;
import retrofit2.Call;

/**
 * Created by soenke on 21.11.16.
 */
public class SetUserNameJob extends SimpleNetworkJob {
    private String name;
    private String token;

    public SetUserNameJob(String token, String name) {
        super(SetUserLocationJob.class);
        this.name = name;
        this.token = token;
    }


    @Override
    protected Call getAPICall() {
        return RestClient.getInstance().getUserNameService().setUserName(token, name);
    }
}
