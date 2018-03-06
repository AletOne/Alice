package com.example.wang.alice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.wang.alice.RecyclerViewUtil.MessageAdapter;
import com.example.wang.alice.RecyclerViewUtil.SpaceItemDecoration;
import com.example.wang.alice.mode.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_RECORD_AUDIO = 7000;


    private Button speakBtn;
    private RecyclerView mRecyclerView;

    private MessageAdapter mAdapter;
    private List<Message> messageList;


    private SpeechRecognizer mSpeechRecognizer;
    private boolean isListening;
    private Intent mSpeechRecognizerIntent;
    private boolean isPermitRecordAudio;//permission to record audio

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        speakBtn = findViewById(R.id.speak_btn);
        mRecyclerView = findViewById(R.id.message_rv);

        //initial left message
        initialMessageRecyclerView();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                                            this.getPackageName());

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission
                if (!isPermitRecordAudio){
                    checkAndRequestPermission();
                }
                //After ask for permission, if confirmed, start listening
                if (isPermitRecordAudio){
                    startListen();
                }
            }
        });
    }

    private void startListen(){
        //start listening; change the button
        if (!isListening){
            Log.d("Speech", "Ready for Speech");
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            speakBtn.startAnimation(scale);
            //speakBtn.setText("Listening");
            isListening = true;
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        } else{
            mSpeechRecognizer.stopListening();
            speakBtn.clearAnimation();
            isListening = false;
        }
    }

    class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("Speech", "Ready for Speech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("Speech", "Begin Speech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            //at the end of speech, change button
            Log.d("Speech", "End Speech");
            //speakBtn.setText("Speech");
            speakBtn.clearAnimation();
            isListening = false;
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            //get result from results, and display result
            List<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(result != null){
//                for (String s : result){
//                    Log.d("Speech", s);
//                }
                Log.d("Speech", result.get(0));
                messageList.add(new Message(result.get(0), Message.RIGHT_MESSAGE));
                mAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    @Override
    protected void onDestroy() {
        //override onDestroy, destroy speechRecognizer
        super.onDestroy();
        if (mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
    }

    private void checkAndRequestPermission(){
        //check and ask for permission.
        Log.d("permission", "checkAndRequestPermission");
        //if permission is not granted, ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_RECORD_AUDIO);
            Log.d("permission", "Ask for permission");
        }else{ // if permission is granted, set isPermitRecordAudio true
            isPermitRecordAudio = true;
        }
    }

    // override onRequestPermissionResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("permission", "Request result");
        // According to requestCode to handle permission
        switch (requestCode){
            // permission is record audio
            case PERMISSION_RECORD_AUDIO:
                Log.d("permission", "Permission confirmed");
                //set isPermitRecordAudio true
                isPermitRecordAudio = true;
                //startListen();
                break;
        }
    }

    private void initialMessageRecyclerView(){
        messageList = new ArrayList<>();
        messageList.add(new Message("What can I help you? ", Message.LEFT_MESSAGE));
        messageList.add(new Message("Direction to Santa Clara University", Message.RIGHT_MESSAGE));
        mAdapter = new MessageAdapter(LayoutInflater.from(this), messageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(50));
        mRecyclerView.setAdapter(mAdapter);
    }
}
