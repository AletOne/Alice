package com.example.wang.alice.TTC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wang on 3/11/18.
 */

public class DateTime {

    public static String getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat( "EEE, d MMM yyyy ");
        String formattedDate = sdf.format(currentTime);
        return "It's " + formattedDate;
    }

    public static String getTime(){
        final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
        Date instant = new Date(CURRENT_TIME_MILLIS);
        SimpleDateFormat sdf = new SimpleDateFormat( "hh:mm a" );
        String time = sdf.format( instant );
        return "It's " + time;
    }
}

