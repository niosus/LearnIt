package com.learnit.LearnIt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootSetter extends BroadcastReceiver {
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notif_enabled = sp.getBoolean(context.getString(R.string.key_pref_notif_active), false);
        String frequency_id = sp.getString(context.getString(R.string.key_notification_frequency), "-1");
        if (notif_enabled) {
            startNotificationTimer(getFreqFromId(frequency_id));
        }
    }

    public void startNotificationTimer(long notif_freq) {

        Intent myIntent = new Intent(mContext,
                MyAlarmService.class);

        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0,
                myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        Long time = sp.getLong("time_to_start", 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                time, notif_freq, pendingIntent);
        Toast.makeText(mContext, R.string.toast_notif_start_text, Toast.LENGTH_LONG)
                .show();
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
