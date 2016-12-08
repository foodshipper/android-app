package de.foodshippers.foodship;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import static de.foodshippers.foodship.MainActivity.ARG_NOTIFICATION_ID;

/**
 * Created by hannes on 06.12.16.
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        this("NotificationService");
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra(ARG_NOTIFICATION_ID, -1));
        GroupDataController.getInstance(getApplicationContext()).cancel();
    }
}
