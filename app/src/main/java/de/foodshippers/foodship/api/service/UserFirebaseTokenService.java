package de.foodshippers.foodship.api.service;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by soenke on 21.11.16.
 */
public interface UserFirebaseTokenService {
    @PUT("/v1/user/firebase-token")
    Call<Void> setUserFirebaseToken(@Query("token") String token, @Query("firebase_token") String firebaseToken);

}
