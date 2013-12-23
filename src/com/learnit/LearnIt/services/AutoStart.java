package com.learnit.LearnIt.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.utils.Utils;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		if (sp.getBoolean(context.getString(R.string.key_pref_notif_active), false))
            Utils.startRepeatingTimer(context);
    }
}