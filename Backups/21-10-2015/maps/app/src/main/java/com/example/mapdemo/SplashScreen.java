package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;



public class SplashScreen extends Activity {

    private static final String BASE64_PUBLIC_KEY = "";
    private int splashTime = 4000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Create an intent that will start the main activity.
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);

                //Finish splash activity so user cant go back to it.
                SplashScreen.this.finish();

                Runtime.getRuntime().gc();

                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                overridePendingTransition(R.anim.main_fadein, R.anim.splash_fadeout);
            }
        }, splashTime);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.gc();
    }
}
