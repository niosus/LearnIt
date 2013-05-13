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
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.services.NotificationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Pair<String,String> getCurrentLanguages(Context context) {
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
        return new Pair<String, String>(selectedLanguageFrom, currentLanguage);
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
        String frequency_id = sp.getString(context.getString(R.string.key_notification_frequency), "-1");
        long frequency = Utils.getFreqFromId(frequency_id);
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(context.getApplicationContext(), 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), frequency, pi);
        Toast.makeText(context, context.getString(R.string.toast_notif_start_text), Toast.LENGTH_LONG).show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
