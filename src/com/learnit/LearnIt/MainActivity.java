/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.learnit.LearnIt.utils.Utils;

import java.util.Arrays;

public class MainActivity extends FragmentActivity {

    final String LOG_TAG = "my_logs";

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    protected void onResume() {
        super.onResume();
        Utils utils = new Utils();
        Pair<String, String> pair = utils.getCurrentLanguages(this);
        Resources res = getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        Log.d(LOG_TAG, "possible languages = " + allLanguages);
        if (!allLanguages.contains(pair.first)) {
            startShowWellcomeActivity();
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity called");
    }

    private void startAboutActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start info activity called");
    }

    private void startShowWordsActivity() {
        Intent intent = new Intent(this, ShowAllWordsActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity called");
    }

    private void startShowWellcomeActivity() {
        Intent intent = new Intent(this, WellcomeActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity welcome");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DBHelper dbHelper;
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Log.d(LOG_TAG, "pref button pressed");
                startSettingsActivity();
                return true;
            case R.id.menu_export:
                Log.d(LOG_TAG, "export DB");
                dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
                dbHelper.exportDB();
                return true;
            case R.id.menu_import:
                Log.d(LOG_TAG, "import DB");
                dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
                dbHelper.importDB();
                dbHelper.close();
                Utils utils = new Utils();
                utils.getCurrentLanguages(this);
                return true;
            case R.id.menu_show_all_words:
                Log.d(LOG_TAG, "show all words");
                startShowWordsActivity();
                return true;
            case R.id.menu_info:
                Log.d(LOG_TAG, "show about");
                startAboutActivity();
                return true;
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
