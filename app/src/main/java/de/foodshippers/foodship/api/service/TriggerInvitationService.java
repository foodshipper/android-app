package de.foodshippers.foodship.api.service;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by soenke on 21.11.16.
 */
public interface TriggerInvitationService {
    @PUT("/v1/user/groups")
    Call<Void> putReinvite(@Query("token") String token, @Query("resend_all") boolean resendAll);

}
