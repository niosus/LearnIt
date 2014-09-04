package com.learnit.LearnIt.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.services.NotificationService;
import com.learnit.LearnIt.utils.Utils;
import com.learnit.LearnIt.views.TimePreference;

/**
 * Created by igor on 04.09.14.
 */
public class PreferencesUiFragment extends PreferenceFragment {
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
    boolean showArticlesOption = true;

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
            if (!lstLanguageToLearn.getValue().equals(getString(R.string.tag_german))) {
                showArticlesOption = false;
            }
        } catch (Exception ex) {
            Log.d(LOG_TAG, "caught exception on pref start");
        }

        lstDirectionOfTrans = (ListPreference) findPreference(getString(R.string.key_direction_of_trans));
        lstDirectionOfTrans.setOnPreferenceChangeListener(listener);

        lstLanguageYouKnow = (ListPreference) findPreference(getString(R.string.key_language_to));
        lstLanguageYouKnow.setOnPreferenceChangeListener(listener);

        lstWayToLearn = (ListPreference) findPreference(getString(R.string.key_way_to_learn));
        if (showArticlesOption) {
            lstWayToLearn.setOnPreferenceChangeListener(listener);
        } else {
            PreferenceCategory mCategory
                    = (PreferenceCategory) findPreference(getString(R.string.key_prefs_main));
            mCategory.removePreference(lstWayToLearn);
            lstWayToLearn.setValue(getString(R.string.val_way_to_learn_trans_only));
        }

        lstNumOfWords = (ListPreference) findPreference(getString(R.string.key_num_of_words));
        lstNumOfWords.setOnPreferenceChangeListener(listener);

        checkBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.key_pref_notif_active));
        checkBoxPreference.setOnPreferenceChangeListener(listener);

        timePreference = (TimePreference) findPreference(getString(R.string.key_time_to_start));
        timePreference.setOnPreferenceChangeListener(listener);


        updateAllSummaries();
    }

    void updateAllSummaries() {
        SharedPreferences sp
                = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (checkBoxPreference.isChecked()) {
            sp.edit().putBoolean(getString(R.string.key_pref_notif_active), true);
            checkBoxPreference.setSummary(R.string.pref_notifications_enabled);
        } else {
            sp.edit().putBoolean(getString(R.string.key_pref_notif_active), false);
            checkBoxPreference.setSummary(R.string.pref_notifications_disabled);
        }
        if (lstNotifFreq.getEntry() != null)
            lstNotifFreq.setSummary(lstNotifFreq.getEntry().toString());
        if (lstLanguageToLearn.getEntry() != null)
            lstLanguageToLearn.setSummary(lstLanguageToLearn.getEntry().toString());
        if (lstLanguageYouKnow.getEntry() != null)
            lstLanguageYouKnow.setSummary(lstLanguageYouKnow.getEntry().toString());
        if (lstWayToLearn.getEntry() != null)
            lstWayToLearn.setSummary(lstWayToLearn.getEntry().toString());
        if (lstNumOfWords.getEntry() != null)
            lstNumOfWords.setSummary(lstNumOfWords.getEntry().toString());
        if (lstDirectionOfTrans.getEntry() != null)
            lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntry().toString());
        if (lstWayToLearn.getValue().equals(getString(R.string.val_way_to_learn_articles_only))) {
            lstDirectionOfTrans.setValue(getString(R.string.val_direction_of_trans_show_word));
            lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntries()[0]);
            lstDirectionOfTrans.setEnabled(false);
        } else {
            lstDirectionOfTrans.setValue(getString(R.string.val_direction_of_trans_mixed));
            lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntries()[2]);
            lstDirectionOfTrans.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEnabled();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "number of Words in Prefs = " + lstNumOfWords.getValue());
        updateNotificationTimer();
    }

    void updateEnabled() {
        boolean enabled = checkBoxPreference.isChecked();
        lstNotifFreq.setEnabled(enabled);
        lstNumOfWords.setEnabled(enabled);
        timePreference.setEnabled(enabled);
    }

    void updateNotificationTimer() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if (changed) {
            boolean notif_enabled = sp.getBoolean(getString(R.string.key_pref_notif_active), false);
            if (notif_enabled) {
                Utils.startRepeatingTimer(this.getActivity());
            } else {
                cancelRepeatingTimer();
            }
        }
    }

    public void cancelRepeatingTimer() {
        Context context = this.getActivity();
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Toast.makeText(context, context.getString(R.string.toast_notif_stop_text), Toast.LENGTH_LONG).show();
    }


    Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference pref, Object newValue) {
            if (pref instanceof CheckBoxPreference) {
                if (pref.getKey().equals(getString(R.string.key_pref_notif_active))) {
                    lstNotifFreq.setEnabled((Boolean) newValue);
                    timePreference.setEnabled(((Boolean) newValue));
                    lstNumOfWords.setEnabled((Boolean) newValue);
                    if ((Boolean) newValue) {
                        pref.setSummary(R.string.pref_notifications_enabled);
                    } else {
                        pref.setSummary(R.string.pref_notifications_disabled);
                    }
                    changed = true;
                    return true;
                }
            } else if (pref instanceof ListPreference) {
                if (pref.getKey().equals(getString(R.string.key_notification_frequency))) {
                    lstNotifFreq = (ListPreference) pref;
                    pref.setSummary(lstNotifFreq.getEntries()[lstNotifFreq.findIndexOfValue(newValue.toString())]);
                    changed = true;
                    updated = true;
                    return true;
                } else if (pref.getKey().equals(getString(R.string.key_num_of_words))) {
                    lstNumOfWords = (ListPreference) pref;
                    pref.setSummary(lstNumOfWords.getEntries()[lstNumOfWords.findIndexOfValue(newValue.toString())]);
                    changed = true;
                    updated = true;
                    return true;
                } else if (pref.getKey().equals(getString(R.string.key_language_from))) {
                    lstLanguageToLearn = (ListPreference) pref;
                    if (!newValue.toString().equals(lstLanguageToLearn.getValue())) {
                        pref.setSummary(lstLanguageToLearn.getEntries()[lstLanguageToLearn.findIndexOfValue(newValue.toString())]);
                        if (!newValue.toString().equals(getString(R.string.tag_german))) {
                            PreferenceCategory mCategory
                                    = (PreferenceCategory) findPreference(getString(R.string.key_prefs_main));
                            lstWayToLearn.setValue(getString(R.string.val_way_to_learn_trans_only));
                            mCategory.removePreference(lstWayToLearn);
                        } else {
                            PreferenceCategory mCategory
                                    = (PreferenceCategory) findPreference(getString(R.string.key_prefs_main));
                            mCategory.addPreference(lstWayToLearn);
                        }
                    }
                    return true;
                } else if (pref.getKey().equals(getString(R.string.key_language_to))) {
                    lstLanguageYouKnow = (ListPreference) pref;
                    if (!newValue.toString().equals(lstLanguageYouKnow.getValue())) {
                        pref.setSummary(lstLanguageYouKnow.getEntries()[lstLanguageYouKnow.findIndexOfValue(newValue.toString())]);
                    }
                    return true;
                } else if (pref.getKey().equals(getString(R.string.key_way_to_learn))) {
                    lstWayToLearn = (ListPreference) pref;
                    pref.setSummary(lstWayToLearn.getEntries()[lstWayToLearn.findIndexOfValue(newValue.toString())]);
                    if (newValue.toString().equals(getString(R.string.val_way_to_learn_articles_only))) {
                        lstDirectionOfTrans.setValue(getString(R.string.val_way_to_learn_articles_only));
                        lstDirectionOfTrans.setSummary(lstDirectionOfTrans.getEntries()[0]);
                        lstDirectionOfTrans.setEnabled(false);
                    } else {
                        lstDirectionOfTrans.setEnabled(true);
                    }
                    changed = true;
                    updated = true;
                    return true;
                } else if (pref.getKey().equals(getString(R.string.key_direction_of_trans))) {
                    lstDirectionOfTrans = (ListPreference) pref;
                    pref.setSummary(lstDirectionOfTrans.getEntries()[lstDirectionOfTrans.findIndexOfValue(newValue.toString())]);
                    return true;
                }
            } else if (pref instanceof TimePreference) {
                changed = true;
                updated = true;
                return true;
            }
            return false;
        }
    };
}
