package sinica.earth.tesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class ConstantVariables {

    /**
     * Progress bar text
     */
    public static final String[] LOADING_TEXT = {
            "資料下載中，請保持網路暢通。",
            "資料量大或網路過慢時，\n將費時較久，請耐心等候。",
            "第一次下載後，\n將自動儲存地震列表基本資訊，\n供離線使用。"
    };

    /**
     * 如果有改過MAX_STORED_EQ_LENGTH，那麼GENERAL_FILE_NAME跟NEWEST_ID_FILE_NAME都要改。
     */
    public static final String GENERAL_FILE_NAME = "TESIS.earthquakeList.general.data.20150712";

    /**
     *
     */
    public static final String NEWEST_ID_FILE_NAME = "TESIS.newest.earthquake.id.20150712";

    /**
     *
     */
    public static final String NEWEST_SEND_NF_FILE_NAME = "TESIS.newist.notification.eq.id.20150712";

    /**
     *
     */
    public static final String LAST_OPENED_ID_FILE_NAME = "TESIS.newest.earthquake.id.last";

    /**
     *
     */
    public static final String SETTING_PREFERENCE_FILE_NAME = "TESIS.earthquakeList.setting";

    /**
     *
     */
    public static final String CHECKBOX_DATA_FILE_NAME = "TESIS.earthquakeList.checkbox.data";

    /**
     *
     */
    public static final int MAX_STORED_EQ_LENGTH = 50;

    /**
     * ML from 0.0 to 10.0
     */
    public static final String[] SETTING_PREFERENCE_ML = {
            "0.0", "0.5", "1.0", "1.5", "2.0",
            "2.5", "3.0", "3.5", "4.0", "4.5",
            "5.0", "5.5", "6.0", "6.5", "7.0",
            "7.5", "8.0", "8.5", "9.0", "9.5",
            "10.0"
    };

    /**
     * Depth from 0 to 350
     */
    public static final String[] SETTING_PREFERENCE_DEPTH = {
            "0", "50", "100", "150", "200",
            "250", "300", "350"
    };

    /**
     * Distance 0 => all
     */
    public static final String[] SETTING_PREFERENCE_DISTANCE = {
            "0", "50", "100", "150", "200",
            "250", "300", "350", "400", "450",
            "500", "1000"
    };

    /**
     *
     */
    public static final String[] SETTING_PREFERENCE_DATE = {
            "全部", "一天", "一週", "一個月", "兩個月", "三個月"
    };

    /**
     * Date 0 => all
     */
    public static final int SETTING_DATE_ALL = 0;

    /**
     *
     */
    public static final int SETTING_DATE_1_DAY = 1;

    /**
     *
     */
    public static final int SETTING_DATE_1_WEEK = 2;

    /**
     *
     */
    public static final int SETTING_DATE_1_MONTH = 3;

    /**
     *
     */
    public static final int SETTING_DATE_2_MONTH = 4;

    /**
     *
     */
    public static final int SETTING_DATE_3_MONTH = 5;

    /**
     *
     */
    public static final int SETTING_NOTIFICATION_STATE_ON = 1;

    /**
     *
     */
    public static final int SETTING_NOTIFICATION_STATE_OFF = 0;

    /**
     *
     */
    public static final int[] EARTHQUAKE_FAULT_LINE_COLOR = {
            1, 1, 1, 5, 5, 0, 5, 5, 5, 5, 5,
            0, 5, 0, 2, 0, 0, 0, 1, 5, 0, 0,
            0, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1,
            1, 5, 1, 5, 5, 5, 5, 1, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0
    };

    /**
     * Handler Message
     */
    public static final int CURRENT_LOCATION_UPDATE = 8848;

    /**
     *
     */
    public static final int CHECK_UPDATE = 8849;

    /**
     *
     */
    public static final int LOAD_RESOURCE_FINISHED = 7777;

    /**
     * MapOverlay
     */
    public static final String[] EARTHQUAKE_FAULT_NAMES = {
            "山腳斷層", "湖口斷層", "新竹斷層", "新城斷層",
            "新城斷層", "獅潭斷層", "三義斷層", "大甲斷層",
            "大甲斷層", "鐵砧山斷層", "鐵砧山斷層", "屯子腳斷層",
            "彰化斷層", "大茅埔斷層", "九芎坑斷層", "梅山斷層",
            "大尖山斷層", "大尖山斷層", "木屐寮斷層", "六甲斷層",
            "觸口斷層", "觸口斷層", "新化斷層", "後甲里斷層",
            "左鎮斷層", "左鎮斷層", "左鎮斷層", "小崗山斷層",
            "旗山斷層", "潮州斷層", "潮州斷層", "恆春斷層",
            "米崙斷層", "嶺頂斷層", "瑞穗斷層", "奇美斷層",
            "玉里斷層", "玉里斷層", "池上斷層", "鹿野斷層",
            "利吉斷層", "利吉斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層",
            "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層",
            "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層",
            "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層", "車籠埔斷層及其支斷層",
            "車籠埔斷層及其支斷層", "車籠埔支斷層(隘寮斷層)"
    };

    /**
     *
     */
    public static void saveSetting(
            Context ctx, int minML,
            int maxML, int minDeep,
            int maxDeep, int inDistance,
            int inDate, int ntfState) {

        HashMap<String, Integer> settingHashMap = new HashMap<>();
        settingHashMap.put("minML", minML);
        settingHashMap.put("maxML", maxML);
        settingHashMap.put("minDeep", minDeep);
        settingHashMap.put("maxDeep", maxDeep);
        settingHashMap.put("inDistance", inDistance);
        settingHashMap.put("inDate", inDate);
        settingHashMap.put("ntfState", ntfState);

        File file = ctx.getFileStreamPath(SETTING_PREFERENCE_FILE_NAME);
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            if (file.exists() || file.createNewFile()) {
                fos = ctx.openFileOutput(SETTING_PREFERENCE_FILE_NAME,
                        Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(settingHashMap);
                oos.flush();
                oos.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("myTag", "Fail to Write setting preference to file.");
        }
        Log.d("myTag", "Write setting preference to file. ");
    }

    /**
     *
     */
    public static int[] loadSetting(Context ctx) {
        
        HashMap<String, Integer> settingHashMap;
        File file = ctx.getFileStreamPath(SETTING_PREFERENCE_FILE_NAME);
        if (file.exists()) {
            try {
                FileInputStream fis;
                fis = ctx.openFileInput(SETTING_PREFERENCE_FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                settingHashMap = (HashMap<String, Integer>) ois.readObject();
                ois.close();
                fis.close();
                Log.d("myTag", "In Setting View, load settingHashMap from file."
                                + "\nminML:" + settingHashMap.get("minML")
                                + "\nmaxML:" + settingHashMap.get("maxML")
                                + "\nminDeep:" + settingHashMap.get("minDeep")
                                + "\nmaxDeep:" + settingHashMap.get("maxDeep")
                                + "\ninDate:" + settingHashMap.get("inDate")
                                + "\ninDistance:"
                                + settingHashMap.get("inDistance"));
                int[] settingValue = {settingHashMap.get("minML"),
                        settingHashMap.get("maxML"),
                        settingHashMap.get("minDeep"),
                        settingHashMap.get("maxDeep"),
                        settingHashMap.get("inDistance"),
                        settingHashMap.get("inDate"),
                        settingHashMap.get("ntfState")};
                return settingValue;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("myTag", "In Setting View, cannot load settingHashMap from file.");

                ConstantVariables.saveSetting(
                        ctx, 0,
                        SETTING_PREFERENCE_ML.length - 1, 0,
                        SETTING_PREFERENCE_DEPTH.length - 1,
                        SETTING_PREFERENCE_DISTANCE.length - 1,
                        SETTING_DATE_ALL,
                        SETTING_NOTIFICATION_STATE_ON);

                int[] settingValue = {0, SETTING_PREFERENCE_ML.length - 1, 0,
                        SETTING_PREFERENCE_DEPTH.length - 1, SETTING_PREFERENCE_DISTANCE.length - 1,
                        SETTING_DATE_ALL, SETTING_NOTIFICATION_STATE_ON};
                return settingValue;
            }
        } else {

            ConstantVariables.saveSetting(
                    ctx, 0,
                    SETTING_PREFERENCE_ML.length - 1, 0,
                    SETTING_PREFERENCE_DEPTH.length - 1,
                    SETTING_PREFERENCE_DISTANCE.length - 1,
                    SETTING_DATE_ALL,
                    SETTING_NOTIFICATION_STATE_ON);

            int[] settingValue = {0, SETTING_PREFERENCE_ML.length - 1, 0,
                    SETTING_PREFERENCE_DEPTH.length - 1, SETTING_PREFERENCE_DISTANCE.length - 1,
                    SETTING_DATE_ALL, SETTING_NOTIFICATION_STATE_ON};
            return settingValue;
        }
    }

    /**
     *
     */
    protected static boolean writeEQToInternalFile(
            Context ctx,
            ArrayList<HashMap<String, String>> mList,
            String filename) {

        if (mList == null || mList.size() <= 0) {
            Log.e("myTag", "writeEQToFile failed: EQList not exist.");
            return false;
        }

        File fileEQ = ctx.getFileStreamPath(filename);
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            if (fileEQ.exists() || fileEQ.createNewFile()) {
                fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(mList);
                oos.flush();
                oos.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("myTag", "Fail to Write EQlist to file.");
            ctx.deleteFile(filename);
            return false;
        }

        File newestIDFile = ctx.getFileStreamPath(NEWEST_ID_FILE_NAME);
        String newestID = mList.get(0).get("No");
        Integer tmpInteger = Integer.parseInt(newestID);
        newestID = tmpInteger.toString();
        try {
            if (newestIDFile.exists() || newestIDFile.createNewFile()) {
                fos = ctx.openFileOutput(NEWEST_ID_FILE_NAME, Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(newestID);
                oos.flush();
                oos.close();
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.deleteFile(NEWEST_ID_FILE_NAME);
            Log.e("myTag", "Fail to Write newest ID to file.");
            return false;
        }
        Log.e("myTag", "writeEQToFile.");
        return true;
    }

    /**
     *
     */
    public static Bitmap ScaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }


}
