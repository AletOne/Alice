package com.example.wang.alice;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button speakBtn;
    private SpeechRecognizer mSpeechRecognizer;
    private boolean isListening;
    private Intent mSpeechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speakBtn = findViewById(R.id.speak_btn);

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
                isListening = true;
                if (isListening){
                    Log.d("Speech", "Ready for Speech");
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }
        });
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
            Log.d("Speech", "End Speech");
            isListening = false;
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            List<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (String s : result){
                Log.d("Speech", s);
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
        super.onDestroy();
        if (mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
    }
}
