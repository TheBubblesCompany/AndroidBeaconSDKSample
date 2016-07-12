package com.bubbles.sample.beacon;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GCMRegistrationIntentService extends IntentService {

    public GCMRegistrationIntentService() {
        super("GCMRegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String token = InstanceID.getInstance(this).getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Const.sharedPreferences.edit().putString(Const.BSP_REGISTRATION_ID_STRING, token).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
