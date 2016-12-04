package de.foodshippers.foodship;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by soenke on 04.12.16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO: 04.12.16 Handle Notifications 
        Log.d(TAG, "onMessageReceived: Received Message!");
        Log.d(TAG, "onMessageReceived: FROM: " + remoteMessage.getFrom());
        for (Map.Entry<String, String> e : remoteMessage.getData().entrySet()) {
            Log.d(TAG, "onMessageReceived: " + e.getKey() + ": " + e.getValue());
        }

    }
}
