package com.example.wang.alice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wang.alice.TTS.AliceSpeech;

public class NeedActivity extends AppCompatActivity {

    private boolean isSumbitted = false;
    private LinearLayout mLin;
    private EditText nameEt;
    private TextView message;
    private Button submitButton;

    private AliceSpeech speech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        mLin = findViewById(R.id.final_l);
        mLin.setVisibility(View.INVISIBLE);

        nameEt = findViewById(R.id.edit_name);
        message = findViewById(R.id.message_tv_1);
        submitButton = findViewById(R.id.button);

        speech = AliceSpeech.GenerateSpeech(this, "Hello, how may I call you?");
    }

    public void startMain(View view){
        if (view.getId() == R.id.button){
            if (!isSumbitted){
                String name = nameEt.getText().toString();
                if (!name.equals("")){
                    String messageString = "Hello, "+ name + ". My name is Alice. I'm your personal virtual assistant.";
                    message.setText(messageString);
                    speech.speak(messageString, "need speech");
                    isSumbitted = true;
                    mLin.setVisibility(View.VISIBLE);
                    submitButton.setText("Let's go! ");

                    SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    preferences.edit().putString("name", name).apply();
                }else{
                    Toast.makeText(this, "Please input your name. ", Toast.LENGTH_SHORT).show();
                }
            }else{
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putBoolean("activity_executed", true);
                ed.apply();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    protected void onDestroy() {
        speech.destroySpeech();
        super.onDestroy();
    }
}
