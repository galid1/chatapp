package com.galid.ongtalk.view.splash;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.galid.ongtalk.R;
import com.galid.ongtalk.view.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout linearLayout;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        linearLayout = findViewById(R.id.linearlayout_splash);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                        }
                        displayWelcomeMessage();
                    }
                });

    }

    public void displayWelcomeMessage() {
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        boolean splash_message_caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        linearLayout.setBackgroundColor(Color.parseColor(splash_background));

        if (splash_message_caps) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

            builder.create().show();
        }
        else {
           Intent intent = new Intent(this , LoginActivity.class);
           startActivity(intent);
           finish();
        }

    }
}
