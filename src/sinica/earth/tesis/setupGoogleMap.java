package sinica.earth.tesis;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import uk.co.senab.photoview.PhotoViewAttacher;

public class setupGoogleMap {

    public GoogleMap mMap;

    // myMapView mapView;
    private summaryActivity mainActivity;

    // setupGoogleMapTool mSetupGoogleMapTool;
    protected MapOverlay mapOverlay;

    private HashMap<String, String> eqHashMap;

    private final String mTag = "myTag";

    private ImageView mImageView;

    private PopupWindow mPopupWindow;

    private ImageView historyImageView;

    private Marker infoOpenedMarker = null;

    TextView[] textViews;

    private Handler handler;

    private final int LOAD_RESOURCE_FINISHED = ConstantVariables.LOAD_RESOURCE_FINISHED;

    /*
    * CheckBox id = group*10+child Group 0 child 0:最新地震資訊 child 1:動畫顯示
    * child2:震源機制 child 3:震度圖 child 4:即時震度圖 child 5:背景地震 Group 1 child 0:地質圖
    * child 1:震間變形 child2:活動斷層 child 3:寬頻測站 Group 2 child 0:道路地圖 child 1:衛星地圖
    * child 2:地形圖 child0:座標線
    */
    protected HashMap<Integer, Boolean> mCheckBoxData;

    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private CheckBox checkBox7;
    private Button button4;

    // Spinner spinnerCh1, spinnerCh2;
    private boolean isGeoStatePopup = false;

    protected SeekBar seekBarCh1;

    private int seekBarProgress;

    private String[] geoMapStrings = {"CWB", "P-alert"};

    private String[] ballStrings = {"gCAP", "auto_BATS", "RMT", "FMNEAR", "WP", "BATS"};

    private myArrayAdapter<String> arrayAdapterCh1, arrayAdapterCh2;

    private Button spinnerBtn1, spinnerBtn2;

    public setupGoogleMap(summaryActivity mainActivity, PopupWindow mPopupWindow) {
        this.mainActivity = mainActivity;
        this.mPopupWindow = mPopupWindow;
        this.eqHashMap = mainActivity.eqHashMap;
        setupMapIfNeed();
    }

    protected Bitmap scaleBitmapOnScreenSize(Drawable d) {
        DisplayMetrics metrics = mainActivity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.d("bitmap", "screen width:" + width + " height:" + height);
        Bitmap oldBitmap = ((BitmapDrawable) d).getBitmap();
        // sony Xperia SP 720*1280
        float scalingFactor = (float) width / 720;
        Bitmap newBitmap = ConstantVariables.ScaleBitmap(oldBitmap, scalingFactor);
        // oldBitmap.recycle();
        return newBitmap;
    }

    protected void loadMapBar() {
        mImageView = (ImageView) mainActivity.findViewById(R.id.imageView1);
        // load map bar image
        mImageView.setImageBitmap(scaleBitmapOnScreenSize(mainActivity
                .getResources().getDrawable(R.drawable.ml_map_bar_trans)));
    }

    protected void loadHistoryBar() {
        historyImageView = (ImageView) mainActivity.findViewById(R.id.imageView2);
        historyImageView.setImageBitmap(scaleBitmapOnScreenSize(mainActivity
                .getResources().getDrawable(R.drawable.colorbar)));
    }

    protected void setupMapIfNeed() {

        loadMapBar();
        loadHistoryBar();

        if (mMap == null) {
            mMap = ((SupportMapFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        } else {
            setupGoogleMapOnCreate();
        }
    }

    private void setupGoogleMapOnCreate() {
        float density = mainActivity.getResources().getDisplayMetrics().density;
        int top = (int) (100 * density);
        mMap.setPadding(0, top, 0, 0);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(true);

        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // if you return null, it will just use the default window
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                String title = marker.getTitle();
                String content = marker.getSnippet();
                TextView textView = new TextView(mainActivity
                        .getApplicationContext());
                // textView.setGravity(Gravity.CENTER);
                if (title == null && content == null) {
                    return null;
                } else if (title != null && content != null) {
                    textView.setText(title + "\n" + content);
                } else if (title != null) {
                    textView.setText(title);
                } else {
                    textView.setText(content);
                }
                textView.setTextColor(Color.BLACK);
                // Log.d("Here!", "set marker Info " + textView.getText());
                return textView;
            }
        });

        Double lat = Double.parseDouble(eqHashMap.get("Latitude"));
        Double lng = Double.parseDouble(eqHashMap.get("Longitude"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),
                8));
        textViews = new TextView[2];
        for (int i = 0; i < textViews.length; ++i) {
            RelativeLayout relativeLayout = (RelativeLayout) mainActivity
                    .findViewById(R.id.mapLayout);
            TextView textView = new TextView(mainActivity);
            textViews[i] = textView;
            relativeLayout.addView(textViews[i]);
        }

        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                infoOpenedMarker = null;
                Log.d(mTag, "marker info window is closed " + arg0.toString());
                if (mainActivity.mPopupWindow.isShowing()) {
                    mainActivity.mPopupWindow.dismiss();
                }
            }
        });
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                infoOpenedMarker = arg0;
                Log.d(mTag, "marker is clicked " + arg0.toString());
                if (mainActivity.mPopupWindow.isShowing()) {
                    mainActivity.mPopupWindow.dismiss();
                }
                return false;
            }
        });

        // TODO List Adapter with checkbox data
        File chbxFile = mainActivity.getFileStreamPath(ConstantVariables.CHECKBOX_DATA_FILE_NAME);
        if (chbxFile.exists()) {
            try {
                FileInputStream fis;
                fis = mainActivity.openFileInput(ConstantVariables.CHECKBOX_DATA_FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                mCheckBoxData = (HashMap<Integer, Boolean>) ois.readObject();
                ois.close();
                fis.close();
            } catch (Exception e) {
                Log.e(mTag, "checkBox data file broken");
                chbxFile.delete();
                e.printStackTrace();
                mCheckBoxData = new HashMap<Integer, Boolean>();
                for (int i = 0; i < 40; ++i) {
                    mCheckBoxData.put(i, false);
                }
                mCheckBoxData.put(100, true); // CWB
//                mCheckBoxData.put(101, false); // gCap
                mCheckBoxData.put(102, false); // P-alert

                // 0828 add all volleyballs
                // mCheckBoxData.put(200, true);// CMTs
                mCheckBoxData.put(201, true);// gCAP
                mCheckBoxData.put(202, false);// BATS
                mCheckBoxData.put(203, false);// auto_BATS
                mCheckBoxData.put(204, false);// FMNEAR
                mCheckBoxData.put(205, false);// RMT
                mCheckBoxData.put(206, false);// WP

                mCheckBoxData.put(0, true);
                mCheckBoxData.put(12, true);
                // mCheckBoxData.put(2, true);

                // checkBox5.setChecked(true);
                mCheckBoxData.put(30, false);
            }
        } else {
            mCheckBoxData = new HashMap<Integer, Boolean>();
            for (int i = 0; i < 40; ++i) {
                mCheckBoxData.put(i, false);
            }
            mCheckBoxData.put(100, true); // CWB
//            mCheckBoxData.put(101, false); // gCap
            mCheckBoxData.put(102, false); // P-alert

            // 0828 add all volleyballs
            // mCheckBoxData.put(200, true);// CMTs
            mCheckBoxData.put(201, true);// gCAP
            mCheckBoxData.put(202, false);// BATS
            mCheckBoxData.put(203, false);// auto_BATS
            mCheckBoxData.put(204, false);// FMNEAR
            mCheckBoxData.put(205, false);// RMT
            mCheckBoxData.put(206, false);// WP

            mCheckBoxData.put(0, true);
            mCheckBoxData.put(12, true);
            // mCheckBoxData.put(2, true);

            // checkBox5.setChecked(true);
            mCheckBoxData.put(30, false);
            mCheckBoxData.put(30, false);
        }

        Log.d(mTag, "mCheckBoxData:" + mCheckBoxData.toString());

        setMapTool();
        mapOverlay = new MapOverlay(mainActivity, mMap);

        mapOverlay.setEarthquakeMarker();
        mainActivity.mapOverlay = mapOverlay;
        setHandler();
    }

    protected void setHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    // 當收到的Message的代號為我們剛剛訂的代號就做下面的動作。
                    case LOAD_RESOURCE_FINISHED:
                        checkTag();
                        break;
                }
                super.handleMessage(msg);
            }

        };
        mapOverlay.setHandler(handler);
    }

    private void setMapTool() {

        //震度圖
        checkBox1 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox1);
        //地質圖
        checkBox2 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox2);
        //活動斷層
        checkBox3 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox3);
        //震間變形
        checkBox4 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox4);
        //震源機制
        checkBox5 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox5);
        //衛星地圖
        checkBox6 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox6);
        //背景地震
        checkBox7 = (CheckBox) mPopupWindow.getContentView().findViewById(R.id.checkBox7);

        ////CWB
        // button1 = (Button) mainActivity.findViewById(R.id.button1);
        ////gCAP
        // button2 = (Button) mainActivity.findViewById(R.id.button2);
        ////P-alert
        // button3 = (Button) mainActivity.findViewById(R.id.button3);

        //地質圖圖示
        button4 = (Button) mainActivity.findViewById(R.id.button4);

        seekBarCh1 = (SeekBar) mainActivity.findViewById(R.id.seekBar1);
        // seekBarCh2 = (SeekBar) mainActivity.findViewById(R.id.seekBar2);
        // spinnerCh1 = (Spinner) mainActivity.findViewById(R.id.spinner1);
        // spinnerCh2 = (Spinner) mainActivity.findViewById(R.id.spinner2);

        spinnerBtn1 = (Button) mainActivity.findViewById(R.id.buttonSpinner1);
        spinnerBtn2 = (Button) mainActivity.findViewById(R.id.buttonSpinner2);

        arrayAdapterCh1 = new myArrayAdapter<String>(mainActivity,
                android.R.layout.simple_spinner_dropdown_item, geoMapStrings);
        arrayAdapterCh2 = new myArrayAdapter<String>(mainActivity,
                android.R.layout.simple_spinner_dropdown_item, ballStrings);

        // TODO set button available when geomap is checked
        button4.setVisibility(View.INVISIBLE);
        seekBarCh1.setVisibility(View.INVISIBLE);
        // seekBarCh2.setVisibility(View.INVISIBLE);
        // spinnerCh1.setVisibility(View.INVISIBLE);
        // spinnerCh2.setVisibility(View.INVISIBLE);
        spinnerBtn1.setVisibility(View.INVISIBLE);
        spinnerBtn2.setVisibility(View.INVISIBLE);

        /**
         * 震度圖選項
         */
        // CWB
        if (mCheckBoxData.get(100)) {
            spinnerBtn1.setText(geoMapStrings[0]);
        }
        // P-alert
        else if (mCheckBoxData.get(102)) {
            spinnerBtn1.setText(geoMapStrings[1]);
        }


        /**
         * 震源機制選項
         */
        // gCAP
        if (mCheckBoxData.get(201)) {
            spinnerBtn2.setText(ballStrings[0]);
        }
        // BATS
        else if (mCheckBoxData.get(202)) {
            spinnerBtn2.setText(ballStrings[5]);
        }
        // auto_BATS
        else if (mCheckBoxData.get(203)) {
            spinnerBtn2.setText(ballStrings[1]);
        }
        // FMNEAR
        else if (mCheckBoxData.get(204)) {
            spinnerBtn2.setText(ballStrings[3]);
        }
        // RMT
        else if (mCheckBoxData.get(205)) {
            spinnerBtn2.setText(ballStrings[2]);
        }
        // WP
        else if (mCheckBoxData.get(206)) {
            spinnerBtn2.setText(ballStrings[4]);
        }

        checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mCheckBoxData.put(3, true);
                    // spinnerCh1.setVisibility(View.VISIBLE);
                    seekBarCh1.setVisibility(View.VISIBLE);
                    spinnerBtn1.setVisibility(View.VISIBLE);

                } else {
                    mCheckBoxData.put(3, false);
                    // spinnerCh1.setVisibility(View.INVISIBLE);
                    seekBarCh1.setVisibility(View.INVISIBLE);
                    spinnerBtn1.setVisibility(View.INVISIBLE);
                }
                checkTag();
            }
        });
        checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mCheckBoxData.put(10, true);
                    // seekBarCh2.setVisibility(View.VISIBLE);
                } else {
                    mCheckBoxData.put(10, false);
                    // seekBarCh2.setVisibility(View.INVISIBLE);
                }
                checkTag();
            }
        });

        checkBox3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mCheckBoxData.put(12, true);
                } else {
                    mCheckBoxData.put(12, false);
                }
                checkTag();
            }
        });
        checkBox4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mCheckBoxData.put(11, true);
                } else {
                    mCheckBoxData.put(11, false);
                }
                checkTag();
            }
        });
        checkBox5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mCheckBoxData.put(2, true);
                    // spinnerCh2.setVisibility(View.VISIBLE);
                    spinnerBtn2.setVisibility(View.VISIBLE);
                } else {
                    mCheckBoxData.put(2, false);
                    // spinnerCh2.setVisibility(View.INVISIBLE);
                    spinnerBtn2.setVisibility(View.INVISIBLE);
                }
                checkTag();
            }
        });

        checkBox6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                checkTag();
            }
        });

        checkBox7.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mCheckBoxData.put(5, true);
                } else {
                    mCheckBoxData.put(5, false);
                }
                checkTag();
            }
        });

        button4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isGeoStatePopup) {
                    isGeoStatePopup = false;
                } else {
                    LinearLayout linearLayout = (LinearLayout) mainActivity
                            .findViewById(R.id.linearLayoutSummary);
                    isGeoStatePopup = true;
                }
            }
        });

        seekBarCh1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                checkTag();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // checkTag();
                if (progress <= 12) {
                    seekBarCh1.setProgress(0);
                } else if (progress > 12 && progress <= 37) {
                    seekBarCh1.setProgress(25);
                } else if (progress > 37 && progress <= 62) {
                    seekBarCh1.setProgress(50);
                } else if (progress > 62 && progress <= 87) {
                    seekBarCh1.setProgress(75);
                } else if (progress > 87) {
                    seekBarCh1.setProgress(100);
                }
                seekBarProgress = seekBar.getMax() - seekBar.getProgress();
            }
        });

        spinnerBtn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mainActivity).setTitle("震度圖選項")
                        .setAdapter(arrayAdapterCh1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                spinnerBtn1.setText(geoMapStrings[which]);
                                mCheckBoxData.put(100, false); // CWB
//                                mCheckBoxData.put(101, false); // gCap
                                mCheckBoxData.put(102, false); // P-alert
                                switch (which) {
                                    case 0:
                                        mCheckBoxData.put(100, true);
                                        break;
//                                    case 1:
//                                        mCheckBoxData.put(101, true);
//                                        break;
                                    case 1:
                                        mCheckBoxData.put(102, true);
                                        break;
                                    default:
                                        break;
                                }
                                checkTag();
                                dialog.dismiss();
                            }
                        }).create().show();

            }
        });

        spinnerBtn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mainActivity).setTitle("震源機制選項")
                        .setAdapter(arrayAdapterCh2, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                spinnerBtn2.setText(ballStrings[which]);
                                mCheckBoxData.put(201, false);// gCAP
                                mCheckBoxData.put(202, false);// BATS
                                mCheckBoxData.put(203, false);// auto_BATS
                                mCheckBoxData.put(204, false);// FMNEAR
                                mCheckBoxData.put(205, false);// RMT
                                mCheckBoxData.put(206, false);// WP

                                Log.i("check", Integer.toString(which));

                                switch (which) {
                                    case 0:
                                        mCheckBoxData.put(201, true);// gCAP
                                        break;
                                    case 1:
                                        mCheckBoxData.put(203, true);// auto_BATS
                                        break;
                                    case 2:
                                        mCheckBoxData.put(205, true);// RMT
                                        break;
                                    case 3:
                                        mCheckBoxData.put(204, true);// FMNEAR
                                        break;
                                    case 4:
                                        mCheckBoxData.put(206, true);// WP
                                        break;
                                    case 5:
                                        mCheckBoxData.put(202, true);// BATS
                                        break;
                                    default:
                                        break;
                                }
                                checkTag();

                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    public synchronized void checkTag() {

        mMap.clear();

        // 地質圖
        if (mCheckBoxData.get(10)) {
            checkBox2.setChecked(true);
            if (mapOverlay.isloadResourceFinished) {
                if (summaryActivity.isConnected(mainActivity.getApplicationContext())) {
                    mapOverlay.draw(1, (float) 0);
                    // mMap.addTileOverlay(mapOverlay.tileOverlayOptions2);
                    // mMap.addTileOverlay(mapOverlay.tileOverlayOptions);
                } else {
                    mapOverlay.draw(2, (float) 0);
                }
            }
            button4.setVisibility(View.VISIBLE);
            // mapOverlay.draw(2, (float) seekBarCh2.getProgress() / 100);
        } else {
            button4.setVisibility(View.INVISIBLE);
            mapOverlay.isDraw2 = false;
        }

        // 震度圖
        if (mCheckBoxData.get(3)) {
            Log.d("Here!", "Draw geo map now.");
            checkBox1.setChecked(true);
            // spinnerCh1.setVisibility(View.VISIBLE);
            spinnerBtn1.setVisibility(View.VISIBLE);
            seekBarCh1.setVisibility(View.VISIBLE);
            seekBarCh1.setProgress(100 - seekBarProgress);
            // seekBar.setVisibility(View.VISIBLE);


            if (mCheckBoxData.get(100)) {
                mapOverlay.draw(10, (float) seekBarProgress / 100);
            } else if (mCheckBoxData.get(102)) {
                mapOverlay.draw(15, (float) seekBarProgress / 100);
            }
        } else {
            // mCheckBoxData.put(100, false); // CWB
            // mCheckBoxData.put(101, false); // gCap
            // mCheckBoxData.put(102, false); // P-alert
            mapOverlay.isDraw9 = false;
            mapOverlay.isDraw10 = false;
            mapOverlay.isDraw15 = false;
        }

        // 震源機制
        if (mCheckBoxData.get(2)) {
            checkBox5.setChecked(true);
            // spinnerCh2.setVisibility(View.VISIBLE);
            spinnerBtn2.setVisibility(View.VISIBLE);
            mImageView.setVisibility(ImageView.INVISIBLE);

            // gCAP
            if (mCheckBoxData.get(201)) {
                mapOverlay.draw(17, (float) 0);
            }
            // BATS
            else if (mCheckBoxData.get(202)) {
                mapOverlay.draw(18, (float) 0);
            }
            // auto_BATS
            else if (mCheckBoxData.get(203)) {
                mapOverlay.draw(19, (float) 0);
            }
            // FMNEAR
            else if (mCheckBoxData.get(204)) {
                mapOverlay.draw(20, (float) 0);
            }
            // RMT
            else if (mCheckBoxData.get(205)) {
                mapOverlay.draw(24, (float) 0);
            }
            // WP
            else if (mCheckBoxData.get(206)) {
                mapOverlay.draw(25, (float) 0);
            }

        } else {
            // mapOverlay.draw(1, (float) 0);
            if (!mCheckBoxData.get(5)) {
                mImageView.setVisibility(ImageView.VISIBLE);
                mapOverlay.draw(7, (float) 0);
            }
            mapOverlay.isDraw17 = false;
            mapOverlay.isDraw18 = false;
            mapOverlay.isDraw19 = false;
            mapOverlay.isDraw20 = false;
            // mCheckBoxData.put(201, false);// gCAP
            // mCheckBoxData.put(202, false);// BATS
            // mCheckBoxData.put(203, false);// New_BATS
            // mCheckBoxData.put(204, false);// FMNEAR
        }

        // 震間變形
        if (mCheckBoxData.get(11)) {
            checkBox4.setChecked(true);
            mapOverlay.draw(11, (float) 0);
        }

        // 活動斷層
        if (mCheckBoxData.get(12)) {
            checkBox3.setChecked(true);
            mapOverlay.draw(3, (float) 0);
        }

        // if (mCheckBoxData.get(30)) { // 0811 add polyline
        // drawPolylines();
        // }
        if (infoOpenedMarker != null) {
            Log.d(mTag, "checkTag:showInfoWindow " + infoOpenedMarker);
            infoOpenedMarker.showInfoWindow();
        }

        // 背景地震
        if (mCheckBoxData.get(5)) {
            historyImageView.setVisibility(View.VISIBLE);
//			sizeImageView.setVisibility(View.VISIBLE);
            checkBox7.setChecked(true);
            if (mapOverlay.isloadResourceFinished) {
                if (summaryActivity.isConnected(mainActivity
                        .getApplicationContext())) {
                    mapOverlay.draw(21, (float) 0);
                    mapOverlay.draw(21, (float) 0);
                } else {
                    mapOverlay.draw(22, (float) 0);
                }
            }
            // mapOverlay.draw(22, (float) 0);
            if (!mCheckBoxData.get(2)) {
                mImageView.setVisibility(ImageView.INVISIBLE);
                // set Marker Icon to star
                mapOverlay.draw(23, (float) 0);
            }
        } else {
            historyImageView.setVisibility(View.INVISIBLE);
//			sizeImageView.setVisibility(View.INVISIBLE);
            if (!mCheckBoxData.get(2)) {
                mImageView.setVisibility(ImageView.VISIBLE);
                mapOverlay.draw(7, (float) 0);
            }
        }
    }
}
