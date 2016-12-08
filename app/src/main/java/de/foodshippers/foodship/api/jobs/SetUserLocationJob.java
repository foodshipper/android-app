package de.foodshippers.foodship.api.jobs;

import de.foodshippers.foodship.api.RestClient;
import retrofit2.Call;

/**
 * Created by Hannes on 04.12.16.
 */
public class SetUserLocationJob extends SimpleNetworkJob {
    private double latitude;
    private double longitude;
    private String userId;

    public SetUserLocationJob(String userId, double latitude, double longitude) {
        super(SetUserLocationJob.class);
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }


    @Override
    protected Call getAPICall() {
        return RestClient.getInstance().getUserLocationService().setHomeLocation(userId, latitude, longitude);
    }
}
