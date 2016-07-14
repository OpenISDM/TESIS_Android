package sinica.earth.tesis;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class JsonFunctions {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static JSONObject sJsonObject = null;

    private static JSONArray sJSONArray = null;

    /**
     * This utility function is to get the ID content from TESIS server.
     * @param url example
     *            http://tesis.earth.sinica.edu.tw/common/php/processdatamobile.php
     *            ?firstid=991&secondid=992
     * @return a JSONArray
     */
    public static JSONObject getJsonObjectFromURL(String url) {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = CLIENT.newCall(request).execute();
                String jsonData = response.body().string();
                sJsonObject = new JSONObject(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("Ex-JSONObject", e.toString());
        }
        return sJsonObject;
    }

    /**
     * This utility function is to get the IDs from TESIS server.
     * @param url example:
     *            http://tesis.earth.sinica.edu.tw/common/php/getid.php
     *            ?start=2016-04-12&end=2016-07-12
     * @return a JSONObject
     */
    public static JSONArray getJsonArrayFromURL(String url) {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = CLIENT.newCall(request).execute();
                String jsonData = response.body().string();
                sJSONArray = new JSONArray(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("Ex-JSONArray", e.toString());
        }
        return sJSONArray;
    }


}
