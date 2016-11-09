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
    private boolean connectedWithAPI;


    public NetworkChangeReceiver(CommunicationManager manager, Context conStartUp) {
        this.conMan = manager;
        parseChanges(conStartUp);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      parseChanges(context);
    }

    public void parseChanges(Context con){
        int status = NetworkUtil.getConnectivityStatusString(con);
        System.out.println(ProcessNetworkChange(status, this.Internet_Status));
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

    public String ProcessNetworkChange(int latest, int old) {
        String Change;
        if (old == latest) {
            Change = "";
        } else {
            switch (latest) {
                case NetworkUtil.NETWORK_STATUS_MOBILE:
                    Change = "MOBILE";
                    conMan.setSendable(false);
                    break;
                case NetworkUtil.NETWORK_STAUS_WIFI:
                    Change = "Wifi";
                    conMan.setSendable(true);
                    break;
                case NetworkUtil.NETWORK_STATUS_NOT_CONNECTED:
                default:
                    Change = "NoInternet";
                    conMan.setSendable(false);
            }
        }
        return Change;
    }
}
