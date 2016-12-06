package de.foodshippers.foodship;


import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.firebase.iid.FirebaseInstanceId;
import de.foodshippers.foodship.api.FoodshipJobManager;
import de.foodshippers.foodship.api.jobs.SetUserFirebaseTokenJob;
import de.foodshippers.foodship.api.jobs.TriggerInvitationJob;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeveloperFragment extends Fragment {
    private static final String TAG = "DeveloperFragment";

    public DeveloperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_developer, container, false);
        Button resetLocation = (Button) v.findViewById(R.id.resetLocationBtn);
        resetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .edit();
                Utils.putDouble(e, "latitude", 0);
                Utils.putDouble(e, "longitude", 0);
                e.apply();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
            }
        });

        Button refreshFoodTypes = (Button) v.findViewById(R.id.refreshFoodTypesBtn);
        refreshFoodTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.downloadFoodTypes(getActivity().getApplicationContext(), false);
            }
        });

        Button triggerNotifBtn = (Button) v.findViewById(R.id.triggerInvitation);
        triggerNotifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new TriggerInvitationJob(Utils.getUserId(getActivity()), true));
            }
        });

        Button sendFirebaseToken = (Button) v.findViewById(R.id.sendFirebaseToken);
        sendFirebaseToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "onClick: Token: " + token);
                FoodshipJobManager.getInstance(getActivity()).addJobInBackground(new SetUserFirebaseTokenJob(Utils.getUserId(getActivity()), token));

            }
        });

        Button sendTestNotification = (Button) v.findViewById(R.id.sendTestNotification);
        sendTestNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = getActivity().getApplicationContext();
                int mNotificationId = (int) System.currentTimeMillis();
                Intent intendyes = new Intent(c, MainActivity.class);
                Intent intendno = new Intent(c, NotificationService.class);
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

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(c)
                                .addAction(R.drawable.ic_thumb_up_black_24dp, getString(R.string.accept_Invitation), pendingyes)
                                .addAction(R.drawable.ic_do_not_disturb_black_24dp, getString(R.string.disline_Invation), pendingno)
                                .setDeleteIntent(pendingno)
                                .setSmallIcon(R.drawable.ic_restaurant_menu_white_24dp)
                                .setContentTitle("Troll")
                                .setContentText("Essen?").setAutoCancel(true);


                NotificationManager mNotifyMgr =
                        (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        });

        return v;
    }

}
