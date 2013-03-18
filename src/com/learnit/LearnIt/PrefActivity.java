/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;
public class PrefActivity extends PreferenceActivity {
    protected static boolean m_languages_changed = false;
    String selectedLanguageFrom;
    String selectedLanguageTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment1())
                .commit();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        selectedLanguageFrom = sp.getString(getString(R.string.key_language_from),"NONE");
        selectedLanguageTo = sp.getString(getString(R.string.key_language_to),"NONE");
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String newSelectedLanguageFrom = sp.getString(getString(R.string.key_language_from),"NONE");
        String newSelectedLanguageTo = sp.getString(getString(R.string.key_language_to),"NONE");
        if(newSelectedLanguageFrom.equals(selectedLanguageFrom) && newSelectedLanguageTo.equals(selectedLanguageTo))
        {
            this.finish();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.pref_dialog_update_dict_title).setMessage(R.string.pref_dialog_update_dict_message).setPositiveButton(R.string.ok, dialogClickListener)
                    .setNegativeButton(R.string.pref_dialog_update_dict_dismiss, dialogClickListener).setIcon(R.drawable.ic_action_alert).show();
        }


    }

    void finishActivity()
    {
        this.finish();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    startDictToSQLActivity();
                    finishActivity();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    private void startDictToSQLActivity() {
        Intent intent = new Intent(this, DictToSQL.class);
        startActivity(intent);
    }


    public static class PrefsFragment1 extends PreferenceFragment {
        private final String LOG_TAG = "my_logs";
        ListPreference lstNotifFreq;
        ListPreference lstWayToLearn;
        ListPreference lstDirectionOfTrans;
        ListPreference lstNumOfWords;
        ListPreference lstLanguageToLearn;
        ListPreference lstLanguageYouKnow;
        CheckBoxPreference checkBoxPreference;
        TimePreference timePreference;
        boolean changed = false;

        boolean updated = false;
        boolean showArticlesOption=true;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            lstNotifFreq = (ListPreference) findPreference(getString(R.string.key_notification_frequency));
            lstNotifFreq.setOnPreferenceChangeListener(listener);

            lstLanguageToLearn = (ListPreference) findPreference(getString(R.string.key_language_from));
            lstLanguageToLearn.setOnPreferenceChangeListener(listener);

            try {
                if (!lstLanguageToLearn.getValue().equals("de"))
                {
                    showArticlesOption=false;
                }
            }
            catch (Exception ex)
            {
               Log.d(LOG_TAG,"cought exception on pref start");
            }


            lstDirectionOfTrans = (ListPreference) findPreference(getString(R.string.key_direction_of_trans));
            lstDirectionOfTrans.setOnPreferenceChangeListener(listener);

            lstLanguageYouKnow = (ListPreference) findPreference(getString(R.string.key_language_to));
            lstLanguageYouKnow.setOnPreferenceChangeListener(listener);

            lstWayToLearn = (ListPreference) findPreference(getString(R.string.key_way_to_learn));
            if (showArticlesOption)
            {
                lstWayToLearn.setOnPreferenceChangeListener(listener);
            }
            else
            {
                PreferenceCategory mCategory = (PreferenceCategory) findPreference("prefs_main");    //TODO: change to R
                mCategory.removePreference(lstWayToLearn);
            }

            lstNumOfWords = (ListPreference) findPreference(getString(R.string.key_num_of_words));
            lstNumOfWords.setOnPreferenceChangeListener(listener);

            checkBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.key_pref_notif_active));
            checkBoxPreference.setOnPreferenceChangeListener(listener);

            timePreference = (TimePreference) findPreference("time_to_start");     //TODO: change to R

            updateAllSummaries();
        }

        void updateAllSummaries()
        {
            if(checkBoxPreference.isChecked())
            {
                checkBoxPreference.setSummary(R.string.pref_notifications_enabled);
            }
            else {
                checkBoxPreference.setSummary(R.string.pref_notifications_disabled);
            }
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
            if (lstDirectionOfTrans.getEntry()!=null)
                lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntry().toString());
            if (lstWayToLearn.getValue().equals("2"))
            {
                lstDirectionOfTrans.setValue("2");
                lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntries()[1]);
                lstDirectionOfTrans.setEnabled(false);
            }
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
                       if ((Boolean) newValue)
                       {
                           pref.setSummary(R.string.pref_notifications_enabled);
                       }
                       else
                       {
                           pref.setSummary(R.string.pref_notifications_disabled);
                       }
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
                        if (!newValue.toString().equals(lstLanguageToLearn.getValue()))
                        {
                            pref.setSummary(lstLanguageToLearn.getEntries()[lstLanguageToLearn.findIndexOfValue(newValue.toString())]);
                            if (!newValue.toString().equals("de"))
                            {
                                PreferenceCategory mCategory = (PreferenceCategory) findPreference("prefs_main");
                                mCategory.removePreference(lstWayToLearn);
                            }
                            else
                            {
                                PreferenceCategory mCategory = (PreferenceCategory) findPreference("prefs_main");
                                mCategory.addPreference(lstWayToLearn);
                            }
                            m_languages_changed=true;
                        }
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_language_to)))
                    {
                        lstLanguageYouKnow = (ListPreference) pref;
                        if (!newValue.toString().equals(lstLanguageYouKnow.getValue()))
                        {
                            pref.setSummary(lstLanguageYouKnow.getEntries()[lstLanguageYouKnow.findIndexOfValue(newValue.toString())]);
                            m_languages_changed=true;
                        }
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_way_to_learn)))
                    {
                        lstWayToLearn = (ListPreference) pref;
                        pref.setSummary(lstWayToLearn.getEntries()[lstWayToLearn.findIndexOfValue(newValue.toString())]);
                        if (newValue.toString().equals("2"))
                        {
                            lstDirectionOfTrans.setValue("2");
                            lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntries()[1]);
                            lstDirectionOfTrans.setEnabled(false);
                        }
                        else
                        {
                            lstDirectionOfTrans.setEnabled(true);
                        }
                        changed = true;
                        updated = true;
                        return true;
                    }
                    else if (pref.getKey().equals(getString(R.string.key_direction_of_trans)))
                    {
                        lstDirectionOfTrans = (ListPreference) pref;
                        pref.setSummary(lstDirectionOfTrans.getEntries()[lstDirectionOfTrans.findIndexOfValue(newValue.toString())]);
                        Log.d(LOG_TAG,"changed!!!!");
                        return true;
                    }
                }
                return false;
            }
        };
    }


}