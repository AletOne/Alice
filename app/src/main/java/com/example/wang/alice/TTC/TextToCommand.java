package com.example.wang.alice.TTC;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wang on 3/10/18.
 */

public class TextToCommand {

    private static final int SMS = 1;
    private static final int WEATHER = 2;
    private static final int DATE = 3;
    private static final int PHONE = 4;
    private static final int MAP = 5;
    private static final int DIR = 7;
    private static final int TIME = 6;
    private static final int TEMP = 8;

    private Map<String, Integer> map;

    public TextToCommand(){
        map = new HashMap<>();
        map.put("call", PHONE);
        map.put("phone", PHONE);
        map.put("date", DATE);
        map.put("time", TIME);
        map.put("direction", DIR);
        map.put("directions", DIR);
        map.put("navigation", DIR);
        map.put("navigations", DIR);
        map.put("where", MAP);
        map.put("location", MAP);
        map.put("weather", WEATHER);
        map.put("text", SMS);
        map.put("message", SMS);
        map.put("day", DATE);
        map.put("temperature", TEMP);

    }

    public int chooseModule(String text){
        String[] arr = text.split("\\s+");
        int module = 0;
        for (String word : arr){
            if (map.containsKey(word)){
                module = map.get(word);
                break;
            }
        }

        return module;
    }


}
