package sinica.earth.tesis;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class summaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    setupGoogleMap mSetupGoogleMap;

    protected HashMap<String, String> eqHashMap;

    protected PopupWindow mPopupWindow;

    boolean isPopup = false;

    public summaryActivity mSummaryActivity;

    MapOverlay mapOverlay;

    final String mTag = "myTag";

    final int HALF = 0, FULL_MAP = 1, FULL_CONTENT = 2;

    int state;

    WebView webView;

    GPSTracker mGpsTracker;

    Location mCurrentLocation;

    Handler handler;

    ImageButton imageButtonUp, imageButtonDown;

    Bitmap upBitmap, upBitmap_press, downBitmap, downBitmap_press;

    LinearLayout linearLayoutSummary;

    int contentHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        mSummaryActivity = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        @SuppressWarnings("unchecked")
        HashMap<String, String> earthquakeInfo = (HashMap<String, String>) intent
                .getSerializableExtra("earthquakeInfo");

        eqHashMap = earthquakeInfo;

        mSummaryActivity.getSupportActionBar().setTitle(
                "ML" + earthquakeInfo.get("ML") +
                "，深度" + earthquakeInfo.get("Depth") + "km");

        mSummaryActivity.getSupportActionBar().setSubtitle(
                earthquakeInfo.get("Date") + " " + earthquakeInfo.get("Time"));

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View popupWindow = layoutInflater
                .inflate(R.layout.popup_map_menu, null);

        mPopupWindow = new PopupWindow(
                popupWindow,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        mPopupWindow.setContentView(popupWindow);

        mSetupGoogleMap = new setupGoogleMap(this, mPopupWindow);

        ImageButton mapMenuButton = (ImageButton) findViewById(R.id.imageButtonMapMenu);

        mapMenuButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPopup) {
                    mPopupWindow.dismiss();
                    isPopup = false;
                } else {
                    mPopupWindow.update();
                    mPopupWindow.showAsDropDown(findViewById(R.id.imageButtonMapMenu));
                    isPopup = true;
                }
            }
        });

        state = HALF;

        setLayout(state);

        setImageButton();

        webView = (WebView) findViewById(R.id.webView1);
        webView.loadData(eqHashMap.get("Tectonic"), "text/html; charset=UTF-8", null);

        mGpsTracker = new GPSTracker(this);
        if (mGpsTracker.canGetLocation) {
            mCurrentLocation = mGpsTracker.getLocation();
        }

        mGpsTracker.setHandler(handler);
        Log.d("MemoryTag", "In SummaryActivity onCreate end: memory less:"
                + Integer.toString(((ActivityManager) getSystemService(ACTIVITY_SERVICE))
                .getMemoryClass()));
    }

    protected void setLayout(int state) {

        linearLayoutSummary = (LinearLayout) findViewById(R.id.linearLayoutSummary);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        if (state == FULL_MAP) {
            Log.d(mTag, "full map");
            Log.d(mTag, "content width, height:" + linearLayoutSummary.getWidth() + ","
                    + linearLayoutSummary.getHeight());

            contentHeight = linearLayoutSummary.getHeight();

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            View view = fragment.getView();

            RelativeLayout.LayoutParams mapLayoutParams = new RelativeLayout.LayoutParams(
                    width, contentHeight - 100);

            view.setLayoutParams(mapLayoutParams);

            view.requestLayout();

            linearLayoutSummary.scrollTo(0, 0);

        } else if (state == FULL_CONTENT) {

            Log.d(mTag, "full content");
            Log.d(mTag, "summary width, height:"
                    + linearLayoutSummary.getWidth() + ","
                    + linearLayoutSummary.getHeight());

            contentHeight = linearLayoutSummary.getHeight();

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            View view = fragment.getView();

            RelativeLayout.LayoutParams mapLayoutParams = new RelativeLayout.LayoutParams(width, height * 3 / 5);
            view.setLayoutParams(mapLayoutParams);
            view.requestLayout();

            linearLayoutSummary.scrollTo(0, height * 3 / 5);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.contentLayout);
            Log.d(mTag, "content width, height:"
                    + linearLayout.getWidth() + ","
                    + linearLayout.getHeight());

            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewWeb);
            Log.d(mTag, "scrollView width, height:"
                    + scrollView.getWidth()
                    + "," + scrollView.getHeight());

            LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(
                    width, contentHeight);
            LinearLayout.LayoutParams webLayoutParams = new LinearLayout.LayoutParams(
                    width, contentHeight - 100);
            linearLayout.setLayoutParams(contentLayoutParams);
            linearLayout.requestLayout();

        } else {
            Log.d(mTag, "half");

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            View view = fragment.getView();
            RelativeLayout.LayoutParams mapLayoutParams = new RelativeLayout.LayoutParams(
                    width, height * 3 / 5);
            view.setLayoutParams(mapLayoutParams);
            view.requestLayout();
            linearLayoutSummary.scrollTo(0, 0);
        }

    }

    protected void setImageButton() {
        try {
            InputStream ims = getAssets().open("up.png");
            Drawable d = Drawable.createFromStream(ims, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            upBitmap = Bitmap.createScaledBitmap(bitmap,
                    // width * 1 / 8,
                    // width * bitmap.getHeight() * 1 / bitmap.getWidth() / 8,
                    100, 100, true);

        } catch (IOException ex) {
            Log.d("Here!!", "load up bitmap error.");
            ex.printStackTrace();
        }

        try {
            InputStream ims = getAssets().open("up2.png");
            Drawable d = Drawable.createFromStream(ims, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            upBitmap_press = Bitmap.createScaledBitmap(bitmap,
                    // width * 1 / 8,
                    // width * bitmap.getHeight() * 1 / bitmap.getWidth() / 8,
                    100, 100, true);

        } catch (IOException ex) {
            Log.d("Here!!", "load up bitmap press error.");
            ex.printStackTrace();
        }

        try {
            InputStream ims = getAssets().open("down.png");
            Drawable d = Drawable.createFromStream(ims, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            downBitmap = Bitmap.createScaledBitmap(bitmap,
                    // width * 1 / 8,
                    // width * bitmap.getHeight() * 1 / bitmap.getWidth() / 8,
                    100, 100, true);
        } catch (IOException ex) {
            Log.d("Here!!", "load down bitmap error.");
            ex.printStackTrace();
        }
        try {
            InputStream ims = getAssets().open("down2.png");
            Drawable d = Drawable.createFromStream(ims, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            downBitmap_press = Bitmap.createScaledBitmap(bitmap,
                    // width * 1 / 8,
                    // width * bitmap.getHeight() * 1 / bitmap.getWidth() / 8,
                    100, 100, true);
        } catch (IOException ex) {
            Log.d("Here!!", "load down bitmap press error.");
            ex.printStackTrace();
        }

        imageButtonUp = (ImageButton) findViewById(R.id.imageButtonUp);
        imageButtonDown = (ImageButton) findViewById(R.id.imageButtonDown);
        imageButtonUp.setImageBitmap(upBitmap);
        imageButtonDown.setImageBitmap(downBitmap);

        imageButtonUp.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setImageBitmap(upBitmap);
                } else {
                    ((ImageButton) v).setImageBitmap(upBitmap_press);
                }
                return false;
            }
        });

        imageButtonDown.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setImageBitmap(downBitmap);
                } else {
                    ((ImageButton) v).setImageBitmap(downBitmap_press);
                }
                return false;
            }
        });

        imageButtonUp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (state == HALF) {
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    state = FULL_CONTENT;
                    setLayout(state);
                } else if (state == FULL_MAP) {
                    state = HALF;
                    setLayout(state);
                }
            }
        });
        imageButtonDown.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (state == HALF) {
                    state = FULL_MAP;
                    setLayout(state);
                } else if (state == FULL_CONTENT) {
                    state = HALF;
                    setLayout(state);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.reload_setting:
                mapOverlay.setEarthquakeMarker();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mGpsTracker = new GPSTracker(this);

        if (mGpsTracker.canGetLocation) {
            mCurrentLocation = mGpsTracker.getLocation();
            // Log.d(mTag,"mCurrentLocation:"+mCurrentLocation.toString());
        }
        mSetupGoogleMap.setupMapIfNeed();

        if (mapOverlay.TaskDownloadBall.isTaskCancel || mapOverlay.TaskDownloadMarkerInfo.isTaskCancel
                || mapOverlay.TaskLoadResource.isTaskCancel) {

            mSetupGoogleMap.mapOverlay = new MapOverlay(this, mSetupGoogleMap.mMap);
            mapOverlay = mSetupGoogleMap.mapOverlay;
            mapOverlay.setEarthquakeMarker();

            mSetupGoogleMap.setHandler();
        }

        super.onResume();
    }

    // TODO on Resume or on Destroy set map

    @Override
    protected void onPause() {
        Log.d(mTag, "In summaryActivity: onPause");
        // TODO save the checkbox data
        if (mSetupGoogleMap.mCheckBoxData != null) {
            File chbxFile = getFileStreamPath(ConstantVariables.CHECKBOX_DATA_FILE_NAME);
            FileOutputStream fos;
            ObjectOutputStream oos;
            try {
                if (chbxFile.exists() || chbxFile.createNewFile()) {
                    fos = openFileOutput(
                            ConstantVariables.CHECKBOX_DATA_FILE_NAME,
                            MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(mSetupGoogleMap.mCheckBoxData);
                    oos.flush();
                    oos.close();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("myTag", "Fail to Write checkbox data to file.");
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(mTag, "In summaryActivity: onStop");
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }
        mGpsTracker = null;

        if (mapOverlay.TaskDownloadMarkerInfo != null) {
            Log.i("AsyncTaskTag", "Download marker Info canceled.");
            mapOverlay.TaskDownloadMarkerInfo.cancel(true);
        }
        if (mapOverlay.TaskDownloadBall != null) {
            Log.i("AsyncTaskTag", "Download Volleyball canceled.");
            mapOverlay.TaskDownloadBall.cancel(true);
        }
        if (mapOverlay.TaskLoadResource != null) {
            Log.i("AsyncTaskTag", "Load Resource canceled.");
            mapOverlay.TaskLoadResource.cancel(true);
        }
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
}
