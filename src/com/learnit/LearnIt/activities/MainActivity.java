/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
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
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.fragments.*;
import com.learnit.LearnIt.utils.Utils;

import java.util.Arrays;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, ListOfFragments.OnFragmentSelectedListener {

    final String LOG_TAG = "my_logs";
    public static int NUMBER_OF_FRAGMENTS = 3;
    public static final  int DICTIONARY_FRAGMENT = 1;
    public static final int ADD_WORDS_FRAGMENT = 0;
    public static final int LEARN_WORDS_FRAGMENT = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        //this means that we have loaded a bigger layout
        if (mViewPager!=null)
        {
            // Specify that the Home/Up button should not be enabled, since there is no hierarchical
            // parent.
            actionBar.setHomeButtonEnabled(false);

            // Specify that we will be displaying tabs in the action bar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mViewPager.setAdapter(mAppSectionsPagerAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    // When swiping between different app sections, select the corresponding tab.
                    // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                    // Tab.
                    actionBar.setSelectedNavigationItem(position);
                }
            });
        }

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onArticleSelected(int position) {
        // Create a new fragment
        Fragment fragment;
        switch(position)
        {
            case DICTIONARY_FRAGMENT:
                fragment = new DictFragment();
                break;
            case ADD_WORDS_FRAGMENT:
                fragment = new AddWordFragment();
                break;
            case LEARN_WORDS_FRAGMENT:
                fragment = new LearnFragment();
                break;
            default: fragment = new DictFragment();
        }
        // Update the layout
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.float_in_left, R.anim.float_away_right);
        ft.replace(R.id.view_group_id, fragment);
        ft.commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        if (mViewPager!=null)
            mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case DICTIONARY_FRAGMENT:
                    return new DictFragment();
                case ADD_WORDS_FRAGMENT:
                    return new AddWordFragment();
                case LEARN_WORDS_FRAGMENT:
                    return new LearnFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Resources resources = getResources();
            switch (position) {
                case DICTIONARY_FRAGMENT:
                    return resources.getString(R.string.dictionary_frag_title);
                case ADD_WORDS_FRAGMENT:
                    return resources.getString(R.string.add_words_frag_title);
                case LEARN_WORDS_FRAGMENT:
                    return resources.getString(R.string.learn_words_frag_title);
            }
            return null;
        }
    }
    protected void onResume() {
        super.onResume();
        Pair<String, String> pair = Utils.getCurrentLanguages(this);
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
                Utils.getCurrentLanguages(this);
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
}
