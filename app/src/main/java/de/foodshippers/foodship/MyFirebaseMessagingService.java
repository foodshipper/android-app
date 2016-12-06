package de.foodshippers.foodship;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by soenke on 04.12.16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "onMessageReceived: Received Message!");
//        Log.d(TAG, "onMessageReceived: FROM: " + remoteMessage.getFrom());
//        for (Map.Entry<String, String> e : remoteMessage.getData().entrySet()) {
//            Log.d(TAG, "onMessageReceived: " + e.getKey() + ": " + e.getValue());
        Context c = getApplicationContext();
        int mNotificationId = (int) System.currentTimeMillis();
        Intent intendyes = new Intent(c, MainActivity.class);
        Intent intendno = new Intent(c, NotificationActivity.class);
        intendno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intendno.putExtra("Noti_ID", mNotificationId);
        intendyes.putExtra("Noti_ID", mNotificationId);
        intendyes.putExtra("Fragment", "DinnerGroup");
        PendingIntent pendingyes =
                PendingIntent.getActivity(
                        c,
                        0,
                        intendyes,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        PendingIntent pendingno =
                PendingIntent.getActivity(
                        c,
                        1,
                        intendno,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        intendno.putExtra("Noti_ID", mNotificationId);
        intendyes.putExtra("Noti_ID", mNotificationId);
        intendyes.putExtra("Fragment", "DinnerGroup");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .addAction(R.drawable.ic_menu_camera, getString(R.string.accept_Invitation), pendingyes)
                        .addAction(R.drawable.ic_menu_camera, getString(R.string.disline_Invation), pendingno)
                        .setDeleteIntent(pendingno)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!").setAutoCancel(true);


        NotificationManager mNotifyMgr =
                (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
