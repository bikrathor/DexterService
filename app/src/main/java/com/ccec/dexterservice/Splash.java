package com.ccec.dexterservice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Splash extends ActionBarActivity {
    public Handler handler = new Handler();
    private static final int TIME = 1 * 1000;
    private UserSessionManager session;
    private String firstTime = "";
    static boolean calledAlready = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorGreen));
        }

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        firstTime = user.get(UserSessionManager.FIRST_TIME);

        if (session.isUserLoggedIn()) {handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent);
                    Splash.this.finish();
                }
            }, TIME);
        } else {
            if (firstTime == null) {handler.postDelayed(new Runnable() {
                    public void run() {
                        //to intro
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        Splash.this.finish();
                    }
                }, TIME);
            } else if (firstTime.equalsIgnoreCase("NO")) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        Splash.this.finish();
                    }
                }, TIME);
            }
        }
    }
}