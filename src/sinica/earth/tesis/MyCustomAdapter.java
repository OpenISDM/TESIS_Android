package sinica.earth.tesis;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by JanSu on 8/24/16.
 */
public class MyCustomAdapter extends BaseAdapter{

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private ArrayList<String> mData = new ArrayList<String>();
    private LayoutInflater mInflater;
    private TreeSet mSeparatorsSet = new TreeSet();

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

    int[] settingValue = null;
    SettingActivity settingActivity;


    public MyCustomAdapter(SettingActivity settingActivity, Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.settingActivity = settingActivity;
        settingValue = ConstantVariables.loadSetting(settingActivity);
    }

    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(final String item) {
        mData.add(item);
        // save separator position
        mSeparatorsSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR
                : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingActivity.ViewHolder holder = null;
        int type = getItemViewType(position);

        if (convertView == null) {
            holder = new SettingActivity.ViewHolder();
            switch (type) {
                case TYPE_ITEM:
                    if (getItem(position).equals(SETTING_NOTIFICATION)) { // CheckBox
                        convertView = mInflater.inflate(R.layout.setting_list_item2, null);
                        holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxSettingListItem);
                        holder.checkBox.setText(SETTING_NOTIFICATION);
                        if (settingValue[6] == ConstantVariables.SETTING_NOTIFICATION_STATE_ON) {
                            holder.checkBox.setChecked(true);
                        } else {
                            holder.checkBox.setChecked(false);
                        }
                        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(
                                    CompoundButton buttonView,
                                    boolean isChecked) {
                                if (isChecked) {
                                    // startService(new Intent(
                                    // settingActivity,
                                    // MyNotificationService.class));
                                    // TODO set send to 1, check if ID
                                    // registered
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        new GCMSetSend(
                                                settingActivity.getApplicationContext(),
                                                GoogleCloudMessaging.getInstance(settingActivity.getApplicationContext()), 1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    else
                                        new GCMSetSend(
                                                settingActivity.getApplicationContext(),
                                                GoogleCloudMessaging.getInstance(settingActivity.getApplicationContext()), 1).execute();

                                    ConstantVariables.saveSetting(
                                            settingActivity,
                                            settingValue[0],
                                            settingValue[1],
                                            settingValue[2],
                                            settingValue[3],
                                            settingValue[4],
                                            settingValue[5],
                                            ConstantVariables.SETTING_NOTIFICATION_STATE_ON);
                                } else {
                                    // stopService(new Intent(
                                    // settingActivity,
                                    // MyNotificationService.class));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        new GCMSetSend(
                                                settingActivity.getApplicationContext(),
                                                GoogleCloudMessaging.getInstance(settingActivity.getApplicationContext()),0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    else
                                        new GCMSetSend(
                                                settingActivity.getApplicationContext(),
                                                GoogleCloudMessaging.getInstance(settingActivity.getApplicationContext()),0).execute();
                                    ConstantVariables.saveSetting(
                                            settingActivity,
                                            settingValue[0],
                                            settingValue[1],
                                            settingValue[2],
                                            settingValue[3],
                                            settingValue[4],
                                            settingValue[5],
                                            ConstantVariables.SETTING_NOTIFICATION_STATE_OFF);
                                }
                            }
                        });

                    } else { // TextView
                        convertView = mInflater.inflate(R.layout.setting_list_item, null);
                        holder.textView = (TextView) convertView.findViewById(R.id.textViewSettingListItem1);
                        TextView contentTextView = (TextView) convertView.findViewById(R.id.textViewSettingListItem2);

                        String contentString = null;
                        if (getItem(position).equals(SETTING_ML)) {
                            contentString = SETTING_PREFERENCE_ML[settingValue[0]] + " - " + SETTING_PREFERENCE_ML[settingValue[1]] + " ML";
                        } else if (getItem(position).equals(SETTING_DEEP)) {
                            contentString = SETTING_PREFERENCE_DEPTH[settingValue[2]] + " - " + SETTING_PREFERENCE_DEPTH[settingValue[3]] + " km";
                        } else if (getItem(position).equals(SETTING_DATE)) {
                            switch (settingValue[5]) {
                                case SETTING_DATE_ALL:
                                    contentString = "-";
                                    break;
                                default:
                                    contentString = SETTING_PREFERENCE_DATE[settingValue[5]] + " 之內";
                                    break;
                            }
                        } else if (getItem(position).equals(SETTING_LOCATE)) {
                            switch (settingValue[4]) {
                                case 0:
                                    contentString = "-";
                                    break;

                                default:
                                    contentString = SETTING_PREFERENCE_DISTANCE[settingValue[4]] + "km 之內";
                                    break;
                            }
                        }
                        contentTextView.setText(contentString);
                        holder.textView.setText(mData.get(position));
                    }
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.section_header_divider, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textViewSectionHeader);
                    holder.textView.setText(mData.get(position));
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (SettingActivity.ViewHolder) convertView.getTag();
        }
        // holder.textView.setText(mData.get(position));
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        // return super.areAllItemsEnabled();
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }
}
