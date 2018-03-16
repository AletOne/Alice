package com.example.wang.alice;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Wang on 3/15/18.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String WEATHER_KEY = "weather";
    private static final String CALL_KEY = "call";
    private static final String TEXT_KEY = "text";
    private static final String REMIDER_KEY = "reminder";
    private static final String NOTES_KEY = "notes";
    private static final String UBER_KEY = "uber";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        switch (key){
            case WEATHER_KEY:
                setContentView(R.layout.activity_weather);
                break;
            case CALL_KEY:
                setContentView(R.layout.activity_phone);
                break;
            case TEXT_KEY:

                break;
            case REMIDER_KEY:

                break;
            case NOTES_KEY:

                break;
            case UBER_KEY:

                break;
        }
    }
}
