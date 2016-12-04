package de.foodshippers.foodship.api.jobs;

import de.foodshippers.foodship.api.RestClient;
import retrofit2.Call;

/**
 * Created by soenke on 04.12.16.
 */
public class SetUserFirebaseTokenJob extends SimpleNetworkJob {
    private String firebaseToken;
    private String token;

    public SetUserFirebaseTokenJob(String token, String firebaseToken) {
        super(SetUserFirebaseTokenJob.class);
        this.firebaseToken = firebaseToken;
        this.token = token;
    }

    @Override
    protected Call getAPICall() {
        return RestClient.getInstance().getUserFirebaseTokenService().setUserFirebaseToken(token, firebaseToken);
    }

}
