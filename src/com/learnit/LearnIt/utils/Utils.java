package com.learnit.LearnIt.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.services.NotificationService;

import java.util.Arrays;
import java.util.Locale;

public class Utils {
    public static final String LOG_TAG = "my_logs";

    public static void updateCurrentDBName(Context context) {
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
        DBHelper.DB_WORDS = "myDB" + selectedLanguageFrom + currentLanguage;
        Log.d(LOG_TAG, "current db name is " + DBHelper.DB_WORDS);
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
        DBHelper.DB_WORDS = "myDB" + selectedLanguageFrom + currentLanguage;
        Log.d(LOG_TAG, "current db name is " + DBHelper.DB_WORDS);
        return new Pair<>(selectedLanguageFrom, currentLanguage);
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
	    while (timeInMs<System.currentTimeMillis())
	    {
		    timeInMs+=frequency;
	    }
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

}
