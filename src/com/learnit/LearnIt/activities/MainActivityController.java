/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.astuetz.PagerSlidingTabStrip;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetMyWordsTask;
import com.learnit.LearnIt.async_tasks.GetTranslationsTask;
import com.learnit.LearnIt.async_tasks.GetWordsTask;
import com.learnit.LearnIt.async_tasks.SaveNewEntryTask;
import com.learnit.LearnIt.data_types.AppSectionsPagerAdapter;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.fragments.AddWordFragment;
import com.learnit.LearnIt.fragments.DictFragment;
import com.learnit.LearnIt.fragments.LearnFragment;
import com.learnit.LearnIt.fragments.ListOfFragments;
import com.learnit.LearnIt.fragments.MyDialogFragment;
import com.learnit.LearnIt.fragments.MySmartFragment;
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
    private ListFragment _listOfFragments;
	private WorkerFragment _worker;

	private String _wordForActionMode;

    private static int _currentItemShown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String currentLayout = getString(R.string.layout_current);
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    _worker = (WorkerFragment) fragmentManager.findFragmentByTag(WorkerFragment.TAG);
	    if (_worker == null)
	    {
		    _worker = new WorkerFragment();
		    fragmentManager.beginTransaction()
				    .add(_worker, WorkerFragment.TAG)
				    .commit();
	    }
        if (currentLayout.equals(LAYOUT_XLARGE))
        {
            MySmartFragment smart = (MySmartFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_dict_id);
	        smart.identifier = DICTIONARY_FRAGMENT;
	        smart = (MySmartFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_add_words_id);
	        smart.identifier = ADD_WORDS_FRAGMENT;
	        smart = (MySmartFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_learn_id);
	        smart.identifier = LEARN_WORDS_FRAGMENT;
        }
        else if (currentLayout.equals(LAYOUT_LARGE_LAND))
        {
            _appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), this);
            //Do some special processing for large screen
            ListOfFragments fragment = (ListOfFragments) getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
            fragment.getListView().setSelection(0);
        }
        else if (currentLayout.equals(LAYOUT_NORMAL))
        {
            // Initialize the view pager
            // Create the adapter that will return a listOfFragments for each of the three primary sections
            // of the app.
            _appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), this);

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
            startShowWelcomeActivity();
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
            case DICTIONARY_FRAGMENT:
                fragment = new DictFragment();
	            fragment.identifier = DICTIONARY_FRAGMENT;
	            Log.d(LOG_TAG,"Created Dictionary Fragment with tag " + fragment.identifier);
                break;
            case ADD_WORDS_FRAGMENT:
                fragment = new AddWordFragment();
	            fragment.identifier = ADD_WORDS_FRAGMENT;
	            Log.d(LOG_TAG,"Created AddWordFragment with tag " + fragment.identifier);
	            break;
            case LEARN_WORDS_FRAGMENT:
                fragment = new LearnFragment();
	            fragment.identifier = LEARN_WORDS_FRAGMENT;
	            Log.d(LOG_TAG,"Created LearnFragment with tag " + fragment.identifier);
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
            Log.d(LOG_TAG,"current fragment id = "+fragment.getId() + " and tag = " + fragment.getTag() + ((Object) fragment).getClass().getName());
            ft.commit();
        }
        _currentItemShown = position;
	    Log.d(LOG_TAG, "onArticleSelected current item set to " + _currentItemShown);
    }

	public void showMessage(int exitCode) {
		DialogFragment frag = new MyDialogFragment();
		frag.show(getFragmentManager(), String.valueOf(exitCode));
	}

	private void showDialog(String queryWord, String translation, int dialogType) {
		DialogFragment frag = new MyDialogFragment(queryWord, translation, dialogType);
		Bundle args = new Bundle();
		args.putInt(MyDialogFragment.ID_TAG, dialogType);
		args.putString(MyDialogFragment.WORD_TAG, queryWord);
		args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
		frag.setArguments(args);
		frag.show(getFragmentManager(), String.valueOf(MyDialogFragment.DIALOG_SHOW_WORD));
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

    private void startShowWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity welcome");
    }

	void startEditWordActivity(String word) {
		Intent intent = new Intent(this, EditWord.class);
		intent.putExtra("word", word);
		startActivity(intent);
		Log.d(LOG_TAG, "start info activity called");
	}

	private MySmartFragment getCurrentShownFragment(int id)
	{
		String currentLayout = getString(R.string.layout_current);
		FragmentManager fm = getSupportFragmentManager();
		if (currentLayout.equals(LAYOUT_NORMAL)) {
			return (MySmartFragment) fm.findFragmentByTag("android:switcher:" + _viewPager.getId() + ":" + _currentItemShown);
		} else if (currentLayout.equals(LAYOUT_LARGE_LAND)) {
			return (MySmartFragment) fm.findFragmentByTag("android:switcher:" + "0" + ":" + _currentItemShown);
		} else if (currentLayout.equals(LAYOUT_XLARGE)) {
			// we do not have tags if we are in the xlarge mode
			return (MySmartFragment) fm.findFragmentById(id);
		}
		return null;
	}

	// Implementing OnUiAction interface
	@Override
	public void onUiClick(int fragmentId, int viewId) {
		MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
		if (currentFragment.identifier == ADD_WORDS_FRAGMENT)
		{
			AddWordFragment frag = (AddWordFragment) currentFragment;
			switch (viewId)
			{
				case R.id.btn_add_word_clear:
					frag.setViewText(R.id.edv_add_word, "");
					frag.setViewFocused(R.id.edv_add_word);
					frag.setListEntries(null, R.id.list_of_add_words);
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
		if (currentFragment.identifier == DICTIONARY_FRAGMENT)
		{
			DictFragment frag = (DictFragment) currentFragment;
			frag.setViewText(R.id.edv_search_word, "");
		}
	}

	@Override
	public void onViewGotFocus(int fragmentId, int viewId) {
		try {
			MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
			if (currentFragment.identifier ==ADD_WORDS_FRAGMENT)
			{
				AddWordFragment frag = (AddWordFragment) currentFragment;
				switch (viewId)
				{
					case R.id.edv_add_word:
						if (!frag.getTextFromView(R.id.edv_add_word).isEmpty())
						{
							_worker.addNewTask(this,
									new GetWordsTask(frag.getTextFromView(R.id.edv_add_word)),
									frag.getId());
						}
						break;
					case R.id.edv_add_translation:
						if (!frag.getTextFromView(R.id.edv_add_word).isEmpty())
						{
							_worker.addNewTask(this,
									new GetTranslationsTask(frag.getTextFromView(R.id.edv_add_word)),
									frag.getId());
						}
						break;
				}
			}
			if (currentFragment.identifier == DICTIONARY_FRAGMENT)
			{
				DictFragment frag = (DictFragment) currentFragment;
				switch (viewId)
				{
					case R.id.edv_search_word:
						_worker.addNewTask(this,
								new GetMyWordsTask(frag.getTextFromView(R.id.edv_add_word)),
								frag.getId());
						break;
				}
			}
		}
		catch (ClassCastException ex)
		{
			Log.e(LOG_TAG, ex.getMessage());
		}
	}

	private <T extends FragmentUiInterface> void updateViewVisibility(
			T frag,
			boolean isCurrentWordEmpty,
			int id)
	{
		if (isCurrentWordEmpty)
		{
			frag.setListEntries(null, R.id.list_of_add_words);
			frag.setListEntries(null, R.id.list_of_words);
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
	public void onTextChange(int fragmentId, int viewId, boolean isEmpty) {
		MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
		if (currentFragment == null) return;
		Log.d(LOG_TAG, "currentFragment.tag = " + currentFragment.getTag());
		Log.d(LOG_TAG, ((Object) currentFragment).getClass().getName());
		if (currentFragment.identifier == ADD_WORDS_FRAGMENT)
		{
			AddWordFragment frag = (AddWordFragment) currentFragment;
			if (!isIdValid(frag.getFocusedId(), viewId))
				return;
			if (!frag.getTextFromView(R.id.edv_add_translation).isEmpty() &&
					!frag.getTextFromView(R.id.edv_add_translation).isEmpty())
				frag.setMenuItemVisible(true);
			else
				frag.setMenuItemVisible(false);
			switch (viewId)
			{
				case R.id.edv_add_word:
					updateViewVisibility(frag, isEmpty, R.id.btn_add_word_clear);
					if (!isEmpty && frag.getTextFromView(R.id.edv_add_translation).isEmpty())
						_worker.addNewTask(this,
								new GetWordsTask(frag.getTextFromView(viewId)),
								frag.getId());
					break;
				case R.id.edv_add_translation:
					updateViewVisibility(frag, isEmpty, R.id.btn_add_trans_clear);
					break;
			}
		}
		if (currentFragment.identifier == DICTIONARY_FRAGMENT)
		{
			DictFragment frag = (DictFragment) currentFragment;
			if (viewId == R.id.edv_search_word)
			{
				updateViewVisibility(frag, isEmpty, R.id.btn_search_clear);
				_worker.addNewTask(this,
						new GetMyWordsTask(frag.getTextFromView(viewId)),
						frag.getId());
			}
		}
	}

	@Override
	public <T> void onListItemClick(int fragmentId, int viewId, T text) {
		MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
		if (currentFragment.identifier ==ADD_WORDS_FRAGMENT)
		{
			AddWordFragment frag = (AddWordFragment) currentFragment;
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
		if (currentFragment.identifier == DICTIONARY_FRAGMENT)
		{
			Pair<String,String> pair = (Pair)text;
			showDialog(pair.first, pair.second, MyDialogFragment.DIALOG_SHOW_WORD);
		}
	}

	@Override
	public void onListItemLongClick(int fragmentId, int viewId, String text) {
		ActionMode.Callback mActionModeCallback = this;
		startActionMode(mActionModeCallback);
		_wordForActionMode = text;
	}

	@Override
	public void onMenuItemClick(int fragmentId, int viewId) {
		MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
		if (currentFragment.identifier ==ADD_WORDS_FRAGMENT)
		{
			AddWordFragment frag = (AddWordFragment) currentFragment;
			switch (viewId)
			{
				case R.id.save_item:
					_worker.addNewTask(this, new SaveNewEntryTask(
							frag.getTextFromView(R.id.edv_add_word),
							frag.getTextFromView(R.id.edv_add_translation)),
							frag.getId());
					frag.setViewText(R.id.edv_add_translation, "");
					frag.setViewText(R.id.edv_add_word, "");
					frag.setViewFocused(R.id.edv_add_word);
					break;
			}
		}
	}

	@Override
	public void onPreExecute() {

	}

	// Implementing OnTaskListener interface
	@Override
	public void onFail(int fragmentId) {
		_worker.onTaskFinished();
		MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
		if (currentFragment.identifier == ADD_WORDS_FRAGMENT)
		{
			((AddWordFragment) currentFragment).setListEntries(null, R.id.list_of_add_words);
		}
	}

	@Override
	public <T> void onSuccess(int fragmentId, T result) {
		try
		{
			_worker.onTaskFinished();
			MySmartFragment currentFragment = getCurrentShownFragment(fragmentId);
			Log.d(LOG_TAG, "currentFragment.identifier = " + currentFragment.identifier);
			if (currentFragment.identifier == ADD_WORDS_FRAGMENT)
			{
				AddWordFragment frag = (AddWordFragment) currentFragment;
				if (result instanceof List)
				{
					if (((List) result).isEmpty()) {
						frag.setListEntries(null, R.id.list_of_add_words);
						return;
					}
					if (!(((List) result).get(0) instanceof String)) return;
					List<String> list = (List<String>)result;
					frag.setListEntries(list, R.id.list_of_add_words);
				}
				if (result instanceof Pair)
				{

					Pair<String, List<String>> pair = (Pair)result;
					if (StringUtils.isArticle(this, pair.first))
					{
						frag.addArticle(pair.first);
					}
					if (pair.second!=null)
						frag.setListEntries(pair.second, R.id.list_of_add_words);
				}
				if (result instanceof Integer)
				{
					Integer exitCode = (Integer)result;
					showMessage(exitCode);
					frag.setViewText(R.id.edv_add_translation,"");
					frag.setViewText(R.id.edv_add_word,"");
				}
			}
			if (currentFragment.identifier == DICTIONARY_FRAGMENT)
			{
				DictFragment frag = (DictFragment) currentFragment;
				if (result instanceof List)
				{
					if (((List) result).isEmpty())
					{
						frag.setListEntries(null, R.id.list_of_words);
						return;
					}
					if (!(((List) result).get(0) instanceof Map)) return;
					List<Map<String,String>> list = (List<Map<String,String>>) result;
					frag.setListEntries(list, R.id.list_of_words);
				}
			}
		}
		catch (NullPointerException ex)
		{
			Log.e(LOG_TAG, ex.getMessage() + " in onSuccess, while trying to process " + result.toString());
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

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		MySmartFragment currentFragment = getCurrentShownFragment(R.id.fragment_dict_id);
		if (currentFragment.identifier ==DICTIONARY_FRAGMENT)
		{
			DictFragment frag = (DictFragment) currentFragment;
			if (!frag.getTextFromView(R.id.edv_search_word).isEmpty())
				frag.setViewText(R.id.edv_search_word, "");
			_wordForActionMode = null;
		}
	}
}
