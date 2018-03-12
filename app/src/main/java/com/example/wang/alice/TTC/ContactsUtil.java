package com.example.wang.alice.TTC;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Wang on 3/10/18.
 */

public class ContactsUtil {



    public static String getNumberByName(Context context, String name){
        String number = null;
        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                new String[]{name}, null);

        if (cursor != null){
            if (cursor.moveToNext()){
                number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }
        return number;
    }

    public static void messageTo(String number, String text, PendingIntent sendIntent){



        Log.d("message", text);
        SmsManager smsManager = SmsManager.getDefault();
        List<String> dividedContents = smsManager.divideMessage(text);
        for (String t : dividedContents){
            Log.d("message", t);
            smsManager.sendTextMessage(number, null, t, sendIntent, null);
        }

    }

    public static void call(Context context, String number){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }
}
