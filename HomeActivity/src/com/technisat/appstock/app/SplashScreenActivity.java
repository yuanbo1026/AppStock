package com.technisat.appstock.app;

import org.json.JSONObject;

import com.crittercism.app.Crittercism;
import com.technisat.appstock.R;
import com.technisat.appstock.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 500;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        Crittercism.initialize(getApplicationContext(), "549002233cf56b9e0457cac4");
        JSONObject jsonObject= LoginActivity.loadLogin(this);
        if(jsonObject!=null)
        	LoginActivity.login(jsonObject);
        new Handler().postDelayed(new Runnable() {
        	
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, BaseDrawerActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
