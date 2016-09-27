package sinica.earth.tesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GcmIntentService extends IntentService {

    private static final String TAG = "GcmIntentService";

    private HashMap<String, String> mNewEarthquakeMap = new HashMap<String, String>();

    GcmIntentService gcmIntentService;

    public GcmIntentService() {
        super("GcmIntentService");
        gcmIntentService = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        int lastSendNFID;

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
                // sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                // sendNotification("Deleted messages on server: "
                // + extras.toString());
                Log.i(TAG, "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                Log.d(TAG, "download new earthquake");
                if (HomeActivity.isConnected(this)) {


// johnson comment 8/23
//                    mNewEarthquakeMap = EarthquakeData.getDataFromURL(extras
//                            .getString("message"));
//                    Log.i(TAG, "Received: " + extras.toString());
//                    Log.i(TAG,
//                            "Download Earthquake: "
//                                    + mNewEarthquakeMap.toString());

                }
                // 0415 check newest send Notification id
                File newestIDFile = this
                        .getFileStreamPath(ConstantVariables.NEWEST_SEND_NF_FILE_NAME);
                if (newestIDFile.exists()) {
                    try {
                        FileInputStream fis;
                        fis = this.openFileInput(ConstantVariables.NEWEST_SEND_NF_FILE_NAME);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        String newestID = (String) ois.readObject();
                        ois.close();
                        fis.close();
                        Log.d("myTag", "load newest ID from file:" + newestID);
                        lastSendNFID = Integer.parseInt(newestID);
                        if (Integer.parseInt(mNewEarthquakeMap.get("No")) > lastSendNFID) {
                            sendNotification(mNewEarthquakeMap);
                            //TODO save new ID
                            newestID = mNewEarthquakeMap.get("No");
                            Integer tmpInteger = Integer.parseInt(newestID);
                            newestID = tmpInteger.toString();
                            try {
                                if (newestIDFile.exists() || newestIDFile.createNewFile()) {
                                    FileOutputStream fos = this.openFileOutput(ConstantVariables.NEWEST_SEND_NF_FILE_NAME, MODE_PRIVATE);
                                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                                    oos.writeObject(newestID);
                                    oos.flush();
                                    oos.close();
                                    fos.close();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                this.deleteFile(ConstantVariables.NEWEST_SEND_NF_FILE_NAME);
                                Log.e("myTag", "Fail to Write newest NF ID to file.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sendNotification(mNewEarthquakeMap);
                    //TODO save new ID
                    String newestID = mNewEarthquakeMap.get("No");
                    Integer tmpInteger = Integer.parseInt(newestID);
                    newestID = tmpInteger.toString();
                    try {
                        if (newestIDFile.createNewFile()) {
                            FileOutputStream fos = this.openFileOutput(ConstantVariables.NEWEST_SEND_NF_FILE_NAME, MODE_PRIVATE);
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(newestID);
                            oos.flush();
                            oos.close();
                            fos.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        this.deleteFile(ConstantVariables.NEWEST_SEND_NF_FILE_NAME);
                        Log.e("myTag", "Fail to Write newest NF ID to file.");
                    }
                }

//				sendNotification(mNewEarthquakeMap);
                if (HomeActivity.isConnected(getApplicationContext())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

//johnson comment 8/23
//                        new updateGeneralEQ().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                    } else {

// joshnson comment 8/23
//                        new updateGeneralEQ().execute();


                    }
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(HashMap<String, String> hMap) {

        NotificationManager manager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, summaryActivity.class);
        intent.putExtra("earthquakeInfo", hMap);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(summaryActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(
                this)
                .setContentTitle("規模" + hMap.get("ml") + "新地震")
                .setContentText(hMap.get("DateAndTime"))
                .setSmallIcon(R.drawable.small_alert)
                .setLargeIcon(
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_launcher)).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        manager.notify(0, nfBuilder.build());
    }

}
