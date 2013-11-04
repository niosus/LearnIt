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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetMyWordsTask;
import com.learnit.LearnIt.async_tasks.GetTranslationsTask;
import com.learnit.LearnIt.async_tasks.GetWordsTask;
import com.learnit.LearnIt.async_tasks.SaveNewEntryTask;
import com.learnit.LearnIt.data_types.AppSectionsPagerAdapter;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.fragments.AddWordFragmentNew;
import com.learnit.LearnIt.fragments.DictFragmentNew;
import com.learnit.LearnIt.fragments.LearnFragment;
import com.learnit.LearnIt.fragments.ListOfFragments;
import com.learnit.LearnIt.fragments.MyDialogFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.interfaces.FragmentUiInterface;
import com.learnit.LearnIt.interfaces.OnUiAction;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivityController extends FragmentActivity implements
		ActionBar.TabListener,
		ListOfFragments.OnFragmentSelectedListener,
		WorkerFragment.OnTaskActionListener,
		OnUiAction,
		ActionMode.Callback{

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
    private ListOfFragments _listOfFragments;
	private WorkerFragment _worker;

	private String _wordForActionMode;

    private static int _currentItemShown = 0;

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
            _appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
            //Do some special processing for large screen
            ListOfFragments fragment = (ListOfFragments) getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
            fragment.getListView().setSelection(0);
        }
        else if (currentLayout.equals(LAYOUT_NORMAL))
        {
            // Initialize the view pager
            // Create the adapter that will return a listOfFragments for each of the three primary sections
            // of the app.
            _appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

            // Set up the action bar.
            final ActionBar actionBar = getActionBar();

            // Set up the ViewPager, attaching the adapter and setting up a listener for when the
            // user swipes between sections.
            _viewPager = (ViewPager) findViewById(R.id.pager);

            // Specify that the Home/Up button should not be enabled, since there is no hierarchical
            // parent.
            actionBar.setHomeButtonEnabled(false);

            // Specify that we will be displaying tabs in the action bar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            _viewPager.setAdapter(_appSectionsPagerAdapter);
            _viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
		            // When swiping between different app sections, select the corresponding tab.
		            // We can also use ActionBar.Tab#select() to do this if we have a reference to the
		            // Tab.
		            _currentItemShown = position;
		            actionBar.setSelectedNavigationItem(position);
	            }
            });

            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < _appSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by the adapter.
                // Also specify this Activity object, which implements the TabListener interface, as the
                // listener for when this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(_appSectionsPagerAdapter.getPageTitle(this, i))
                                .setTabListener(this));
            }
	        actionBar.selectTab(actionBar.getTabAt(_currentItemShown));
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        _listOfFragments = (ListOfFragments) getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
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
            startShowWellcomeActivity();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        if (_viewPager !=null)
            _viewPager.setCurrentItem(tab.getPosition());
//	    _currentItemShown = tab.getPosition();
	    Log.d(LOG_TAG, "onTabSelected current item set to " + _currentItemShown);
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
                fragment = new DictFragmentNew();
                break;
            case ADD_WORDS_FRAGMENT:
                fragment = new AddWordFragmentNew();
                break;
            case LEARN_WORDS_FRAGMENT:
                fragment = new LearnFragment();
                break;
            default: fragment = null;
        }
        // Update the layout
        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag("android:switcher:" + 0 + ":" + position)==null)
        {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.float_in_right, R.anim.float_away_left);
	        ft.replace(R.id.view_group_id, fragment, "android:switcher:" + 0 + ":" + position);
            Log.d(LOG_TAG,"current fragment id = "+fragment.getId() + " and tag = " + fragment.getTag() + fragment.getClass().getName());
            ft.commit();
        }
        _currentItemShown = position;
	    Log.d(LOG_TAG, "onArticleSelected current item set to " + _currentItemShown);
    }


	public void showMessage(int exitCode) {
		MyDialogFragment frag = new MyDialogFragment();
		frag.showMessage(exitCode, getSupportFragmentManager());
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

    private void startShowWellcomeActivity() {
        Intent intent = new Intent(this, WellcomeActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity welcome");
    }

	void startEditWordActivity(String word) {
		Intent intent = new Intent(this, EditWord.class);
		intent.putExtra("word", word);
		startActivity(intent);
		Log.d(LOG_TAG, "start info activity called");
	}

	private void showDialog(String queryWord, String translation, int dialogType) {
		MyDialogFragment frag = new MyDialogFragment();
		Bundle args = new Bundle();
		args.putInt(MyDialogFragment.ID_TAG, dialogType);
		args.putString(MyDialogFragment.WORD_TAG, queryWord);
		args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
		frag.setArguments(args);
		frag.show(getSupportFragmentManager(), "show_word_fragment_dialog");
	}

	private Fragment getCurrentShownFragment()
	{
		String currentLayout = getString(R.string.layout_current);
		Log.d(LOG_TAG, currentLayout + _currentItemShown);
		FragmentManager fm = getSupportFragmentManager();
		if (currentLayout.equals(LAYOUT_NORMAL))
		{
			return fm.findFragmentByTag("android:switcher:" + _viewPager.getId() + ":" + _currentItemShown);
		}
		else if (currentLayout.equals(LAYOUT_LARGE_LAND))
		{
			return fm.findFragmentByTag("android:switcher:" + 0 + ":" + _currentItemShown);
		}
		return null;
	}

	// Implementing OnUiAction interface
	@Override
	public void onUiClick(int id) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.btn_add_word_clear:
					frag.setViewText(R.id.edv_add_word, "");
					frag.setViewFocused(R.id.edv_add_word);
					frag.setListEntries(null);
					updateViewVisibility(
							frag,
							frag.getTextFromView(R.id.edv_add_word).isEmpty(),
							R.id.btn_add_word_clear);
					break;
				case R.id.btn_add_trans_clear:
					frag.setViewText(R.id.edv_add_translation, "");
					frag.setViewFocused(R.id.edv_add_translation);
					updateViewVisibility(
							frag,
							frag.getTextFromView(R.id.edv_add_translation).isEmpty(),
							R.id.btn_add_trans_clear);
					break;
			}
		}
		if (currentFragment instanceof DictFragmentNew)
		{
			DictFragmentNew frag = (DictFragmentNew) currentFragment;
			frag.setViewText(R.id.edv_search_word, "");
		}
	}

	@Override
	public void onViewGotFocus(int id) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			Log.d(LOG_TAG, "onViewGotFocus got id = " + id);
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.edv_add_word:
					if (!frag.getTextFromView(R.id.edv_add_word).isEmpty())
					{
						_worker.addNewTask(this, new GetWordsTask(frag.getTextFromView(R.id.edv_add_word)));
					}
					break;
				case R.id.edv_add_translation:
					if (!frag.getTextFromView(R.id.edv_add_word).isEmpty())
					{
						_worker.addNewTask(this, new GetTranslationsTask(frag.getTextFromView(R.id.edv_add_word)));
					}
					break;
			}
		}
		if (currentFragment instanceof DictFragmentNew)
		{
			Log.d(LOG_TAG, "onViewGotFocus got id = " + id);
			DictFragmentNew frag = (DictFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.edv_search_word:
					_worker.addNewTask(this, new GetMyWordsTask(frag.getTextFromView(R.id.edv_add_word)));
					break;
			}
		}
	}

	private <T extends FragmentUiInterface> void updateViewVisibility(
			T frag,
			boolean isCurrentWordEmpty,
			int id)
	{
		if (isCurrentWordEmpty)
		{
			frag.setListEntries(null);
			frag.setViewVisibility(id, View.INVISIBLE);
		}
		else
		{
			frag.setViewVisibility(id, View.VISIBLE);
		}
	}

	private boolean isIdValid(Integer focusedId, int thisId)
	{
		if (focusedId==null) return false;
		if (focusedId != thisId) return false;
		return true;
	}

	@Override
	public void onTextChange(int id, boolean isEmpty) {
		Log.d(LOG_TAG, "onTextChange got id = " + id + " is empty = " + isEmpty);
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment == null) return;
		Log.d(LOG_TAG, currentFragment.getClass().getName());
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			if (!isIdValid(frag.getFocusedId(), id))
				return;
			if (!frag.getTextFromView(R.id.edv_add_translation).isEmpty() &&
					!frag.getTextFromView(R.id.edv_add_translation).isEmpty())
				frag.setMenuItemVisible(true);
			else
				frag.setMenuItemVisible(false);
			switch (id)
			{
				case R.id.edv_add_word:
					updateViewVisibility(frag, isEmpty, R.id.btn_add_word_clear);
					if (!isEmpty && frag.getTextFromView(R.id.edv_add_translation).isEmpty())
						_worker.addNewTask(this, new GetWordsTask(frag.getTextFromView(id)));
					break;
				case R.id.edv_add_translation:
					updateViewVisibility(frag, isEmpty, R.id.btn_add_trans_clear);
					break;
			}
		}
		if (currentFragment instanceof DictFragmentNew)
		{
			DictFragmentNew frag = (DictFragmentNew) currentFragment;
			Log.d(LOG_TAG, "word changed?");
			if (id == R.id.edv_search_word)
			{
				Log.d(LOG_TAG, "word changed");
				updateViewVisibility(frag, isEmpty, R.id.btn_search_clear);
				_worker.addNewTask(this, new GetMyWordsTask(frag.getTextFromView(id)));
			}
		}
	}

	@Override
	public <T> void onListItemClick(int id, T text) {
		Log.d(LOG_TAG, "list item clicked " + text);
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			Integer focusedId = frag.getFocusedId();
			switch (focusedId)
			{
				case R.id.edv_add_word:
					frag.setViewText(focusedId, (String)text);
					frag.setViewFocused(R.id.edv_add_translation);
					break;
				case R.id.edv_add_translation:
					frag.addTextToView(R.id.edv_add_translation, (String)text);
					break;
			}
		}
		if (currentFragment instanceof DictFragmentNew)
		{
			Pair<String,String> pair = (Pair)text;
			showDialog(pair.first, pair.second, MyDialogFragment.DIALOG_SHOW_WORD);
		}
	}

	@Override
	public void onListItemLongClick(int id, String text) {
		ActionMode.Callback mActionModeCallback = this;
		startActionMode(mActionModeCallback);
		_wordForActionMode = text;
	}

	@Override
	public void onMenuItemClick(int id) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			switch (id)
			{
				case R.id.save_item:
					_worker.addNewTask(this, new SaveNewEntryTask(
							frag.getTextFromView(R.id.edv_add_word),
							frag.getTextFromView(R.id.edv_add_translation)));
					break;
			}
		}
	}

	@Override
	public void onPreExecute() {

	}

	// Implementing OnTaskListener interface
	@Override
	public void onFail() {
		Log.d(LOG_TAG, "on fail!!!!");
		_worker.onTaskFinished();
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			((AddWordFragmentNew) currentFragment).setListEntries(null);
		}
	}

	@Override
	public <T> void onSuccess(T result) {
		Log.d(LOG_TAG, "on success!!!!" + result.toString());
		_worker.onTaskFinished();
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof AddWordFragmentNew)
		{
			AddWordFragmentNew frag = (AddWordFragmentNew) currentFragment;
			if (result instanceof List)
			{
				List<String> list = (List<String>)result;
				if (list==null || list.isEmpty())
				{
					frag.setListEntries(null);
					return;
				}
				frag.setListEntries(list);
			}
			if (result instanceof Pair)
			{

				Pair<String, List<String>> pair = (Pair)result;
				Log.d(LOG_TAG, "pair is " + pair);
				if (StringUtils.isArticle(this, pair.first))
				{
					Log.d(LOG_TAG, "article? " + pair.first);
					frag.addArticle(pair.first);
				}
				if (pair.second!=null)
					frag.setListEntries(pair.second);
			}
			if (result instanceof Integer)
			{
				Integer exitCode = (Integer)result;
				showMessage(exitCode);
				frag.setViewText(R.id.edv_add_translation,"");
				frag.setViewText(R.id.edv_add_word,"");
			}
		}
		if (currentFragment instanceof DictFragmentNew)
		{
			DictFragmentNew frag = (DictFragmentNew) currentFragment;
			if (result instanceof List)
			{
				List<Map<String,String>> list = (List<Map<String,String>>) result;
				frag.setListEntries(list);
			}
		}
	}

	@Override
	public void onProgressUpdate(Integer... values) {

	}

	@Override
	public void noTaskSpecified() {

	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		MenuInflater inflater = actionMode.getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof DictFragmentNew)
		{
			String queryWord = _wordForActionMode;
			switch (menuItem.getItemId()) {
				case R.id.context_menu_edit:
					startEditWordActivity(queryWord);
					actionMode.finish(); // Action picked, so close the CAB
					return true;
				case R.id.context_menu_delete:
					DBHelper dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
					dbHelper.deleteWord(StringUtils.stripFromArticle(this, queryWord));
					showDialog(queryWord, null, MyDialogFragment.DIALOG_WORD_DELETED);
					actionMode.finish();
					return true;
				default:
					return false;
			}
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		Fragment currentFragment = getCurrentShownFragment();
		if (currentFragment instanceof DictFragmentNew)
		{
			DictFragmentNew frag = (DictFragmentNew) currentFragment;
			if (!frag.getTextFromView(R.id.edv_search_word).isEmpty())
				frag.setViewText(R.id.edv_search_word, "");
			_wordForActionMode = null;
		}
	}
}
