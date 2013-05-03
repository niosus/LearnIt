package com.learnit.LearnIt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "context set alarm = " + context.getClass().getName());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String frequency_id = sp.getString(context.getApplicationContext().getString(R.string.key_notification_frequency), "-1");
        long frequency = Utils.getFreqFromId(frequency_id);
        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context.getApplicationContext(), NotificationService.class);
        PendingIntent pi = PendingIntent.getService(context.getApplicationContext(), 0, i, 0);
        am.cancel(pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), frequency, pi);
        Toast.makeText(context.getApplicationContext(), context.getApplicationContext().getString(R.string.toast_notif_start_text), Toast.LENGTH_LONG).show();
    }
}