/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity {
    private final String LOG_TAG = "my_logs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment1())
                .commit();
    }



    public static class PrefsFragment1 extends PreferenceFragment {
        private final String LOG_TAG = "my_logs";
        ListPreference lstNotifFreq;
        ListPreference lstWayToLearn;
        ListPreference lstNumOfWords;
        ListPreference lstLanguageToLearn;
        ListPreference lstLanguageYouKnow;
        CheckBoxPreference checkBoxPreference;
        TimePreference timePreference;
        boolean changed = false;
        boolean updated = false;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            lstNotifFreq = (ListPreference) findPreference(getString(R.string.key_notification_frequency));
            lstNotifFreq.setOnPreferenceChangeListener(listener);

            lstLanguageToLearn = (ListPreference) findPreference(getString(R.string.key_language_from));
            lstLanguageToLearn.setOnPreferenceChangeListener(listener);

            lstLanguageYouKnow = (ListPreference) findPreference(getString(R.string.key_language_to));
            lstLanguageYouKnow.setOnPreferenceChangeListener(listener);

            lstWayToLearn = (ListPreference) findPreference(getString(R.string.key_way_to_learn));
            lstWayToLearn.setOnPreferenceChangeListener(listener);

            lstNumOfWords = (ListPreference) findPreference(getString(R.string.key_num_of_words));
            lstNumOfWords.setOnPreferenceChangeListener(listener);

            checkBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.key_pref_notif_active));
            checkBoxPreference.setOnPreferenceChangeListener(listener);
            timePreference = (TimePreference) findPreference("time_to_start");

            updateAllSummaries();
        }

        void updateAllSummaries()
        {
            if (lstNotifFreq.getEntry()!=null)
                lstNotifFreq.setSummary(lstNotifFreq.getEntry().toString());
            if (lstLanguageToLearn.getEntry()!=null)
                lstLanguageToLearn.setSummary(lstLanguageToLearn.getEntry().toString());
            if (lstLanguageYouKnow.getEntry()!=null)
                lstLanguageYouKnow.setSummary(lstLanguageYouKnow.getEntry().toString());
            if (lstWayToLearn.getEntry()!=null)
                lstWayToLearn.setSummary(lstWayToLearn.getEntry().toString());
            if (lstNumOfWords.getEntry()!=null)
                lstNumOfWords.setSummary(lstNumOfWords.getEntry().toString());
        }

        @Override
        public void onResume()
        {
            super.onResume();
            updateEnabled();
        }

        @Override
        public void onPause()
        {
            super.onPause();
            updateNotificationTimer();
        }

        void updateEnabled()
        {
            boolean enabled = checkBoxPreference.isChecked();
            lstNotifFreq.setEnabled(enabled);
            lstNumOfWords.setEnabled(enabled);
            timePreference.setEnabled(enabled);
        }

        void updateNotificationTimer()
        {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            if (changed)
            {
                boolean notif_enabled = sp.getBoolean(getString(R.string.key_pref_notif_active), false);
                String frequency_id = sp.getString(getString(R.string.key_notification_frequency), "-1");
                if (notif_enabled) {
                    startNotificationTimer(getFreqFromId(frequency_id));
                } else if (!notif_enabled) {
                    stopTimer();
                }
            }
        }

        public void startNotificationTimer(long notif_freq) {

            Intent myIntent = new Intent(this.getActivity(),
                    MyAlarmService.class);

            PendingIntent pendingIntent = PendingIntent.getService(this.getActivity(), 0,
                    myIntent, 0);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            Long time = sp.getLong("time_to_start", 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    time, notif_freq, pendingIntent);
            if (updated)
            {
                Toast.makeText(this.getActivity(), R.string.toast_notif_update_text, Toast.LENGTH_LONG)
                    .show();
            }
            else
            {
                Toast.makeText(this.getActivity(), R.string.toast_notif_start_text, Toast.LENGTH_LONG)
                        .show();
            }
        }

        private void stopTimer() {
            Intent myIntent = new Intent(this.getActivity(), MyAlarmService.class);

            PendingIntent pendingIntent = PendingIntent.getService(this.getActivity(), 0,
                    myIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(this.getActivity(), R.string.toast_notif_stop_text, Toast.LENGTH_LONG)
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

        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object newValue) {
                if (pref instanceof CheckBoxPreference)
                {
                   if (pref.getKey().equals(getString(R.string.key_pref_notif_active)))
                   {
                       lstNotifFreq.setEnabled((Boolean) newValue);
                       timePreference.setEnabled(((Boolean) newValue));
                       lstNumOfWords.setEnabled((Boolean) newValue);
                       changed=true;
                       return true;
                   }
                }
                else if (pref instanceof ListPreference) {
                    if (pref.getKey().equals(getString(R.string.key_notification_frequency)))
                    {
                        lstNotifFreq = (ListPreference) pref;
                        pref.setSummary(lstNotifFreq.getEntries()[lstNotifFreq.findIndexOfValue(newValue.toString())]);
                        changed = true;
                        updated = true;
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_num_of_words)))
                    {
                        lstNumOfWords = (ListPreference) pref;
                        pref.setSummary(lstNumOfWords.getEntries()[lstNumOfWords.findIndexOfValue(newValue.toString())]);
                        changed = true;
                        updated = true;
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_language_from)))
                    {
                        lstLanguageToLearn = (ListPreference) pref;
                        pref.setSummary(lstLanguageToLearn.getEntries()[lstLanguageToLearn.findIndexOfValue(newValue.toString())]);
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_language_to)))
                    {
                        lstLanguageYouKnow = (ListPreference) pref;
                        pref.setSummary(lstLanguageYouKnow.getEntries()[lstLanguageYouKnow.findIndexOfValue(newValue.toString())]);
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_way_to_learn)))
                    {
                        lstWayToLearn = (ListPreference) pref;
                        pref.setSummary(lstWayToLearn.getEntries()[lstWayToLearn.findIndexOfValue(newValue.toString())]);
                        changed = true;
                        updated = true;
                        return true;
                    }
                }
                return false;
            }
        };
    }


}