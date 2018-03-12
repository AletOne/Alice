package com.example.wang.alice.TTC;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;

import com.example.wang.alice.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Wang on 3/11/18.
 */

public class WeatherUtil {
    private static final String API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
//    public static  getWatherInfo(String location){
//        HttpURLConnection conn = null;
//        try {
//            // 利用string url构建URL对象
//            URL mURL = new URL(API + location);
//            conn = (HttpURLConnection) mURL.openConnection();
//
//            conn.setRequestMethod("GET");
//            conn.setReadTimeout(5000);
//            conn.setConnectTimeout(10000);
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == 200) {
//
//                InputStream is = conn.getInputStream();
//                String response = getStringFromInputStream(is);
//                return response;
//            } else {
//                throw new NetworkErrorException("response status is "+responseCode);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        return null;
//    }

    public static JSONObject getJSON(Context context, String city) {
        Log.d("network", "getJSON: ");
        try {
            URL url = new URL(String.format(API, city));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            Log.d("network", json.toString());
            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            Log.d("network", data.getInt("cod") + "");
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            Log.d("error", "aa",e);
            return null;
        }
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }


}
