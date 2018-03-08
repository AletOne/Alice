package com.example.wang.alice.TTS;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.Locale;

/**
 * Created by Wang on 3/6/18.
 */

public class AliceSpeech {
    private static AliceSpeech aliceSpeech;
    private TextToSpeech textToSpeech;

    public static AliceSpeech GenerateSpeech(Context context, String text){
        if (aliceSpeech != null){
            return aliceSpeech;
        }else{
            aliceSpeech = new AliceSpeech(context, text);
            return aliceSpeech;
        }
    }

    private AliceSpeech(Context context, String text){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This language is not support");
                    }else{
                        speak("What can I help you?", "111111");

                        //Log.d("speak", result+ "");
                    }
                }else{
                    Log.e("error", "Initialed Failed");
                }
            }
        });
    }

    public int speak(String text, String utteranceId){
        return textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

}
