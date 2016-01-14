package com.bubbles.sample.beacon;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.bubbles.sdk.beacon.BubblesBeaconInterface;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AppBubblesBeaconManager implements BubblesBeaconInterface {

    // Singleton instance
    protected static AppBubblesBeaconManager instance = null;
    // Context variable
    protected Context application;

    private AppBubblesBeaconManager(Context application) {
        this.application = application;
    }

    /**
     * Get Beacon manager instance
     *
     * @param application Context application
     * @return Beacon manager instance
     */
    public static AppBubblesBeaconManager getInstance(Context application) {
        if (instance == null) instance = new AppBubblesBeaconManager(application);
        return instance;
    }

    /**
     * Get current application user identifier to restrict scenario
     *
     * @return User identifier
     */
    @Override
    public String getUserId() {
        return null;
    }

    /**
     * Send notification for detected scenario
     *
     * @param text   Notification text configured in Beacon CMS
     * @param intent Intent to redirect user to
     */
    @Override
    public void sendNotification(String text, Intent intent) {

        boolean foreground = false;
        try {
            foreground = new ForegroundCheckTask().execute(application).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // if Background, show Notification
        if (!foreground) {

            int notifIconRessourceID;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) notifIconRessourceID = R.drawable.ic_launcher_notif_kitkat;
            else notifIconRessourceID = R.drawable.ic_launcher_notif;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(application)
                    .setContentTitle(application.getResources().getString(R.string.app_name))
                    .setContentText(text)
                    .setSmallIcon(notifIconRessourceID)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);

            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        } else {
            // else if Foreground, instantly show Activity
            application.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public Class<?> getDefaultActivityClass() {
        return MainActivity.class;
    }

    @Override
    public Class<?> getWebViewActivityClass() {
        return WebViewActivity.class;
    }

    @Override
    public Class<?> getImageActivityClass() {
        return ImageActivity.class;
    }

    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            return isAppOnForeground(params[0].getApplicationContext());
        }

        private boolean isAppOnForeground(Context context) {
            List<ActivityManager.RunningAppProcessInfo> appProcesses = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
            if (appProcesses == null) return false;
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName))
                    return true;
            }
            return false;
        }
    }
}
