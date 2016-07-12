package com.bubbles.sample.beacon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageBeaconActivity extends AppCompatActivity {

    private static final String TYPE_URI = "URI";
    private static final String TYPE_WEBVIEW = "WVW";
    private static String actionType = null;
    private static String actionUriDefault = null;
    private static String actionUriFallback = null;
    private static String actionUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {

            ImageView image = (ImageView) findViewById(R.id.image);
            Picasso.with(this).load(extras.getString("url")).into(image);

            actionType = extras.getString("action_type");

            if (actionType != null && !actionType.isEmpty()) {

                if (image != null) image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (actionType.equals(TYPE_URI)) {

                            actionUriDefault = extras.getString("action_uri_default");
                            actionUriFallback = extras.getString("action_uri_fallback");

                            if (actionUriDefault != null && !actionUriDefault.isEmpty()) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(actionUriDefault)));
                                finish();
                            } else if (actionUriFallback != null && !actionUriFallback.isEmpty()) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(actionUriFallback)));
                                finish();
                            }

                        } else if (actionType.equals(TYPE_WEBVIEW)) {

                            actionUrl = extras.getString("action_url");

                            if (actionUrl != null && !actionUrl.isEmpty()) {
                                Intent intent = new Intent(ImageBeaconActivity.this, WebViewBeaconActivity.class);
                                intent.putExtra("url", actionUrl);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ImageBeaconActivity.this, MainActivity.class));
        super.onBackPressed();
    }
}
