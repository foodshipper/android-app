package de.foodshippers.foodship;


import android.app.Fragment;
import android.app.NotificationManager;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getActivity().getApplicationContext())
                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!");
                NotificationManager mNotifyMgr =
                        (NotificationManager) getActivity().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                int mNotificationId = 001;
// Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        });

        return v;
    }

}
