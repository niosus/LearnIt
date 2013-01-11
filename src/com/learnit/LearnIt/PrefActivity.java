package com.learnit.LearnIt;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class PrefActivity extends PreferenceActivity {
    ListPreference listPrefTimes;
    private final String LOG_TAG = "my_logs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        Log.d(LOG_TAG,"pref init from xml");
        listPrefTimes = (ListPreference) findPreference("notification_frequency");
        listPrefTimes.setOnPreferenceChangeListener(listener);
        if (listPrefTimes.getEntry()!=null)
            listPrefTimes.setSummary(listPrefTimes.getEntry().toString());
        Log.d(LOG_TAG,"pref activity started");
    }

    OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference pref, Object newValue) {
            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference) pref;
                pref.setSummary(listPref.getEntries()[listPref.findIndexOfValue(newValue.toString())]);
            }
            return true;
        }
    };


}