/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

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
        ListPreference listPrefTimes;
        CheckBoxPreference checkBoxPreference;
        TimePreference timePreference;
        boolean changed = false;
        boolean updated = false;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            listPrefTimes = (ListPreference) findPreference(getString(R.string.notification_frequency));
            listPrefTimes.setOnPreferenceChangeListener(listener);

            checkBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_notif_active));
            checkBoxPreference.setOnPreferenceChangeListener(listener);
            timePreference = (TimePreference) findPreference("time_to_start");

            if (listPrefTimes.getEntry()!=null)
                listPrefTimes.setSummary(listPrefTimes.getEntry().toString());
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
            listPrefTimes.setEnabled(enabled);
            timePreference.setEnabled(enabled);
        }

        void updateNotificationTimer()
        {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            if (changed)
            {
                boolean notif_enabled = sp.getBoolean(getString(R.string.pref_notif_active), false);
                String frequency_id = sp.getString(getString(R.string.notification_frequency), "-1");
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
                   if (pref.getKey().toString()==getString(R.string.pref_notif_active))
                   {
                       listPrefTimes.setEnabled((Boolean) newValue);
                       timePreference.setEnabled(((Boolean) newValue));
                       changed=true;
                       return true;
                   }
                }
                else if (pref instanceof ListPreference) {
                    if (pref.getKey().toString()==getString(R.string.notification_frequency))
                    {
                        listPrefTimes = (ListPreference) pref;
                        pref.setSummary(listPrefTimes.getEntries()[listPrefTimes.findIndexOfValue(newValue.toString())]);
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