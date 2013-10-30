package com.learnit.LearnIt.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Utils.startRepeatingTimer(context);
    }
}