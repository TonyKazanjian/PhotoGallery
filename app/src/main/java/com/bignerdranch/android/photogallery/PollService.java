package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by tonyk_000 on 1/26/2016.
 */
public class PollService extends IntentService {
    private static final String TAG = "PollService";

    private static final long POLL_INTERVAL = 1000 * 60; //60 seconds

            //AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
    }

    /*
    turns alarm on and off
     */
    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = PollService.newIntent(context);
        //PendingIntent tells AlarmManager what intent to send
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            //setInexactRepeating allows the system to batch your alarm up with others to minimize wake time,
            //hence time between repetitions is not exact
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        //saving the preference of the alarm being on
        QueryPreferences.setAlarmOn(context, isOn);
    }
    /*
    checking if PendingIntent is active to see whether or not the alarm is active.
    That's what the FLAG_NO_CREATE constant is for - it returns null if the PendingIntent does not already exist
     */
    public static boolean isServiceAlarmOn(Context context){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public PollService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //makng sure the app doesn't try to download data if it isn't connected to a network
        if(!isNetworkAvailableAndConnected()){
            return;
        }

        /*
        The service is polling for new results, so we need to know what the last result fetched was.
         */
        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        // Fetching the lastest results. If there are results, grab the first one.
        if (query == null){
            items = new FlickrFetchr().fetchRecentPhotos();
        } else {
            items = new FlickrFetchr().searchPhotos(query);
        }

        if (items.size() == 0){
            return;
        }

        //Checking to see if the result is different from the last result ID
        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);

            //creating notification to notify user of new result
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            //creates notification and calles NotificationManager
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    //with setAutoCancel, the notification is deleted from the drawer when the user presses it
                    .setAutoCancel(true)
                    .build();

            showBackgroundNotification(0, notification);
        }

        //storing the first result back in SharedPreferences
        QueryPreferences.setLastResultId(this, resultId);
    }

    //sends broadcast when new search results are available, so we can filter foreground notifications
    //using private permissions declared in manifest
    private void showBackgroundNotification(int requestCode, Notification notification){
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    // method for checking network availability
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // mking sure the background data setting is toggled to allow downloading data in the background
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
