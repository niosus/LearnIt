package com.learnit.LearnIt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver
{
    public static final String LOG_TAG = "my_logs";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        NotificationBuilder notificationBuilder = new NotificationBuilder(context.getApplicationContext());

        wl.release();
    }

    public void SetAlarm(Context context)
    {
        Log.d(LOG_TAG,"context setalarm = " + context.getClass().getName());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String frequency_id = sp.getString(context.getString(R.string.key_notification_frequency), "-1");
        long frequency = getFreqFromId(frequency_id);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), frequency, pi);
        Toast.makeText(context, context.getString(R.string.toast_notif_start_text), Toast.LENGTH_LONG).show();
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Toast.makeText(context, context.getString(R.string.toast_notif_stop_text), Toast.LENGTH_LONG).show();
    }


    protected long getFreqFromId(String id) {
        int idInt = Integer.parseInt(id);
        switch (idInt) {
            case 1:
                return AlarmManager.INTERVAL_HOUR;
            case 2:
                return 2 * AlarmManager.INTERVAL_HOUR;
            case 3:
                return 4 * AlarmManager.INTERVAL_HOUR;
            case 4:
                return AlarmManager.INTERVAL_HALF_DAY;
            case 5:
                return AlarmManager.INTERVAL_DAY;
            default:
                return -1;
        }
    }
}