package sinica.earth.tesis;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    final String SETTING_ML = "規模範圍";
    final String SETTING_DEEP = "深度範圍";
    final String SETTING_DATE = "距離現在時間";
    final String SETTING_LOCATE = "距離現在位置";
    final String SETTING_NOTIFICATION = "新地震通知";

    final int SETTING_DATE_ALL = ConstantVariables.SETTING_DATE_ALL;
    final int SETTING_DATE_1_DAY = ConstantVariables.SETTING_DATE_1_DAY;
    final int SETTING_DATE_1_WEEK = ConstantVariables.SETTING_DATE_1_WEEK;
    final int SETTING_DATE_1_MONTH = ConstantVariables.SETTING_DATE_1_MONTH;

    String SETTING_PREFERENCE_ML[] = ConstantVariables.SETTING_PREFERENCE_ML;
    String SETTING_PREFERENCE_DEPTH[] = ConstantVariables.SETTING_PREFERENCE_DEPTH;
    String SETTING_PREFERENCE_DISTANCE[] = ConstantVariables.SETTING_PREFERENCE_DISTANCE;
    String SETTING_PREFERENCE_DATE[] = ConstantVariables.SETTING_PREFERENCE_DATE;

    Dialog settingDialog;

    private MyCustomAdapter mAdapter;

    ListView listView;

    SettingActivity settingActivity;

    NumberPicker np1, np2;

    Button confirmButton;

    View currentSettingView = null;

    int currentPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingActivity = this;
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setTitle("設定地震篩選條件");
        getSupportActionBar().setSubtitle("TESIS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listViewInSetting);

        mAdapter = new MyCustomAdapter(settingActivity , this);
        mAdapter.addSeparatorItem("地震數值");
        mAdapter.addItem(SETTING_ML);
        mAdapter.addItem(SETTING_DEEP);
        mAdapter.addSeparatorItem("時間");
        mAdapter.addItem(SETTING_DATE);
        mAdapter.addSeparatorItem("地點");
        mAdapter.addItem(SETTING_LOCATE);
        mAdapter.addSeparatorItem("通知");
        mAdapter.addItem(SETTING_NOTIFICATION);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                currentPosition = position;
                int[] settingValue = ConstantVariables
                        .loadSetting(settingActivity);

                settingDialog = new Dialog(SettingActivity.this);

                if (mAdapter.getItem(position).equals(SETTING_ML)) {

                    settingDialog.setContentView(R.layout.setting_number_picker);
                    settingDialog.setTitle("規模範圍 (0.0-10.0)");

                    np1 = (NumberPicker) settingDialog.findViewById(R.id.numberPicker1);
                    np1.setMaxValue(SETTING_PREFERENCE_ML.length - 1);
                    np1.setMinValue(0);
                    np1.setWrapSelectorWheel(false);
                    np1.setDisplayedValues(SETTING_PREFERENCE_ML);
                    np1.setValue(settingValue[0]);
                    np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    np1.setOnValueChangedListener(new OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker,
                                                  int oldVal, int newVal) {
                            if (newVal > np2.getValue()) {
                                np2.setValue(newVal);
                            }
                        }
                    });

                    np2 = (NumberPicker) settingDialog.findViewById(R.id.numberPicker2);
                    np2.setMaxValue(SETTING_PREFERENCE_ML.length - 1);
                    np2.setMinValue(0);
                    np2.setWrapSelectorWheel(false);
                    np2.setDisplayedValues(SETTING_PREFERENCE_ML);
                    np2.setValue(settingValue[1]);
                    np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    np2.setOnValueChangedListener(new OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker,
                                                  int oldVal, int newVal) {
                            if (newVal < np1.getValue()) {
                                np1.setValue(newVal);
                            }
                        }
                    });

                    confirmButton = (Button) settingDialog.findViewById(R.id.buttonConfirm);
                    confirmButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] settingValue = ConstantVariables
                                    .loadSetting(settingActivity);

                            ConstantVariables.saveSetting(
                                    settingActivity,
                                    np1.getValue(),
                                    np2.getValue(),
                                    settingValue[2],
                                    settingValue[3],
                                    settingValue[4],
                                    settingValue[5],
                                    settingValue[6]);

                            TextView tx = (TextView) listView.getChildAt(
                                    currentPosition).findViewById(
                                    R.id.textViewSettingListItem2);
                            tx.setText(SETTING_PREFERENCE_ML[np1.getValue()] + " - "
                                    + SETTING_PREFERENCE_ML[np2.getValue()] + " ML");
                            np1 = null;
                            np2 = null;
                            settingDialog.dismiss();
                        }
                    });

                    settingDialog.show();

                } else if (mAdapter.getItem(position).equals(SETTING_DEEP)) {

                    settingDialog.setContentView(R.layout.setting_number_picker);
                    settingDialog.setTitle("深度範圍 (0-500)");

                    np1 = (NumberPicker) settingDialog.findViewById(R.id.numberPicker1);
                    np1.setMaxValue(SETTING_PREFERENCE_DEPTH.length - 1);
                    np1.setMinValue(0);
                    np1.setWrapSelectorWheel(false);
                    np1.setDisplayedValues(SETTING_PREFERENCE_DEPTH);
                    np1.setValue(settingValue[2]);
                    np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    np1.setOnValueChangedListener(new OnValueChangeListener() {

                        @Override
                        public void onValueChange(NumberPicker picker,
                                                  int oldVal, int newVal) {
                            if (newVal > np2.getValue()) {
                                np2.setValue(newVal);
                            }
                        }
                    });

                    np2 = (NumberPicker) settingDialog.findViewById(R.id.numberPicker2);
                    np2.setMaxValue(SETTING_PREFERENCE_DEPTH.length - 1);
                    np2.setMinValue(0);
                    np2.setWrapSelectorWheel(false);
                    np2.setDisplayedValues(SETTING_PREFERENCE_DEPTH);
                    np2.setValue(settingValue[3]);
                    np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    np2.setOnValueChangedListener(new OnValueChangeListener() {

                        @Override
                        public void onValueChange(NumberPicker picker,
                                                  int oldVal, int newVal) {
                            if (newVal < np1.getValue()) {
                                np1.setValue(newVal);
                            }
                        }
                    });

                    confirmButton = (Button) settingDialog.findViewById(R.id.buttonConfirm);
                    confirmButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] settingValue = ConstantVariables
                                    .loadSetting(settingActivity);

                            ConstantVariables.saveSetting(
                                    settingActivity,
                                    settingValue[0],
                                    settingValue[1],
                                    np1.getValue(),
                                    np2.getValue(),
                                    settingValue[4],
                                    settingValue[5],
                                    settingValue[6]);

                            TextView tx = (TextView) listView.getChildAt(
                                    currentPosition).findViewById(
                                    R.id.textViewSettingListItem2);
                            tx.setText(SETTING_PREFERENCE_DEPTH[np1.getValue()] + " - "
                                    + SETTING_PREFERENCE_DEPTH[np2.getValue()] + " km 之內");
                            np1 = null;
                            np2 = null;
                            settingDialog.dismiss();
                        }
                    });

                    settingDialog.show();

                } else if (mAdapter.getItem(position).equals(SETTING_DATE)) {

                    settingDialog.setContentView(R.layout.setting_number_picker_2);
                    settingDialog.setTitle("距離現在時間");

                    np1 = (NumberPicker) settingDialog.findViewById(R.id.numberPicker1);
                    np1.setMaxValue(SETTING_PREFERENCE_DATE.length - 1);
                    np1.setMinValue(0);
                    np1.setWrapSelectorWheel(false);
                    np1.setDisplayedValues(SETTING_PREFERENCE_DATE);
                    np1.setValue(settingValue[5]);
                    np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                    confirmButton = (Button) settingDialog.findViewById(R.id.buttonConfirm);
                    confirmButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] settingValue = ConstantVariables
                                    .loadSetting(settingActivity);

                            ConstantVariables.saveSetting(
                                    settingActivity,
                                    settingValue[0],
                                    settingValue[1],
                                    settingValue[2],
                                    settingValue[3],
                                    settingValue[4],
                                    np1.getValue(),
                                    settingValue[6]);

                            TextView tx = (TextView) listView.getChildAt(
                                    currentPosition).findViewById(
                                    R.id.textViewSettingListItem2);
                            tx.setText(SETTING_PREFERENCE_DATE[np1.getValue()] + " 之內");
                            np1 = null;
                            settingDialog.dismiss();
                        }
                    });

                    settingDialog.show();

                } else if (mAdapter.getItem(position).equals(SETTING_LOCATE)) {

                    settingDialog.setContentView(R.layout.setting_number_picker_2);
                    settingDialog.setTitle("距離現在位置 (km)");

                    np1 = (NumberPicker) settingDialog.findViewById(R.id.numberPicker1);
                    np1.setMaxValue(SETTING_PREFERENCE_DISTANCE.length - 1);
                    np1.setMinValue(0);
                    np1.setWrapSelectorWheel(false);
                    np1.setDisplayedValues(SETTING_PREFERENCE_DISTANCE);
                    np1.setValue(settingValue[4]);
                    np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                    confirmButton = (Button) settingDialog.findViewById(R.id.buttonConfirm);
                    confirmButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] settingValue = ConstantVariables
                                    .loadSetting(settingActivity);

                            ConstantVariables.saveSetting(
                                    settingActivity,
                                    settingValue[0],
                                    settingValue[1],
                                    settingValue[2],
                                    settingValue[3],
                                    np1.getValue(),
                                    settingValue[5],
                                    settingValue[6]);

                            TextView tx = (TextView) listView.getChildAt(
                                    currentPosition).findViewById(
                                    R.id.textViewSettingListItem2);
                            tx.setText(SETTING_PREFERENCE_DISTANCE[np1.getValue()] + "km 之內");
                            np1 = null;
                            settingDialog.dismiss();
                        }
                    });

                    settingDialog.show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d("myTag", "In Setting View: onDestroy.");
        setResult(RESULT_OK);
        super.onDestroy();
    }


    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }
}
