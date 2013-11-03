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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.AppSectionsPagerAdapter;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.data_types.GetWordsTask;
import com.learnit.LearnIt.fragments.AddWordFragmentNew;
import com.learnit.LearnIt.fragments.DictFragment;
import com.learnit.LearnIt.fragments.LearnFragment;
import com.learnit.LearnIt.fragments.ListOfFragments;
import com.learnit.LearnIt.fragments.MyDialogFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class MainActivityController extends FragmentActivity implements
		ActionBar.TabListener,
		ListOfFragments.OnFragmentSelectedListener,
		WorkerFragment.OnTaskActionListener,
		AddWordFragmentNew.OnUiAction{

    final String LOG_TAG = "my_logs";
    public static int NUMBER_OF_FRAGMENTS = 3;
    public static final int ADD_WORDS_FRAGMENT = 0;
    public static final int DICTIONARY_FRAGMENT = 1;
    public static final int LEARN_WORDS_FRAGMENT = 2;
    public static final String LAYOUT_NORMAL = "normal";
    public static final String LAYOUT_XLARGE = "xlarge";
    public static final String LAYOUT_LARGE_LAND = "large_landscape";


    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    ListOfFragments listOfFragments;
	WorkerFragment _worker;

    static int currentItemShown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String currentLayout = getString(R.string.layout_current);
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    _worker = (WorkerFragment) fragmentManager
			    .findFragmentByTag(WorkerFragment.TAG);
	    if (_worker == null)
	    {
		    _worker = new WorkerFragment();
		    fragmentManager.beginTransaction()
				    .add(_worker, WorkerFragment.TAG)
				    .commit();
	    }
        if (currentLayout.equals(LAYOUT_XLARGE))
        {
            //Do some special processing for xlarge screen
        }
        else if (currentLayout.equals(LAYOUT_LARGE_LAND))
        {
            mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
            //Do some special processing for large screen
            ListOfFragments fragment = (ListOfFragments) getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
            fragment.getListView().setSelection(0);
        }
        else if (currentLayout.equals(LAYOUT_NORMAL))
        {
            // Initialize the view pager
            // Create the adapter that will return a listOfFragments for each of the three primary sections
            // of the app.
            mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

            // Set up the action bar.
            final ActionBar actionBar = getActionBar();

            // Set up the ViewPager, attaching the adapter and setting up a listener for when the
            // user swipes between sections.
            mViewPager = (ViewPager) findViewById(R.id.pager);

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

            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by the adapter.
                // Also specify this Activity object, which implements the TabListener interface, as the
                // listener for when this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mAppSectionsPagerAdapter.getPageTitle(this, i))
                                .setTabListener(this));
            }
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mViewPager!=null)
            currentItemShown =  mViewPager.getCurrentItem();
        if (listOfFragments !=null && listOfFragments.isInLayout())
            currentItemShown = listOfFragments.getListView().getCheckedItemPosition();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onResume() {
        super.onResume();
        listOfFragments = (ListOfFragments) getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        // check which layout is shown
        if (null!= listOfFragments && listOfFragments.isInLayout())
        {
            listOfFragments.getListView().setItemChecked(currentItemShown,true);
            onArticleSelected(currentItemShown);
        }
        if (mViewPager!=null)
        {
            mViewPager.setCurrentItem(currentItemShown, true);
        }

        Pair<String, String> pair = Utils.getCurrentLanguages(this);
        Resources res = getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        Log.d(LOG_TAG, "possible languages = " + allLanguages);
        if (!allLanguages.contains(pair.first)) {
            startShowWellcomeActivity();
        }
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

    public void onArticleSelected(int position) {
        // Create a new listOfFragments
        Fragment fragment;
        switch(position)
        {
            case DICTIONARY_FRAGMENT:
                fragment = new DictFragment();
                break;
            case ADD_WORDS_FRAGMENT:
                fragment = new AddWordFragmentNew();
                break;
            case LEARN_WORDS_FRAGMENT:
                fragment = new LearnFragment();
                break;
            default: fragment = new DictFragment();
        }
        // Update the layout
        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(fragment.getClass().getName())==null)
        {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.float_in_right, R.anim.float_away_left);
	        ft.replace(R.id.view_group_id, fragment, "android:switcher:" + 0 + ":" + position);
            Log.d(LOG_TAG,"current fragment id = "+fragment.getId() + " and tag = " + fragment.getTag() + fragment.getClass().getName());
            ft.commit();
        }
        currentItemShown=position;
    }


	public void showMessage(int exitCode) {
		MyDialogFragment frag = new MyDialogFragment();
		frag.showMessage(exitCode, getSupportFragmentManager());
	}

	@Override
	public void onPreExecute() {

	}

	@Override
	public void onFail() {
		Log.d(LOG_TAG, "on fail!!!!");
		_worker.onTaskFinished();
	}

	@Override
	public void onSuccess(List<String> name) {
		Log.d(LOG_TAG, "on success!!!!" + name.toString());
		_worker.onTaskFinished();
	}

	@Override
	public void onProgressUpdate(Integer... values) {

	}

	@Override
	public void noTaskSpecified() {

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

	private Fragment getCurrentShownFragment()
	{
		String currentLayout = getString(R.string.layout_current);
		FragmentManager fm = getSupportFragmentManager();
		if (currentLayout.equals(LAYOUT_NORMAL))
		{
			return fm.findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + currentItemShown);
		}
		else if (currentLayout.equals(LAYOUT_LARGE_LAND))
		{
			return fm.findFragmentByTag("android:switcher:" + 0 + ":" + currentItemShown);
		}
		return null;
	}


	@Override
	public void onUiClick(int id) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.btn_add_word_clear:
					frag.clearWord();
					frag.setWordFocused();
					break;
				case R.id.btn_add_trans_clear:
					frag.clearTranslation();
					frag.setTranslationFocused();
					break;
			}
		}
	}

	@Override
	public void onUiGotFocus(int id) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			Log.d(LOG_TAG, "onUiGotFocus got id = " + id);
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.edv_add_word:
					break;
				case R.id.edv_add_translation:
					break;
			}
		}
	}

	@Override
	public void onTextChange(int id, boolean isEmpty) {
		Log.d(LOG_TAG, "onTextChange got id = " + id + " is empty = " + isEmpty);
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.edv_add_word:
					if (!frag.isWordEmpty() && frag.isTransEmpty())
					{
						_worker.addNewTask(this, new GetWordsTask(frag.getWord()));
					}
					break;
				case R.id.edv_add_translation:
					break;
			}
		}
	}

	@Override
	public void onListItemClick(int id, String text) {

	}

	@Override
	public void onMenuItemClick(int id) {

	}
}
