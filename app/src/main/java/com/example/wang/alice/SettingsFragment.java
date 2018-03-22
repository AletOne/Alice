package com.example.wang.alice;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by Wang on 3/14/18.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private static final String WEATHER_KEY = "weather";
    private static final String CALL_KEY = "call";
    private static final String TEXT_KEY = "text";
    private static final String REMIDER_KEY = "reminder";
    private static final String NOTES_KEY = "notes";
    private static final String UBER_KEY = "uber";



    private Preference weatherPreference;
    private Preference phonePreference;
    private Preference textPreference;
    private Preference reminderPreference;
    private Preference notesPreference;
    private Preference uberPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        weatherPreference = findPreference("weather");
        weatherPreference.setOnPreferenceClickListener(this);

        phonePreference = findPreference("call");
        phonePreference.setOnPreferenceClickListener(this);

        textPreference = findPreference("text");
        textPreference.setOnPreferenceClickListener(this);

        reminderPreference = findPreference("reminder");
        reminderPreference.setOnPreferenceClickListener(this);

        notesPreference = findPreference("notes");
        notesPreference.setOnPreferenceClickListener(this);

        uberPreference = findPreference("uber");
        uberPreference.setOnPreferenceClickListener(this);

    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key){
            case WEATHER_KEY:
                Intent weatherIntent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(weatherIntent);
                break;
            case CALL_KEY:
                Intent callIntent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(callIntent);
                break;
            case TEXT_KEY:
                Intent textIntent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(textIntent);
                break;
            case REMIDER_KEY:

                break;
            case NOTES_KEY:
                break;
            case UBER_KEY:
                break;
        }

//        startActivity(intent);
        return true;
    }
}
