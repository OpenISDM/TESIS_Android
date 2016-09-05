package sinica.earth.tesis;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sinica.earth.tesis.rest.RestApiClient;
import sinica.earth.tesis.rest.service.TESISApiService;

public class HomeActivity extends AppCompatActivity {

    final int CURRENT_LOCATION_UPDATE = ConstantVariables.CURRENT_LOCATION_UPDATE;

    final int CHECK_UPDATE = ConstantVariables.CHECK_UPDATE;

    String generalFilename = ConstantVariables.GENERAL_FILE_NAME;

    TESISApiService mRestApiClient = new RestApiClient().getTESISApiService();

    public HomeActivity homeActivity;

    protected Location mCurrentLocation;

    GPSTracker mGpsTracker;

    ArrayList<HashMap<String, String>> mGeneralEarthquakeList;
    ArrayList<HashMap<String, String>> mDisplayedEarthquakeList;
    ArrayList<HashMap<String, String>> mManualEarthquakeList;

    ArrayList<String> mEarthquakeIds;

    EarthquakeEvents mEarthquakeList = null;

    boolean isCheckingUpdate = true;

    FrameLayout frameLayout;

    ListView mListView;

    Handler handler;

    // 1017 add GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "TESIS.v3.2014.10.17";
    private static final String TAG = "GCMRelated";
    GoogleCloudMessaging gcm;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeActivity = this;

        mGpsTracker = new GPSTracker(this);

        if (mGpsTracker.canGetLocation) {
            mCurrentLocation = mGpsTracker.getLocation();
        }

        String startDate = myDate.getFrom().DatetoString();
        String endDate = myDate.getTo().DatetoString();

        updateEarthquakeIds(startDate, endDate);

    }

    @Override
    protected void onResume() {
        Log.d("Activity", "In HomeActivity: onResume");
        Log.d("MemoryTag",
                "In SummaryActivity onResume : memory less:"
                        + Integer
                        .toString(((ActivityManager) getSystemService(ACTIVITY_SERVICE))
                                .getMemoryClass()));
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Activity", "In HomeActivity: onPause:");
        // mGpsTracker.stopUsingGPS();
        // mGpsTracker = null;
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Activity", "In HomeActivity: onStop: write file.");
        if (mGeneralEarthquakeList != null) {
            for (int i = 0; i < mGeneralEarthquakeList.size(); ++i) {
                mGeneralEarthquakeList.get(i).put("isNew", "false");
            }
            ConstantVariables.writeEQToInternalFile(homeActivity,
                    mGeneralEarthquakeList, generalFilename);
        }
        if (handler != null) {
            handler.removeMessages(CURRENT_LOCATION_UPDATE);
        }
        isCheckingUpdate = false;
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
            mGpsTracker = null;
        }

        super.onStop();
    }


    public void updateEarthquakeIds(String startDate, String endDate) {

        Log.i("api-getId", startDate);
        Log.i("api-getId", endDate);

        Call<ArrayList<String>> call = mRestApiClient.getId(startDate, endDate);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {

                    mEarthquakeIds = response.body();

                    downloadEarthquakeEventsData();

                    startGCM();

                } else {
                    Log.e("HTTP Status Code", response.code() + response.message());
                    showAlertDialog("無法下載地震事件項目", "下載失敗", true);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    public void downloadEarthquakeEventsData() {

        setDownloadLayout();

        Integer count = mEarthquakeIds.size();

        String firstId = mEarthquakeIds.get(0);
        String endId = mEarthquakeIds.get(count - 1);

        Log.i("api-getData", firstId);
        Log.i("api-getData", endId);

        Call<EarthquakeEvents> call = mRestApiClient.getEarthquakeData(firstId, endId);

        call.enqueue(new Callback<EarthquakeEvents>() {
            @Override
            public void onResponse(Call<EarthquakeEvents> call, Response<EarthquakeEvents> response) {
                if (response.isSuccessful()) {

                    mEarthquakeList = response.body();

                    Log.i("api-getEarthquakeData", response.raw().toString());

                    mGeneralEarthquakeList =
                            setEQlistLocation(mEarthquakeList.earthquakes);

                    mDisplayedEarthquakeList =
                            setDisplayedEarthquakeList(mGeneralEarthquakeList, false);

                    adaptEQList(mDisplayedEarthquakeList);
                    if (handler == null) {
                        setHandler();
                    }

                    Message m = new Message();
                    // 定義 Message的代號，handler才知道這個號碼是不是自己該處理的。
                    m.what = CHECK_UPDATE;
                    handler.sendMessage(m);


                } else {
                    Log.e("HTTP Status Code", response.code() + response.message());
                    showAlertDialog("無法下載地震事件資料", "下載失敗", true);
                }
            }

            @Override
            public void onFailure(Call<EarthquakeEvents> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void showAlertDialog(String errorMessage, String errorTitle,
                                 boolean isFinishActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(homeActivity);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setTitle(errorTitle);
        alertDialog.setMessage(errorMessage);
        if (isFinishActivity) {
            alertDialog.setPositiveButton("確定", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    homeActivity.finish();
                }
            });
        } else {
            alertDialog.setPositiveButton("確定", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        alertDialog.show();
    }

    protected static boolean isConnected(Context ctx) {
        NetworkInfo info = ((ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }
        return !info.isRoaming();
    }

    int loadingTextIndex = 0;
    TextSwitcher textSwitcher;

    private void setDownloadLayout() {

        frameLayout = (FrameLayout) homeActivity.findViewById(R.id.container);
        frameLayout.removeAllViews();

        View child = getLayoutInflater().inflate(R.layout.progress_bar, null);

        textSwitcher = (TextSwitcher) child.findViewById(R.id.textSwitcherOnLoadingPage);
        textSwitcher.setInAnimation(getApplicationContext(), R.anim.abc_fade_in);
        textSwitcher.setOutAnimation(getApplicationContext(), R.anim.abc_fade_out);

        TextView textView1 = new TextView(getApplicationContext());
        textView1.setTextAppearance(getApplicationContext(), R.style.GenericProgresstextColor1);
        textView1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        TextView textView2 = new TextView(getApplicationContext());
        textView2.setTextAppearance(getApplicationContext(), R.style.GenericProgresstextColor2);
        textView2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        textSwitcher.addView(textView1);
        textSwitcher.addView(textView2);

        int count = loadingTextIndex % ConstantVariables.LOADING_TEXT.length;
        textSwitcher.setText(ConstantVariables.LOADING_TEXT[count]);

        frameLayout.addView(child);

        Timer timer = new Timer("myTimer");
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                homeActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int count = (loadingTextIndex++) % ConstantVariables.LOADING_TEXT.length;
                        textSwitcher.setText(ConstantVariables.LOADING_TEXT[count]);
                    }
                });
            }
        }, 0, 4000);
    }

    void downloadManualEQList(myDate from, myDate to) {
        homeActivity.getSupportActionBar().setSubtitle(from.DatetoString() + "~" + to.DatetoString());
        setDownloadLayout();

//        if (taskDownloadManualAsyncTask != null) {
//            taskDownloadManualAsyncTask.cancel(true);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            taskDownloadManualAsyncTask = new DownloadEQListManualTask()
//                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, from, to);
//        else
//            taskDownloadManualAsyncTask = new DownloadEQListManualTask()
//                    .execute(from, to);

        TESISApiService restApiClient = new RestApiClient().getTESISApiService();

        Call<EarthquakeEvents> call = restApiClient.getEarthquakeData(from.DatetoString(), to.DatetoString());

        call.enqueue(new Callback<EarthquakeEvents>() {
            @Override
            public void onResponse(Call<EarthquakeEvents> call, Response<EarthquakeEvents> response) {
                if (response.isSuccessful()) {

                    mEarthquakeList = response.body();

                    Log.i("api-getEarthquakeData", response.raw().toString());
                    Log.i("api-getEarthquakeData", mEarthquakeList.earthquakes.get(0).get("mrt"));

                    mManualEarthquakeList = setEQlistLocation(mEarthquakeList.earthquakes);

                    mDisplayedEarthquakeList = setDisplayedEarthquakeList(
                            mManualEarthquakeList, true);
                    adaptEQList(mDisplayedEarthquakeList);
                    setHandler();

                } else {
                    Log.e("HTTP Status Code", response.message());
                    showAlertDialog("日期區間無地震發生或資料下載失敗", "無地震資料", false);
                }
            }

            @Override
            public void onFailure(Call<EarthquakeEvents> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    protected void adaptEQList(ArrayList<HashMap<String, String>> mList) {

        frameLayout = (FrameLayout) homeActivity.findViewById(R.id.container);
        frameLayout.removeAllViews();

        View child = getLayoutInflater().inflate(R.layout.earthquake_list, null);
        frameLayout.addView(child);

        mListView = (ListView) homeActivity.findViewById(R.id.listView1);

        String[] eqListFrom = new String[] {
                "ML",
                "Date",
                "distance",
                "Location"};

        int[] eqListTo = new int[] {
                R.id.eqlist_ml,
                R.id.eqlist_date,
                R.id.eqlist_distance,
                R.id.eqlist_location};

        myEarthquakeListAdapter mEarthquakeListAdapter = new myEarthquakeListAdapter(
                homeActivity,
                mList,
                R.layout.drawer_list_item,
                eqListFrom,
                eqListTo);

        mListView.setAdapter(mEarthquakeListAdapter);
        mListView.setOnItemClickListener(new myOnItemClickListener());
    }

    private ArrayList<HashMap<String, String>> setDisplayedEarthquakeList(
            ArrayList<HashMap<String, String>> mList, boolean isManual) {

        ArrayList<HashMap<String, String>> mFilteredList = new ArrayList<HashMap<String, String>>();

        HashMap<String, Integer> settingHashMap = null;

        File fileEQ = getFileStreamPath(settingPreferenceFilename);

        if (fileEQ.exists()) {
            try {
                FileInputStream fis;
                fis = openFileInput(settingPreferenceFilename);
                ObjectInputStream ois = new ObjectInputStream(fis);
                settingHashMap = (HashMap<String, Integer>) ois.readObject();
                ois.close();
                fis.close();

                double minML = Double.parseDouble(SETTING_PREFERENCE_ML[settingHashMap.get("minML")]);
                double maxML = Double.parseDouble(SETTING_PREFERENCE_ML[settingHashMap.get("maxML")]);
                double minDeep = Double.parseDouble(SETTING_PREFERENCE_DEPTH[settingHashMap.get("minDeep")]);
                double maxDeep = Double.parseDouble(SETTING_PREFERENCE_DEPTH[settingHashMap.get("maxDeep")]);

                int inDistance = Integer.parseInt(SETTING_PREFERENCE_DISTANCE[settingHashMap.get("inDistance")]);
                int inDate = settingHashMap.get("inDate");

                for (int i = 0; i < mList.size(); ++i) {
                    if (Double.parseDouble(mList.get(i).get("ML")) >= minML
                            && Double.parseDouble(mList.get(i).get("ML")) <= maxML
                            && Double.parseDouble(mList.get(i).get("depth")) >= minDeep
                            && Double.parseDouble(mList.get(i).get("depth")) <= maxDeep
                            && Double.parseDouble(mList.get(i).get(
                            "distance_value")) <= inDistance) {

                        Calendar today = Calendar.getInstance();
                        String date_of_eqString = mList.get(i).get("Date");
                        DateTimeFormatter dateStringFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                        DateTime todayDateTime = new DateTime(today.getTimeInMillis());
                        DateTime eqDateTime = dateStringFormatter.parseDateTime(date_of_eqString);

                        int days = Days.daysBetween(
                                new LocalDate(eqDateTime),
                                new LocalDate(todayDateTime)).getDays();
                        int months = Months.monthsBetween(
                                new LocalDate(eqDateTime),
                                new LocalDate(todayDateTime)).getMonths();

                        if (!isManual) {
                            switch (inDate) {
                                case SETTING_DATE_ALL:
                                    mFilteredList.add(mList.get(i));
                                    getSupportActionBar().setSubtitle("過去三個月顯著地震列表");
                                    break;
                                case SETTING_DATE_1_DAY:
                                    if (days <= 1)
                                        mFilteredList.add(mList.get(i));
                                    getSupportActionBar().setSubtitle("過去一日內顯著地震列表");
                                    break;
                                case SETTING_DATE_1_WEEK:
                                    if (days <= 7)
                                        mFilteredList.add(mList.get(i));
                                    getSupportActionBar().setSubtitle("過去一週內顯著地震列表");
                                    break;
                                case SETTING_DATE_1_MONTH:
                                    if (months <= 1)
                                        mFilteredList.add(mList.get(i));
                                    getSupportActionBar().setSubtitle("過去一個月內顯著地震列表");
                                    break;
                                case SETTING_DATE_2_MONTH:
                                    if (months <= 2)
                                        mFilteredList.add(mList.get(i));
                                    getSupportActionBar().setSubtitle("過去兩個月內顯著地震列表");
                                    break;
                                case SETTING_DATE_3_MONTH:
                                    if (months <= 3)
                                        mFilteredList.add(mList.get(i));
                                    getSupportActionBar().setSubtitle("過去三個月內顯著地震列表");
                                    break;
                                default:
                                    mFilteredList.add(mList.get(i));
                                    break;
                            }
                        } else {
                            mFilteredList.add(mList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(homeActivity, "篩選失敗",
                // Toast.LENGTH_SHORT).show();


                ConstantVariables.saveSetting(
                        homeActivity, 0,
                        SETTING_PREFERENCE_ML.length - 1, 0,
                        SETTING_PREFERENCE_DEPTH.length - 1,
                        SETTING_PREFERENCE_DISTANCE.length - 1,
                        SETTING_DATE_ALL,
                        ConstantVariables.SETTING_NOTIFICATION_STATE_ON);

                mFilteredList = mList;
            }

            if (mFilteredList.size() == 0) {
                if (isManual) {
                    Toast.makeText(homeActivity, "沒有符合條件的地震，將重設地震篩選條件",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(homeActivity, "沒有符合條件的地震，將重設地震篩選條件",
                            Toast.LENGTH_SHORT).show();
                }

                ConstantVariables.saveSetting(
                        homeActivity, 0,
                        SETTING_PREFERENCE_ML.length - 1, 0,
                        SETTING_PREFERENCE_DEPTH.length - 1,
                        SETTING_PREFERENCE_DISTANCE.length - 1,
                        SETTING_DATE_ALL,
                        ConstantVariables.SETTING_NOTIFICATION_STATE_ON);

                mFilteredList = mList;

            }
        } else {

            ConstantVariables.saveSetting(
                    homeActivity, 0,
                    SETTING_PREFERENCE_ML.length - 1, 0,
                    SETTING_PREFERENCE_DEPTH.length - 1,
                    SETTING_PREFERENCE_DISTANCE.length - 1,
                    SETTING_DATE_ALL,
                    ConstantVariables.SETTING_NOTIFICATION_STATE_ON);


            mFilteredList = mList;
        }
        return mFilteredList;
    }

    String settingPreferenceFilename = ConstantVariables.SETTING_PREFERENCE_FILE_NAME;

    // ML 0-100 => 0.0-10.0
    // Deep 0-350
    // Distance 0 => all
    // Date 0 => all, others see flags "SETTING_DATE_*" below
    String SETTING_PREFERENCE_ML[] = ConstantVariables.SETTING_PREFERENCE_ML;
    String SETTING_PREFERENCE_DEPTH[] = ConstantVariables.SETTING_PREFERENCE_DEPTH;
    String SETTING_PREFERENCE_DISTANCE[] = ConstantVariables.SETTING_PREFERENCE_DISTANCE;
    String SETTING_PREFERENCE_DATE[] = ConstantVariables.SETTING_PREFERENCE_DATE;
    final int SETTING_DATE_ALL = ConstantVariables.SETTING_DATE_ALL;
    final int SETTING_DATE_1_DAY = ConstantVariables.SETTING_DATE_1_DAY;
    final int SETTING_DATE_1_WEEK = ConstantVariables.SETTING_DATE_1_WEEK;
    final int SETTING_DATE_1_MONTH = ConstantVariables.SETTING_DATE_1_MONTH;
    final int SETTING_DATE_2_MONTH = ConstantVariables.SETTING_DATE_2_MONTH;
    final int SETTING_DATE_3_MONTH = ConstantVariables.SETTING_DATE_3_MONTH;

    protected ArrayList<HashMap<String, String>> setEQlistLocation(
            ArrayList<HashMap<String, String>> mList) {

        if (mCurrentLocation == null) {
            for (int i = 0; i < mList.size(); ++i) {
                mList.get(i).put("distance", "未連上網路，無法估計震央距離現在位置");
                mList.get(i).put("distance_value", "" + 0);
            }
        } else {
            for (int i = 0; i < mList.size(); ++i) {

                Location location = new Location("start");
                double startLat = Double.parseDouble(mList.get(i).get("Latitude"));
                double startLng = Double.parseDouble(mList.get(i).get("Longitude"));
                location.setLatitude(startLat);
                location.setLongitude(startLng);

                float distance = location.distanceTo(mCurrentLocation);

                // set direction here
                HashMap<String, String> eqHashMap = mList.get(i);
                double dx = location.getLongitude() - mCurrentLocation.getLongitude();
                double dy = location.getLatitude() - mCurrentLocation.getLatitude();
                double theta = Math.atan2(dy, dx);

                // Log.d(mTag, "theta:" + theta);
                if (theta < 0) {
                    theta += 2 * Math.PI;
                }
                if (theta > 2 * Math.PI * 31 / 32
                        || theta <= 2 * Math.PI * 1 / 32) {
                    eqHashMap.put("direction", "東方");
                } else if (theta > 2 * Math.PI * 1 / 32
                        && theta <= 2 * Math.PI * 3 / 32) {
                    eqHashMap.put("direction", "東北東方");
                } else if (theta > 2 * Math.PI * 3 / 32
                        && theta <= 2 * Math.PI * 5 / 32) {
                    eqHashMap.put("direction", "東北方");
                } else if (theta > 2 * Math.PI * 5 / 32
                        && theta <= 2 * Math.PI * 7 / 32) {
                    eqHashMap.put("direction", "北北東方");
                } else if (theta > 2 * Math.PI * 7 / 32
                        && theta <= 2 * Math.PI * 9 / 32) {
                    eqHashMap.put("direction", "北方");
                } else if (theta > 2 * Math.PI * 9 / 32
                        && theta <= 2 * Math.PI * 11 / 32) {
                    eqHashMap.put("direction", "北北西方");
                } else if (theta > 2 * Math.PI * 11 / 32
                        && theta <= 2 * Math.PI * 13 / 32) {
                    eqHashMap.put("direction", "西北方");
                } else if (theta > 2 * Math.PI * 13 / 32
                        && theta <= 2 * Math.PI * 15 / 32) {
                    eqHashMap.put("direction", "西北西方");
                } else if (theta > 2 * Math.PI * 15 / 32
                        && theta <= 2 * Math.PI * 17 / 32) {
                    eqHashMap.put("direction", "西方");
                } else if (theta > 2 * Math.PI * 17 / 32
                        && theta <= 2 * Math.PI * 19 / 32) {
                    eqHashMap.put("direction", "西南西方");
                } else if (theta > 2 * Math.PI * 19 / 32
                        && theta <= 2 * Math.PI * 21 / 32) {
                    eqHashMap.put("direction", "西南方");
                } else if (theta > 2 * Math.PI * 21 / 32
                        && theta <= 2 * Math.PI * 23 / 32) {
                    eqHashMap.put("direction", "南南西方");
                } else if (theta > 2 * Math.PI * 23 / 32
                        && theta <= 2 * Math.PI * 25 / 32) {
                    eqHashMap.put("direction", "南方");
                } else if (theta > 2 * Math.PI * 25 / 32
                        && theta <= 2 * Math.PI * 27 / 32) {
                    eqHashMap.put("direction", "南南東方");
                } else if (theta > 2 * Math.PI * 27 / 32
                        && theta <= 2 * Math.PI * 29 / 32) {
                    eqHashMap.put("direction", "東南方");
                } else if (theta > 2 * Math.PI * 29 / 32
                        && theta <= 2 * Math.PI * 31 / 32) {
                    eqHashMap.put("direction", "東南東方");
                }

                eqHashMap.put("distance_value", "" + distance / 1000);

                eqHashMap.put("distance",
                        "震央位於現在位置 " + eqHashMap.get("direction")
                                + (int) (distance / 1000) + "公里處");
            }
        }
        return mList;
    }

    private void setHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    // 當收到的Message的代號為我們剛剛訂的代號就做下面的動作。

                    case CURRENT_LOCATION_UPDATE:
                        // call back here, 更改current Location
                        if (mGpsTracker != null) {
                            mCurrentLocation = mGpsTracker.getLocation();
                            if (mManualEarthquakeList != null) {
                                mDisplayedEarthquakeList = setEQlistLocation(mManualEarthquakeList);
                            } else {
                                mDisplayedEarthquakeList = setEQlistLocation(mGeneralEarthquakeList);
                                ConstantVariables.writeEQToInternalFile(
                                        homeActivity,
                                        mGeneralEarthquakeList,
                                        generalFilename);
                            }
                        }
                        break;
                    case CHECK_UPDATE:
                        adaptEQList(mDisplayedEarthquakeList);
                        // ConstantVariables.writeEQToInternalFile(homeActivity,
                        // mGeneralEarthquakeList, generalFilename);
                        break;
                }
                super.handleMessage(msg);
            }
        };
        if (mGpsTracker == null) {
            mGpsTracker = new GPSTracker(this);
        }
        mGpsTracker.setHandler(handler);
    }

    protected class myOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                long arg3) {
            if (isConnected(getApplicationContext())) {
                Intent intent = new Intent(homeActivity, summaryActivity.class);
                HashMap<String, String> earthquakeInfo = mDisplayedEarthquakeList
                        .get(arg2);
                intent.putExtra("earthquakeInfo", earthquakeInfo);
                startActivity(intent);

            } else {
                showAlertDialog("尚未連上網路，無法查看詳細資料", "無網路", false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    final int SETTING = 1001;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(homeActivity, SettingActivity.class);
                startActivityForResult(intent, SETTING);
                break;
            case R.id.reload_setting:
                downloadEarthquakeEventsData();
                break;
            case R.id.datePicker_setting:
                if (isConnected(getApplicationContext())) {
                    MyDoubleDatePicker myDatePicker = new MyDoubleDatePicker(this);
                    myDatePicker.show();
                } else {
                    showAlertDialog("尚未連上網路，無法使用此功能", "無網路", false);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETTING:
                // 0825 comming back from setting
                // setGPSTrakerIfNeed();
                setHandler();
                if (mManualEarthquakeList != null) {
                    mDisplayedEarthquakeList = setDisplayedEarthquakeList(
                            mManualEarthquakeList, true);
                } else {
                    mDisplayedEarthquakeList = setDisplayedEarthquakeList(
                            mGeneralEarthquakeList, false);
                }
                adaptEQList(mDisplayedEarthquakeList);
                break;
            default:
                break;
        }

    }

    /***************** GCM *****************/

    /**
     * 1017 add GCM GCM main function
     */
    private void startGCM() {
        Log.i("startGCM", "startGCM");
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = getRegistrationId(getApplicationContext());
            if (!regid.isEmpty()) {
                Log.d(TAG, "get GCM ID:" + regid);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    new GcmRegisterID(getApplicationContext(), gcm,
                            getAppVersion(getApplicationContext()))
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    new GcmRegisterID(getApplicationContext(), gcm,
                            getAppVersion(getApplicationContext())).execute();
            }
        }
    }

    /**
     * 1017 add GCM Check the device to make sure it has the Google Play
     * Services APK. If it doesn't, display a dialog that allows users to
     * download the APK from the Google Play Store or enable it in the device's
     * system settings.
     */

    protected boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * 1017 add GCM Gets the current registration ID for application on GCM
     * service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    protected String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        Log.i(TAG, "registeredVersion version :" + registeredVersion);
        int currentVersion = getAppVersion(getApplicationContext());
        Log.i(TAG, "currentVersion version :" + currentVersion);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * 1017 add GCM
     *
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the regID in your app is up to you.
        return getSharedPreferences(HomeActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * 1017 add GCM
     *
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
