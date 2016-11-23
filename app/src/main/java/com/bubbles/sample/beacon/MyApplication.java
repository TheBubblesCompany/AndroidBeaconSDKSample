package com.bubbles.sample.beacon;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bubbles.sdk.beacon.Bubbles;
import com.bubbles.sdk.beacon.BubblesException;
import com.bubbles.sdk.beacon.BubblesInterface;

import java.util.Date;


public class MyApplication extends Application {

    private final String TAG = this.getClass().getSimpleName();

    private Context that = this;

    private final BubblesInterface bubblesInterface = new BubblesInterface() {

        @Override
        public void onNetworkNotAvailable() {
            Log.e(TAG, "OnNetworkNotAvailable");
        }

        @Override
        public void onScanFinished() {
            Log.e(TAG, "Scan is finished.");
        }

        @Override
        public void onReceivedBeaconNotification(int id, String notifText, Intent intent) {

            int notifIconResourceID;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) notifIconResourceID = R.drawable.ic_launcher_notif_kitkat;
            else notifIconResourceID = R.drawable.ic_launcher_notif;

            PendingIntent resultPendingIntent = PendingIntent.getActivity(that, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(that)
                    .setContentTitle(that.getResources().getString(R.string.app_name))
                    .setContentText(notifText)
                    .setSmallIcon(notifIconResourceID)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notifText))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    // .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + that.getPackageName() + "/raw/ctpn_perfect"))
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager) that.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, builder.build());
        }

        @Override
        public void onLocationChanged(Location currentLocation, Date lastUpdateTime) {
            Log.e(TAG, "OnLocationChanged : " + currentLocation.getLatitude() + " " + currentLocation.getLongitude() + " " + lastUpdateTime.toString());
        }

        @Override
        public Class<?> getDefaultActivityClass() {
            return MainActivity.class;
        }

        @Override
        public Class<?> getImageActivityClass() {
            return ImageBeaconActivity.class;
        }

        @Override
        public Class<?> getUriActivityClass() {
            return UriBeaconActivity.class;
        }

        @Override
        public Class<?> getWebViewActivityClass() {
            return WebViewBeaconActivity.class;
        }

        @Override
        public Class<?> getServiceActivityClass() {
            return ServiceBeaconActivity.class;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        that = this;

        // Init Bubbles SharedPreferences
        Const.sharedPreferences = getSharedPreferences(Const.BUBBLES_APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Init GCM
        final String registrationID = Const.sharedPreferences.getString(Const.BSP_REGISTRATION_ID_STRING, null);
        if (registrationID != null) initBubblesSDK(registrationID);
        else {
            startService(new Intent(this, GCMRegistrationIntentService.class));
            waitForRegistrationID();
        }
    }

    private void waitForRegistrationID() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("APP", "registrationID : [" + Const.sharedPreferences.getString(Const.BSP_REGISTRATION_ID_STRING, null) + "]");
                final String registrationID = Const.sharedPreferences.getString(Const.BSP_REGISTRATION_ID_STRING, null);
                if (registrationID != null) initBubblesSDK(registrationID);
                else waitForRegistrationID();
            }
        }, 500);
    }

    public void initBubblesSDK(String registrationID) {
        try {
            if (Const.bubbles == null) Const.bubbles = Bubbles.getInstance(this, null, registrationID, false, null);
        } catch (BubblesException e) {
            Log.e("APP", e.getMessage());
        }

        if (Const.bubbles != null) Const.bubbles.setInterface(bubblesInterface);
    }
}
