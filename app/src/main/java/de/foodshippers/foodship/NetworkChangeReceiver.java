package de.foodshippers.foodship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by hannes on 06.11.16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static CommunicationManager conMan;
    private int Internet_Status;
    private boolean connectedWithAPI;
    private static NetworkChangeReceiver mReceiver = null;
    private static Context mContext = null;
    private boolean isRegistered = false;

    private NetworkChangeReceiver() {
        parseChanges(mContext);
    }

    public static NetworkChangeReceiver newInstance(CommunicationManager manager, Context conStartUp) {
        mContext = conStartUp;
        conMan = manager;
        if(mReceiver == null) {
            mReceiver = new NetworkChangeReceiver();
        }
        mReceiver.register(conStartUp);
        return mReceiver;
    }

    private void register(Context context) {
        if(context != null && !isRegistered) {
            final IntentFilter filters = new IntentFilter();
            filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this, filters);
            isRegistered = true;
        }
    }

    public static void unregister() {
        if(mReceiver != null) {
            mReceiver.unregister(mContext);
        }
    }

    private void unregister(Context context) {
        if(isRegistered) {
            context.unregisterReceiver(mReceiver);
            isRegistered = false;
        }
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
