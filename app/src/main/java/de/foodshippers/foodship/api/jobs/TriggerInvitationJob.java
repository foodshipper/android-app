package de.foodshippers.foodship.api.jobs;

import de.foodshippers.foodship.api.RestClient;
import retrofit2.Call;

/**
 * Created by soenke on 21.11.16.
 */
public class TriggerInvitationJob extends SimpleNetworkJob {
    private boolean resend_all;
    private String token;

    public TriggerInvitationJob(String token, boolean resend_all) {
        super(TriggerInvitationJob.class);
        this.resend_all = resend_all;
        this.token = token;
    }


    @Override
    protected Call getAPICall() {
        return RestClient.getInstance().getTriggerInvitationService().putReinvite(token, true);
    }
}
