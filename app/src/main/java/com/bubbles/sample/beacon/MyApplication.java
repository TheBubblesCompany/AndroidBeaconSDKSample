package com.bubbles.sample.beacon;

import android.app.Application;
import android.util.Log;

import com.bubbles.sdk.beacon.BubblesBeaconApplication;
import com.bubbles.sdk.beacon.BubblesBeaconInterface;
import com.bubbles.sdk.beacon.BubblesBeaconManager;
import com.bubbles.sdk.beacon.exception.BubblesBeaconException;

public class MyApplication extends Application implements BubblesBeaconApplication {

    private final String TAG = getClass().getName();

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            BubblesBeaconManager.getInstance(this);
        } catch (BubblesBeaconException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public BubblesBeaconInterface getBeaconInterface() {
        return AppBubblesBeaconManager.getInstance(this);
    }
}
