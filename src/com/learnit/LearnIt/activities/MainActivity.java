
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

package com.learnit.LearnIt.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.astuetz.PagerSlidingTabStrip;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.AppSectionsPagerAdapter;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.data_types.FactoryDbHelper;
import com.learnit.LearnIt.fragments.AddWordFragment;
import com.learnit.LearnIt.fragments.DictFragment;
import com.learnit.LearnIt.fragments.LearnCasualFragment;
import com.learnit.LearnIt.fragments.ListOfFragments;
import com.learnit.LearnIt.fragments.MySmartFragment;
import com.learnit.LearnIt.fragments.TaskSchedulerFragment;
import com.learnit.LearnIt.utils.Utils;

import java.util.Arrays;

public class MainActivity extends Activity implements
		ActionBar.TabListener,
		ListOfFragments.OnFragmentSelectedListener{

    final String LOG_TAG = "my_logs";
    public static int NUMBER_OF_FRAGMENTS = 3;
    public static final int ADD_WORDS_FRAGMENT = 0;
    public static final int DICTIONARY_FRAGMENT = 1;
    public static final int LEARN_WORDS_FRAGMENT = 2;
    public static final String LAYOUT_NORMAL = "normal";
    public static final String LAYOUT_XLARGE = "xlarge";
    public static final String LAYOUT_LARGE_LAND = "large_landscape";


    private AppSectionsPagerAdapter _appSectionsPagerAdapter;
    private ViewPager _viewPager;
    private ListFragment _listOfFragments;
	private TaskSchedulerFragment _taskScheduler;

    private static int _currentItemShown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) { actionBar.setTitle(""); }
        String currentLayout = getString(R.string.layout_current);
	    FragmentManager fragmentManager = getFragmentManager();
	    _taskScheduler = Utils.getCurrentTaskScheduler(this);
	    switch (currentLayout) {
		    case LAYOUT_XLARGE:
			    MySmartFragment dictFragment = (MySmartFragment) getFragmentManager().findFragmentByTag(DictFragment.TAG);
			    if (dictFragment == null) {
				    dictFragment = DictFragment.newInstance(_taskScheduler);
				    dictFragment.identifier = DICTIONARY_FRAGMENT;
				    fragmentManager.beginTransaction()
						    .add(R.id.dict_frame, dictFragment, DictFragment.TAG)
						    .commit();
			    }
			    MySmartFragment addWordFragment = (MySmartFragment) getFragmentManager().findFragmentByTag(AddWordFragment.TAG);
			    if (addWordFragment == null) {
				    addWordFragment = AddWordFragment.newInstance(_taskScheduler);
				    addWordFragment.identifier = ADD_WORDS_FRAGMENT;
				    fragmentManager.beginTransaction()
						    .add(R.id.add_words_frame, addWordFragment, AddWordFragment.TAG)
						    .commit();
			    }
			    MySmartFragment learnFragment = (MySmartFragment) getFragmentManager().findFragmentByTag(LearnCasualFragment.TAG);
			    if (learnFragment == null) {
				    learnFragment = LearnCasualFragment.newInstance(_taskScheduler);
				    learnFragment.identifier = LEARN_WORDS_FRAGMENT;
				    fragmentManager.beginTransaction()
						    .add(R.id.learn_frame, learnFragment, LearnCasualFragment.TAG)
						    .commit();
			    }
			    break;
		    case LAYOUT_LARGE_LAND:
			    _appSectionsPagerAdapter = new AppSectionsPagerAdapter(getFragmentManager(), this, _taskScheduler);
			    //Do some special processing for large screen
			    ListOfFragments fragment = (ListOfFragments) getFragmentManager().findFragmentById(R.id.headlines_fragment);
			    fragment.getListView().setSelection(0);
			    break;
		    case LAYOUT_NORMAL:
			    // Initialize the view pager
			    // Create the adapter that will return a listOfFragments for each of the three primary sections
			    // of the app.
			    _appSectionsPagerAdapter = new AppSectionsPagerAdapter(getFragmentManager(), this, _taskScheduler);

			    final PagerSlidingTabStrip strip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

			    // Set up the ViewPager, attaching the adapter and setting up a listener for when the
			    // user swipes between sections.
			    _viewPager = (ViewPager) findViewById(R.id.pager);
			    _viewPager.setAdapter(_appSectionsPagerAdapter);
			    strip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				    @Override
				    public void onPageSelected(int position) {
					    _currentItemShown = position;
					    Log.d(LOG_TAG, "current position updated");
				    }
			    });
			    strip.setViewPager(_viewPager);
			    strip.setBackgroundColor(this.getResources().getColor(R.color.white));
			    strip.setUnderlineColor(this.getResources().getColor(R.color.highlight));
			    strip.setIndicatorColor(this.getResources().getColor(R.color.highlight_lighter));
			    strip.setIndicatorHeight(100);
			    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			    break;
	    }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_viewPager !=null)
        {
            _currentItemShown =  _viewPager.getCurrentItem();
	        Log.d(LOG_TAG, "onPause current item set to " + _currentItemShown);
        }
        if (_listOfFragments !=null && _listOfFragments.isInLayout())
        {
            _currentItemShown = _listOfFragments.getListView().getCheckedItemPosition();
	        Log.d(LOG_TAG, "onPause current item set to " + _currentItemShown);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", _currentItemShown);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _listOfFragments = (ListOfFragments) getFragmentManager().findFragmentById(R.id.headlines_fragment);
        _viewPager = (ViewPager) findViewById(R.id.pager);
        // check which layout is shown
        if (null!= _listOfFragments && _listOfFragments.isInLayout())
        {
            _listOfFragments.getListView().setItemChecked(_currentItemShown,true);
            onArticleSelected(_currentItemShown);
        }
        if (_viewPager !=null)
        {
            _viewPager.setCurrentItem(_currentItemShown, true);
        }
        Pair<String, String> pair = Utils.getCurrentLanguages(this);
        Resources res = getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        Log.d(LOG_TAG, "possible languages = " + allLanguages);
        if (!allLanguages.contains(pair.first)) {
            startShowWelcomeActivity();
        }
	    if (Utils.isDictUpdateNeeded(this)) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle(R.string.dialog_update_to_help_dict_needed_title).setMessage(R.string.dialog_update_to_help_dict_needed).setPositiveButton(R.string.dialog_button_ok, dialogClickListener)
				    .setNegativeButton(R.string.dialog_button_cancel, dialogClickListener).setIcon(R.drawable.ic_action_alerts_and_states_warning).show();
	    }
    }

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					startDictToSQLActivity();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	};

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        if (_viewPager !=null)
            _viewPager.setCurrentItem(tab.getPosition());
	    Log.d(LOG_TAG, "onTabSelected current item set to " + _currentItemShown);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onArticleSelected(int position) {
        // Create a new listOfFragments
        MySmartFragment fragment;
        switch(position)
        {
	        case ADD_WORDS_FRAGMENT:
		        fragment = AddWordFragment.newInstance(_taskScheduler);
		        fragment.identifier = ADD_WORDS_FRAGMENT;
		        Log.d(LOG_TAG,"Created AddWordFragment with tag " + fragment.identifier);
		        break;
            case DICTIONARY_FRAGMENT:
                fragment = DictFragment.newInstance(_taskScheduler);
	            fragment.identifier = DICTIONARY_FRAGMENT;
	            Log.d(LOG_TAG,"Created Dictionary Fragment with tag " + fragment.identifier);
                break;
            case LEARN_WORDS_FRAGMENT:
                fragment = LearnCasualFragment.newInstance(_taskScheduler);
	            fragment.identifier = LEARN_WORDS_FRAGMENT;
	            Log.d(LOG_TAG,"Created LearnFragment with tag " + fragment.identifier);
	            break;
            default: fragment = null;
        }
        // Update the layout
        FragmentManager fm = getFragmentManager();

        if (fm.findFragmentByTag("android:switcher:" + 0 + ":" + position)==null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
	        ft.replace(R.id.view_group_id, fragment, "android:switcher:" + 0 + ":" + position);
            Log.d(LOG_TAG,"current fragment id = "+fragment.getId() + " and tag = " + fragment.getTag() + ((Object) fragment).getClass().getName());
            ft.commit();
        }
        _currentItemShown = position;
	    Log.d(LOG_TAG, "onArticleSelected current item set to " + _currentItemShown);
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
                dbHelper = FactoryDbHelper.createDbHelper(this, DBHelper.DB_WORDS);
                dbHelper.exportDB();
                dbHelper.close();
                return true;
            case R.id.menu_import:
                Log.d(LOG_TAG, "import DB");
                dbHelper = FactoryDbHelper.createDbHelper(this, DBHelper.DB_WORDS);
                dbHelper.importDB();
                dbHelper.close();
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

    private void startShowWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity welcome");
    }

	private void startDictToSQLActivity() {
		Intent intent = new Intent(this, LoadStarDictActivity.class);
		startActivity(intent);
	}
}
