/*
 * Copyright (C) 2014  Igor Bogoslavskyi
 * This file is part of LearnIt.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.learnit.LearnIt.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.data_types.FactoryDbHelper;
import com.learnit.LearnIt.fragments.LearnFragment;
import com.learnit.LearnIt.fragments.TaskSchedulerFragment;
import com.learnit.LearnIt.services.NotificationService;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class Utils {
    public static final String LOG_TAG = "my_logs";

    public static TaskSchedulerFragment getCurrentTaskScheduler(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment taskScheduler = fragmentManager
                .findFragmentByTag(TaskSchedulerFragment.TAG);
        if (taskScheduler == null)
        {
            taskScheduler = new TaskSchedulerFragment();
            fragmentManager.beginTransaction()
                    .add(taskScheduler, TaskSchedulerFragment.TAG)
                    .commit();
        }
        return (TaskSchedulerFragment) taskScheduler;
    }

    public static String localizeDBName(Context context, String name) {
        Pair<String, String> langs = Utils.getCurrentLanguages(context);
        return name + langs.first + langs.second;
    }

	public static int getIconForWordNumber(int wordNum)
	{
		switch (wordNum) {
			case 1:
				return R.drawable.ic_stat_one;
			case 2:
				return R.drawable.ic_stat_two;
			case 3:
				return R.drawable.ic_stat_three;
			case 4:
				return R.drawable.ic_stat_four;
			case 5:
				return R.drawable.ic_stat_five;
			case 6:
				return R.drawable.ic_stat_six;
			case 7:
				return R.drawable.ic_stat_seven;
			case 8:
				return R.drawable.ic_stat_eight;
			case 9:
				return R.drawable.ic_stat_nine;
			case 10:
				return R.drawable.ic_stat_ten;
		}
		return -1;
	}

	public static void removeOldSavedValues(SharedPreferences sp, int[] btnIds) {
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(LearnFragment.WORD_TAG);
		editor.remove(LearnFragment.CORRECT_INDEX_TAG);
		for (int i = 0; i < btnIds.length; ++i) {
			editor.remove(LearnFragment.BUTTON_PREFIX_TAG + i);
		}
		editor.commit();
	}

    public static Pair<String,String> getCurrentLanguages(Context context) {
        if (context == null)
        {
	        Log.e(LOG_TAG, "well, there is no context to get languages");
            return null;
        }
        String currentLanguage;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedLanguageFrom = sp.getString(context.getString(R.string.key_language_from), "NONE");
        String selectedLanguageTo = sp.getString(context.getString(R.string.key_language_to), "NONE");
        Resources res = context.getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        if (allLanguages.contains(selectedLanguageTo)) {
            currentLanguage = selectedLanguageTo;
        } else {
            currentLanguage = Locale.getDefault().getLanguage();
        }
        return new Pair<>(selectedLanguageFrom, currentLanguage);
    }

    public static Pair<String,String> getCurrentLanguagesFullNames(Context context) {
        Pair<String,String> langs = Utils.getCurrentLanguages(context);
        String[] langsLearnValues =
                context.getResources()
                        .getStringArray(R.array.values_languages_from);
        String[] langsHintEntries =
                context.getResources()
                        .getStringArray(R.array.add_words_language_hint);
        String langLearn = null;
        String langKnow = null;
        for (int i = 0; i < langsLearnValues.length; ++i) {
            if (langsLearnValues[i].equals(langs.first)) {
                langLearn = langsHintEntries[i];
            }
            if (langsLearnValues[i].equals(langs.second)) {
                langKnow = langsHintEntries[i];
            }
        }
        return new Pair<>(langLearn, langKnow);
    }

    public static long getFreqFromId(String id) {
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

    public static void startRepeatingTimer(Context context) {
        Log.d(LOG_TAG, "context setalarm = " + context.getClass().getName());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    long timeInMs = sp.getLong(context.getString(R.string.key_time_to_start), 0);
	    Log.d(LOG_TAG, "SET TIME IS " + timeInMs);
        String frequency_id = sp.getString(context.getString(R.string.key_notification_frequency), "-1");
        long frequency = Utils.getFreqFromId(frequency_id);
	    while (timeInMs<System.currentTimeMillis()) { timeInMs+=frequency; }
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(context.getApplicationContext(), 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMs, frequency, pi);
        Toast.makeText(context, context.getString(R.string.toast_notif_start_text), Toast.LENGTH_LONG).show();
    }
    public static void hideSoftKeyboard(Activity activity) {
	    try
	    {
	        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	    }
	    catch (NullPointerException ex)
	    {
	    }
    }

    public static boolean dictIsOnDisk(Context context, Pair<String, String> currentLanguages) {
        // check if there is a file on the disc that contains the needed dict
        // for the current language pair
        File sd = Environment.getExternalStorageDirectory();
        sd = new File(sd, "LearnIt");
        sd = new File(sd, currentLanguages.first + "-" + currentLanguages.second);
        sd = new File(sd, "dict.ifo");
        if (!sd.exists()) {
            // set the state of this database to null
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(Constants.CURRENT_HELP_DICT_TAG, "null").commit();
            // there is no needed file present, so we clear the existing database
            DBHelper dbHelper = FactoryDbHelper.createDbHelper(context, DBHelper.DB_DICT_FROM);
            dbHelper.deleteDatabase();
            return false;
        }
        return true;
    }

    public static boolean isDictUpdateNeeded(Context context) {
        Pair<String,String> currentLanguages = Utils.getCurrentLanguages(context);
        boolean dictPresent = dictIsOnDisk(context, currentLanguages);
        boolean languagesChanged = languagesHaveChanged(context, currentLanguages);
        DBHelper helper = FactoryDbHelper.createDbHelper(context, DBHelper.DB_DICT_FROM);
        if (helper.tableExists()) { return false; }
        return (dictPresent && languagesChanged);
    }

    public static boolean languagesHaveChanged(Context context, Pair<String,String> currentLanguages) {
        // check if the saved in preferences languages for the help dictionary have changed
        // if no change - then no need to reload. Otherwise - reload.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String langString = sp.getString(Constants.CURRENT_HELP_DICT_TAG, "null");
        Log.d(LOG_TAG, langString + " <> " + currentLanguages.first + " " + currentLanguages.second);
        if (langString.equals(currentLanguages.first + " " + currentLanguages.second)) {
            return false;
        }
        // well, the languages changed, so no need to show old word and translations
        Utils.removeOldSavedValues(sp, Constants.btnIdsTranslations);
        return true;
    }

}
