package org.mike.autolog.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.mike.autolog.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GuestLongReceiver extends BroadcastReceiver {

    private static final String AP_NAME = "\"ING_GUEST_LONG\"";
    private static NetworkInfo.State previousState = NetworkInfo.State.UNKNOWN;


    public GuestLongReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if (networkInfo != null) {
            Log.d("GuestLongReceiver", "NetworkName: " + networkInfo.getExtraInfo());
            Log.d("GuestLongReceiver", "NetworkStatue: " + networkInfo.getState());
            Log.d("GuestLongReceiver", "NetworkPreviousState: " + previousState);


            if (AP_NAME.equals(networkInfo.getExtraInfo()) && networkInfo.getState() == NetworkInfo.State.CONNECTED && previousState != NetworkInfo.State.CONNECTED) {
                Log.d("GuestLongReceiver", "Network OK");
                context.startActivity(
                        MainActivity.newIntent(context, networkInfo)
                                .setFlags(FLAG_ACTIVITY_NEW_TASK)
                );
            }

            previousState = networkInfo.getState();
        }
    }
}
