package de.foodshippers.foodship;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.SetUserFirebaseTokenJob;

/**
 * Created by soenke on 04.12.16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIDSer";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        FoodshipJobManager.getInstance(getApplicationContext()).addJobInBackground(new SetUserFirebaseTokenJob(CommunicationManager.getUserId(getApplicationContext()), refreshedToken));
    }
}
