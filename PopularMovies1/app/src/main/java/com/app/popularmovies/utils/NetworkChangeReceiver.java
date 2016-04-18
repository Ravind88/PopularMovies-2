package com.app.popularmovies.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.popularmovies.AppController;


/**
 * Created by ravind maurya on 30/9/15.
 * Class to manage network change, right now we are not using this class.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utility.getNetworkState(context)) {
            AppController.getApplicationInstance().setIsNetworkConnected(true);
            Lg.i("Network Receiver", "connected");
        } else {
            AppController.getApplicationInstance().setIsNetworkConnected(false);
            Lg.i("Network Receiver", "disconnected");
        }
    }
}
