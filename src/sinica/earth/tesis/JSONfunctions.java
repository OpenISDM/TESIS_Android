package sinica.earth.tesis;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class JsonFunctions {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private static JSONObject sJsonObject = null;

    private static JSONArray sJSONArray = null;

    protected static boolean JSONObjectisLoad = false;

    public static JSONObject getJSONfromURL(String url) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;
        JSONObjectisLoad = false;
        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
            JSONObjectisLoad = false;
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
            JSONObjectisLoad = false;
        }

        try {

            jArray = new JSONObject(result);
            JSONObjectisLoad = true;
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            JSONObjectisLoad = false;
        }
        if (JSONObjectisLoad) {
            return jArray;
        } else {
            return null;
        }
    }

    /**
     * This utility function is to get the IDs from TESIS server.
     *
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
                Log.d("Response", "64");
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
