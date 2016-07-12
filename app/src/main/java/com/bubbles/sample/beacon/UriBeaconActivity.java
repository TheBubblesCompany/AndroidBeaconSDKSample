package com.bubbles.sample.beacon;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class UriBeaconActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        launchURI();
    }

    private void launchURI() {

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String uriDefault = extras.getString("uri_default");
            String uriFallback = extras.getString("uri_fallback");

            if (uriDefault != null && !uriDefault.isEmpty()) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriDefault)));
                } catch (ActivityNotFoundException e1) {
                    try {
                        startActivity(new Intent(uriDefault));
                    } catch (ActivityNotFoundException e2) {
                    }
                }

            } else if (uriFallback != null && !uriFallback.isEmpty()) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriFallback)));
                } catch (ActivityNotFoundException e1) {
                    try {
                        startActivity(new Intent(uriFallback));
                    } catch (ActivityNotFoundException e2) {
                    }
                }
            }
        }

        finish();
    }
}
