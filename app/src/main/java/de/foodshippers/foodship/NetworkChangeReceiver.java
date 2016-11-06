package de.foodshippers.foodship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hannes on 06.11.16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private final CommunicationManager conMan;
    private int Internet_Status;


    public NetworkChangeReceiver(CommunicationManager manager, Context base) {
        this.conMan = manager;
        this.Internet_Status = NetworkUtil.getConnectivityStatusString(base);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        System.out.println(change(status, this.Internet_Status));
        this.Internet_Status = status;
    }

    public int getInternet_Status() {
        return Internet_Status;
    }

    public boolean isInternetzConnected() {
        switch (Internet_Status) {
            case NetworkUtil.NETWORK_STATUS_MOBILE:
                return true;
            case NetworkUtil.NETWORK_STAUS_WIFI:
                return true;
            case NetworkUtil.NETWORK_STATUS_NOT_CONNECTED:
                return false;
        }
        return false;
    }

    public String change(int latest, int old) {
        String Change;
        if (old == latest) {
            Change = "";
        } else {
            switch (latest) {
                case NetworkUtil.NETWORK_STATUS_MOBILE:
                    Change = "MOBILE";
                    break;
                case NetworkUtil.NETWORK_STAUS_WIFI:
                    Change = "Wifi";
                    break;
                case NetworkUtil.NETWORK_STATUS_NOT_CONNECTED:
                default:
                    Change = "NoInternet";
            }
        }
        return Change;
    }
}
