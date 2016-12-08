package de.foodshippers.foodship;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static de.foodshippers.foodship.DinnerGroupFragment.GROUP_ID;
import static de.foodshippers.foodship.MainActivity.*;

/**
 * Created by soenke on 04.12.16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage firebaseMassage) {
        super.onMessageReceived(firebaseMassage);
        int groupId = Integer.decode(firebaseMassage.getData().get("group_id"));
        //Send Notification to User
        sendNotificationtoUser(groupId);
        //Gets the dataController
        GroupDataController dataController = GroupDataController.getInstance(getApplicationContext());
        //Sets new GroupID
        dataController.setGroupId(Integer.decode(firebaseMassage.getData().get("group_id")));
        //Starts Prefetching of GroupData
        dataController.prefetch();
    }

    private void sendNotificationtoUser(int groupId) {
        Context c = getApplicationContext();
        int mNotificationId = (int) System.currentTimeMillis();
        Intent intendyes = new Intent(c, MainActivity.class);
        Intent intendno = new Intent(c, NotificationService.class);
        intendno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intendno.putExtra(ARG_NOTIFICATION_ID, mNotificationId);
        intendyes.putExtra(ARG_NOTIFICATION_ID, mNotificationId);
        intendyes.putExtra(GROUP_ID, groupId);
        intendyes.putExtra(ARG_FRAGMENT, DINNER_FRAGMENT);
        PendingIntent pendingyes =
                PendingIntent.getActivity(
                        c,
                        0,
                        intendyes,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        PendingIntent pendingno =
                PendingIntent.getService(
                        c,
                        1,
                        intendno,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .addAction(R.drawable.ic_thumb_up_black_24dp, getString(R.string.accept_Invitation), pendingyes)
                        .addAction(R.drawable.ic_do_not_disturb_black_24dp, getString(R.string.disline_Invation), pendingno)
                        .setDeleteIntent(pendingno)
                        .setSmallIcon(R.drawable.ic_restaurant_menu_white_24dp)
                        .setContentTitle("Joint Dinner?")
                        .setContentText("I don't want to eat alone").setAutoCancel(true);


        NotificationManager mNotifyMgr =
                (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
