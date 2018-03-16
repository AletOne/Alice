package com.example.wang.alice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wang.alice.TTS.AliceSpeech;

import java.util.Locale;

public class NeedActivity extends AppCompatActivity {


    private final static int PERMISSION_RECORD_AUDIO = 7000;
    private final static int PERMISSION_READ_CONTACTS = 7001;
    private final static int PERMISSION_CALL = 7002;
    private final static int PERMISSION_WRITE_SMS = 7003;


    private boolean audioPermission = true;
    private boolean readContacts = true;
    private boolean callPermission = true;
    private boolean writeSms = true;

    private boolean permission = false;

    private boolean isSumbitted = false;
    private LinearLayout mLin;
    private EditText nameEt;
    private TextView message;
    private Button submitButton;

    private TextToSpeech speech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        mLin = findViewById(R.id.final_l);
        mLin.setVisibility(View.INVISIBLE);

        nameEt = findViewById(R.id.edit_name);
        message = findViewById(R.id.message_tv_1);
        submitButton = findViewById(R.id.button);
        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = speech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This language is not support");
                    } else {
                        speech.speak("Hello, how may I call you?", TextToSpeech.QUEUE_FLUSH, null, "111111");

                        //Log.d("speak", result+ "");
                    }
                } else {
                    Log.e("error", "Initialed Failed");
                }
            }
        });
        //speech.speak("Hello, how may I call you?", "speak");
    }

    public void startMain(View view){
        if (view.getId() == R.id.button){
            if (!isSumbitted){
                String name = nameEt.getText().toString();
                if (!name.equals("")){
                    String messageString = "Hello, "+ name + ". I am Alice, your personal virtual assistant. I can help you check weather, send text message, make phones, navigation and many more. For more commands, go to settings to see what else I can do for you! :)";
                    message.setText(messageString);
                    speech.speak(messageString, TextToSpeech.QUEUE_FLUSH, null, "need speech");
                    isSumbitted = true;
                    mLin.setVisibility(View.VISIBLE);
                    submitButton.setText("Let's go! ");

                    SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    preferences.edit().putString("name", name).apply();
                }else{
                    Toast.makeText(this, "Please input your name. ", Toast.LENGTH_SHORT).show();
                }
            }else{
                checkAndRequestPermission();
                if (permission){
                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = pref.edit();
                    ed.putBoolean("activity_executed", true);
                    ed.apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    onDestroy();
                }else{
                    Toast.makeText(this, "You must accept the permissions to proceed", Toast.LENGTH_LONG).show();
                }

            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        speech.stop();
    }

    @Override
    public void finish() {
        if (speech != null){
            speech.shutdown();
        }
        super.finish();

    }

    @Override
    protected void onDestroy() {
        if (speech != null){
            speech.stop();
        }
        super.onDestroy();
    }

    private void checkAndRequestPermission(){
        //check and ask for permission.
        Log.d("permission", "checkAndRequestPermission");
        //if permission is not granted, ask for permission
        if (!permission){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS},
                    PERMISSION_RECORD_AUDIO);
            Log.d("permission", "Ask for permission");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("permission", "Request result");
        // According to requestCode to handle permission
        switch (requestCode){
            // permission is record audio
            case PERMISSION_RECORD_AUDIO:
                Log.d("permission", "Permission confirmed");
                //set isPermitRecordAudio true
                permission = true;
                //startListen();
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putBoolean("activity_executed", true);
                ed.apply();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
