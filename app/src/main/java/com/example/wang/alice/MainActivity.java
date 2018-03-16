package com.example.wang.alice;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.example.wang.alice.RecyclerViewUtil.MessageAdapter;
import com.example.wang.alice.RecyclerViewUtil.SpaceItemDecoration;
//import com.example.wang.alice.TTC.AsyncNamedLoader;
import com.example.wang.alice.TTC.ContactsUtil;
import com.example.wang.alice.TTC.DateTime;
import com.example.wang.alice.TTC.TextToCommand;
import com.example.wang.alice.TTC.WeatherUtil;
import com.example.wang.alice.TTS.AliceSpeech;
import com.example.wang.alice.mode.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final static String FAILED_MESSAGE = "Sorry, I don't understand what you said. :(";

    private final static int PERMISSION_RECORD_AUDIO = 7000;
    private final static int NLP_LOADER_ID = 3000;
    private final static String LEFT_SPEAK_ID = "ALICE_SPEAK";

    //module number
    private static final int PREPARE_SMS = 7;
    private static final int SMS = 1;
    private static final int WEATHER = 2;
    private static final int DATE = 3;
    private static final int PHONE = 4;
    private static final int MAP = 5;
    private static final int TIME = 6;
    private static final int DIR = 7;
    private static final int TEMP = 8;


    //Process number of SMS
    private int smsProcess = 0;
    //Process number of Phone call;
    private int callProcess = 0;

    //In which module
    private int inWhichModule = 0;

    //UI components
    private Button speakBtn;
    private RecyclerView mRecyclerView;

    //RecyclerView Adapter and Data
    private MessageAdapter mAdapter;
    private List<Message> messageList;


    //Speech Recognizer
    private SpeechRecognizer mSpeechRecognizer;
    private boolean isListening;
    private Intent mSpeechRecognizerIntent;
    private boolean isPermitRecordAudio;//permission to record audio
    private SpeechRecognitionListener listener;


    //Text to Speech
    private AliceSpeech aliceSpeech;

    //Text to Command
    private String mText;
    private TextToCommand ttc;


    //SMS util variable
    private String number;


    //toorbar
    private Toolbar toolbar;

    private ImageView groupImage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        speakBtn = findViewById(R.id.speak_btn);
        mRecyclerView = findViewById(R.id.message_rv);
        toolbar = findViewById(R.id.alice_toolbar);

        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_setting){
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
                return true;
            }
        });



        groupImage = findViewById(R.id.group_image);

        //Initial SpeechRecognizer
        initialSpeechRecognizer();





        //initial left message
        initialMessageRecyclerView();
        //Initial Text to Speech

        SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String userName = preferences.getString("name", "Master");
        aliceSpeech = AliceSpeech.GenerateSpeech(this, "" + userName + ", What can I help you?");

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // do something
                //if (!isPermitRecordAudio){
                //    checkAndRequestPermission();
                //}
                //After ask for permission, if confirmed, start listening
                //if (isPermitRecordAudio){
                    startListen();
                //}
            }

        }, 1000);
//        //Initial  NLP
//        mGlobal = (GlobalVariable) getApplication();
//        intialNLP();

        //Initial TextToCommand
        ttc = new TextToCommand();

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission
//                if (!isPermitRecordAudio){
//                    checkAndRequestPermission();
//                }
                //After ask for permission, if confirmed, start listening
                //if (isPermitRecordAudio){
                    startListen();
                //}
            }
        });


    }


    private void startListen(){
        //start listening; change the button
        if (!isListening){
            Log.d("Speech", "Ready for listening");
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            speakBtn.startAnimation(scale);

            Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
            LinearInterpolator interpolator = new LinearInterpolator();
            rotate.setInterpolator(interpolator);
            if (rotate != null){
                groupImage.startAnimation(rotate);
            }
            //speakBtn.setText("Listening");

            isListening = true;

            if(mSpeechRecognizer == null){
                initialSpeechRecognizer();
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        } else{
            mSpeechRecognizer.stopListening();
            groupImage.clearAnimation();
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
            groupImage.clearAnimation();
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

                String text = result.get(0);
                Log.d("Speech", text);
                if (!text.equals("")){
                    addMessage(text, Message.RIGHT_MESSAGE);
                    //aliceSpeech.speak(text, "111111");
                    handleText(text);
                }

            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    private void initialSpeechRecognizer(){
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());

        listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
    }


    @Override
    protected void onDestroy() {
        //override onDestroy, destroy speechRecognizer
        super.onDestroy();
        Log.d("onDestory", "destory");
        if (mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    protected void onPause() {
        if (mSpeechRecognizer != null){
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
        mSpeechRecognizer = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSpeechRecognizer == null){
            initialSpeechRecognizer();
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
        SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String userName = preferences.getString("name", "Master");
        messageList.add(new Message("" + userName + ", What can I help you?", Message.LEFT_MESSAGE));
        mAdapter = new MessageAdapter(LayoutInflater.from(this), messageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(50));
        mRecyclerView.setAdapter(mAdapter);

    }


    private void handleText(String text) {
//        mText = text;
//        getLoaderManager().restartLoader(NLP_LOADER_ID, null, this);
        if (inWhichModule == 0){
            int module = ttc.chooseModule(text);

            switch (module) {
                case SMS:
                    prepareSendSMS(text);
                    break;
                case WEATHER:
                    getWeather(text);
                    break;
                case DATE:
                    speakDate();
                    break;
                case PHONE:
                    prepareCall(text);
                    break;
                case MAP:
                    showMap(text);
                    break;
                case TIME:
                    speakTime();
                    break;
                case DIR:
                    direction(text);
                    break;
                case TEMP:
                    getTemp(text);
                    break;
                case 0:
                    addMessage(FAILED_MESSAGE, Message.LEFT_MESSAGE);
                    break;
            }
        }else if (inWhichModule == SMS){
            switch (smsProcess){
                case 1:
                    getNumber(text);
                    break;
                case 2:
                    sendMessage(text);
                    break;
                case 0:
                    break;
            }
        }else if (inWhichModule == PHONE){
            switch (callProcess){
                case 1:
                    call(text);
                    break;

            }
        }

    }




    ///////////////////////////////////////////SMS functions///////////////////////////
    private void prepareSendSMS(String text){

        inWhichModule = SMS;
        String name = "";
        String[] arr = text.split("\\s+");
        for (int i = arr.length-1; i >= 0; i--){
            if (arr[i].equals("text") || arr[i].equals("to") || arr[i].equals("message")){
                break;
            }else {
                name = arr[i] + " " + name;
            }
        }
        if (name.equals("")){
            addMessage("Who do you want to send?", Message.LEFT_MESSAGE);
            smsProcess = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                    startListen();
                }

            }, 1000);
        }else{
           getNumber(name.trim());
        }

    }

    private void sendMessage(String text){
        if (number != null ){
            String SENT_SMS_ACTION = "SENT_SMS_ACTION";
            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            PendingIntent sendIntent= PendingIntent.getBroadcast(this, 0, sentIntent,
                    0);

            this.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            addMessage("Message sent successfully.", Message.LEFT_MESSAGE);
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            addMessage("Sorry, the message cannot be sent.", Message.LEFT_MESSAGE);
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Log.d("message", "radio off");
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Log.d("message", "null pdu");
                            break;
                    }
                }
            }, new IntentFilter(SENT_SMS_ACTION));
            ContactsUtil.messageTo(number, text, sendIntent);
        }

        inWhichModule = 0;
        number = null;
        smsProcess = 0;
    }

    private void getNumber(String text){
        number = ContactsUtil.getNumberByName(this, text);
        if (number == null || number.equals("")){
            addMessage("Sorry, there is no " + text + " in your contact.", Message.LEFT_MESSAGE);
            inWhichModule = 0;
            number = null;
            smsProcess = 0;
        }else{
            smsProcess = 2;
            addMessage("What would you like to send?", Message.LEFT_MESSAGE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                    startListen();
                }

            }, 1000);

        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////


    private void speakDate(){
        String dateString = DateTime.getDate();
        addMessage(dateString, Message.LEFT_MESSAGE);
    }

    private void speakTime(){
        String timeString = DateTime.getTime();
        addMessage(timeString, Message.LEFT_MESSAGE);
    }

    private void addMessage(String text, int side){
        int count = messageList.size();
        messageList.add(new Message(text, side));
        mAdapter.notifyItemInserted(count);
        mRecyclerView.scrollToPosition(count);

        if (side == Message.LEFT_MESSAGE){
            aliceSpeech.speak(text, LEFT_SPEAK_ID);
        }
    }

    private void prepareCall(String text){
        inWhichModule = PHONE;
        String name = "";
        String[] arr = text.split("\\s+");
        for (int i = arr.length-1; i >= 0; i--){
            if (arr[i].equals("call") || arr[i].equals("to")){
                break;
            }else {
                name = arr[i] + " " + name;
            }
        }
        if (name.equals("")){
            addMessage("Who do you want to call?", Message.LEFT_MESSAGE);
            callProcess = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                    startListen();
                }

            }, 1000);
        }else{
            call(name.trim());
        }
    }

    private void call(String name){
        number = ContactsUtil.getNumberByName(this, name);
        if (number == null || number.equals("")){
            addMessage("Sorry, there is no " + name + " in your contact.", Message.LEFT_MESSAGE);
            inWhichModule = 0;
            number = null;
            callProcess = 0;
            return;
        }
        inWhichModule = 0;
        callProcess = 0;
        addMessage("Calling " + name, Message.LEFT_MESSAGE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // do something
                ContactsUtil.call(MainActivity.this, number);
            }

        }, 1000);


    }

    private void showMap(String text){
        Log.d("location", text);
        if (text.equals("where am I")){
            addMessage("Sorry, I can't do this for you at this point. :(", Message.LEFT_MESSAGE);
            return;
        }
        String[] arr = text.split("\\s+");
        String location = "";
        for (int i = arr.length-1; i >= 0; i--){
            if (arr[i].equals("of") || arr[i].equals("is") || arr[i].equals("location") || arr[i].equals("where")){
                break;
            }else{
                location = arr[i] + " " + location;
            }
        }
        if (location.equals("")){
            addMessage(FAILED_MESSAGE, Message.LEFT_MESSAGE);
        }else{
            addMessage("Here is the location of " + location, Message.LEFT_MESSAGE);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                }

            }, 1000);
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            this.startActivity(mapIntent);

        }
    }

    private void direction(String text){
        String[] arr = text.split("\\s+");
        String location = "";
        for (int i = arr.length-1; i >= 0; i--){
            if (arr[i].equals("to") || arr[i].equals("navigation") || arr[i].equals("direction") || arr[i].equals("directions")){
                break;
            }else{
                location = arr[i] + " " + location;
            }
        }
        if (location.equals("")){
            addMessage(FAILED_MESSAGE, Message.LEFT_MESSAGE);
        }else{
            addMessage("Starting navigation to " + location, Message.LEFT_MESSAGE);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                }

            }, 1000);
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            this.startActivity(mapIntent);

        }
    }

    private void getWeather(String text){
        String[] arr = text.split("\\s+");
        String location = "";
        for (int i = arr.length-1; i >= 0; i--){
            if (arr[i].equals("in") || arr[i].equals("of") || arr[i].equals("at") || arr[i].equals("weather")){
                break;
            }else{
                location = arr[i] + " " + location;
            }
        }
        if (location.equals("")){
            location = "San Francisco";
        }
        new AsyncWeatherTask().execute(location);

    }

    private void getTemp(String text){
        String[] arr = text.split("\\s+");
        String location = "";
        for (int i = arr.length-1; i >= 0; i--){
            if (arr[i].equals("in") || arr[i].equals("of") || arr[i].equals("at") || arr[i].equals("temperature")){
                break;
            }else{
                location = arr[i] + " " + location;
            }
        }
        if (location.equals("")){
            location = "San Francisco";
        }
        new AsyncTempTask().execute(location);
    }

    class AsyncWeatherTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... strings) {
            return WeatherUtil.getJSON(MainActivity.this, strings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            String weatherString = pasrJsonToWeather(jsonObject);
            addMessage(weatherString, Message.LEFT_MESSAGE);
            Log.d("weather", weatherString);

        }
    }

    class AsyncTempTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... strings) {
            return WeatherUtil.getJSON(MainActivity.this, strings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            String weatherString = parseJsonToTemp(jsonObject);
            addMessage(weatherString, Message.LEFT_MESSAGE);
            Log.d("weather", weatherString);

        }
    }

    private String parseJsonToTemp(JSONObject jsonObject){
        StringBuilder weatherString = new StringBuilder();

        try{
            String location = jsonObject.getString("name");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject object = weatherArray.getJSONObject(0);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            String temp = mainObject.getString("temp");
            if (location.equals("San Francisco")){
                weatherString.append(" The current temperature is " + temp + " degree.");
            }else{
                weatherString.append(" The current temperature in " + location + " is " + temp + " degree.");
            }

        }catch (Exception e){
            weatherString.append("Sorry, I can't get weather now");
        }
        return weatherString.toString();
    }

    private String pasrJsonToWeather(JSONObject jsonObject){
        StringBuilder weatherString = new StringBuilder();

        try{
            String location = jsonObject.getString("name");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject object = weatherArray.getJSONObject(0);
            String description = object.getString("description");
            JSONObject mainObject = jsonObject.getJSONObject("main");
            String temp = mainObject.getString("temp");
            if (location.equals("San Francisco")){
                weatherString.append("Today the weather is " + description + ",");
            }else{
                weatherString.append("Today the weather in " + location + " is " + description + ",");
            }

            weatherString.append(" and the current temperature is " + temp + " degree.");
        }catch (Exception e){
            weatherString.append("Sorry, I can't get weather now");
        }
        return weatherString.toString();

    }

}
