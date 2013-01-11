package com.learnit.LearnIt;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends FragmentActivity {
    private static final int NONE = -1;

    private static long NOTIF_FREQ = -1;
    private static Boolean NOTIF_ENABLED = false;

    private static long currentAlarmFreqRunning = -1;

    private PendingIntent pendingIntent;

    SectionsPagerAdapter mSectionsPagerAdapter;

    AlertDialog.Builder builder;

    SharedPreferences sp;

    ViewPager mViewPager;

    final String LOG_TAG = "my_logs";

    public static DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        dbHelper = new DBHelper(this);

        builder = new AlertDialog.Builder(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String frequency_id = sp.getString("notification_frequency", "-1");
        getFreqFromId(frequency_id);
        currentAlarmFreqRunning = NOTIF_FREQ;
    }

    private void getFreqFromId(String id) {
        int idInt = Integer.parseInt(id);
        switch (idInt) {
            case 1:
                NOTIF_FREQ = AlarmManager.INTERVAL_HOUR;
                break;
            case 2:
                NOTIF_FREQ = 2 * AlarmManager.INTERVAL_HOUR;
                break;
            case 3:
                NOTIF_FREQ = 4 * AlarmManager.INTERVAL_HOUR;
                break;
            case 4:
                NOTIF_FREQ = AlarmManager.INTERVAL_HALF_DAY;
                break;
            case 5:
                NOTIF_FREQ = AlarmManager.INTERVAL_DAY;
                break;
        }
    }

    protected void onResume() {
        super.onResume();

        NOTIF_ENABLED = sp.getBoolean("notification_enabled", false);
        String frequency_id = sp.getString("notification_frequency", "-1");
        if (NOTIF_ENABLED) {
            startNotificationTimer();
        } else if (!NOTIF_ENABLED) {
            stopTimer();
        }
        Log.d(LOG_TAG,"main activity on resume + Notif Enabled = " + NOTIF_ENABLED);
        getFreqFromId(frequency_id);
    }

    public void startNotificationTimer() {
        if (currentAlarmFreqRunning != NOTIF_FREQ) {
            Intent myIntent = new Intent(MainActivity.this,
                    MyAlarmService.class);

            pendingIntent = PendingIntent.getService(MainActivity.this, 0,
                    myIntent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), NOTIF_FREQ, pendingIntent);

            Toast.makeText(MainActivity.this, R.string.toast_notif_text, Toast.LENGTH_LONG)
                    .show();
            currentAlarmFreqRunning = NOTIF_FREQ;
        }
    }

    private void stopTimer() {
        Intent myIntent = new Intent(MainActivity.this, MyAlarmService.class);

        pendingIntent = PendingIntent.getService(MainActivity.this, 0,
                myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        currentAlarmFreqRunning = NONE;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG,"start activity called");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Log.d(LOG_TAG, "pref button pressed");
                startSettingsActivity();
                return true;
            case R.id.menu_export:
                Log.d(LOG_TAG, "export DB");
                dbHelper.exportDB();
            case R.id.menu_import:
                Log.d(LOG_TAG, "import DB");
                dbHelper.importDB();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new DictFragment();
                    return fragment;
                case 1:
                    fragment = new AddWordFragment();
                    return fragment;
                case 2:
                    fragment = new LearnFragment();
                    return fragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.dictionary_frag_title).toUpperCase();
                case 1:
                    return getString(R.string.add_words_frag_title).toUpperCase();
                case 2:
                    return getString(R.string.learn_words_frag_title).toUpperCase();
            }
            return null;
        }
    }



}
