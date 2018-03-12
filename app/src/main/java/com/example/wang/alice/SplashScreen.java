package com.example.wang.alice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //translucent status
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //translucent navigation
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }



        Thread myThread = new Thread() {
            @Override
            public void run(){
                try {
                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    //editor.clear().apply();

                    boolean activityExecuted = pref.getBoolean("activity_executed", false);
                    Log.d("pref", activityExecuted+"");
                    if (activityExecuted){
                        sleep(3000);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{

                        sleep(3000);
                        Intent intent = new Intent(getApplicationContext(), NeedActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                }
            };

        myThread.start();
        }


}

