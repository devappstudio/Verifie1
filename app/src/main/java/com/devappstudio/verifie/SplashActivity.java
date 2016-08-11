package com.devappstudio.verifie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.ChasingDots;

import datastore.RealmController;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //RealmController.with(getApplication()).clearAll();
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        ChasingDots doubleBounce = new ChasingDots();
        progressBar.setIndeterminateDrawable(doubleBounce);
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);

                    // After 5 seconds redirect to another intent

                    Intent intent=new Intent(getBaseContext(),Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    startActivity(intent);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };
        Thread background1 = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);


                    Intent intent=new Intent(getBaseContext(),main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();


                } catch (Exception e) {

                }
            }
        };

            if(RealmController.with(getApplication()).hasUser())
            {
                background1.start();

            }
            else
            {
                background.start();
            }

     }






}
