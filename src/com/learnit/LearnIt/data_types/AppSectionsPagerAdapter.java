package com.learnit.LearnIt.data_types;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.activities.MainActivityController;
import com.learnit.LearnIt.fragments.AddWordFragmentNew;
import com.learnit.LearnIt.fragments.DictFragmentNew;
import com.learnit.LearnIt.fragments.LearnFragment;
import com.learnit.LearnIt.fragments.MySmartFragment;
import com.learnit.LearnIt.utils.Constants;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a listOfFragments corresponding to one of the primary
 * sections of the app.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

	public AppSectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		MySmartFragment fragment = null;
		switch (i) {
			case MainActivityController.DICTIONARY_FRAGMENT:
				fragment = new DictFragmentNew();
				fragment.identifier = MainActivityController.DICTIONARY_FRAGMENT;
				Log.d(Constants.LOG_TAG, "Created Dictionary Fragment with tag " + fragment.identifier);
				break;
			case MainActivityController.ADD_WORDS_FRAGMENT:
				fragment = new AddWordFragmentNew();
				fragment.identifier = MainActivityController.ADD_WORDS_FRAGMENT;
				Log.d(Constants.LOG_TAG,"Created AddWordFragmentNew with tag " + fragment.identifier);
				break;
			case MainActivityController.LEARN_WORDS_FRAGMENT:
				fragment = new LearnFragment();
				fragment.identifier = MainActivityController.LEARN_WORDS_FRAGMENT;
				Log.d(Constants.LOG_TAG,"Created LearnFragment with tag " + fragment.identifier);
				break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return MainActivityController.NUMBER_OF_FRAGMENTS;
	}

	public CharSequence getPageTitle(Context context,int position) {
		Resources resources = context.getResources();
		switch (position) {
			case MainActivityController.DICTIONARY_FRAGMENT:
				return resources.getString(R.string.dictionary_frag_title);
			case MainActivityController.ADD_WORDS_FRAGMENT:
				return resources.getString(R.string.add_words_frag_title);
			case MainActivityController.LEARN_WORDS_FRAGMENT:
				return resources.getString(R.string.learn_words_frag_title);
		}
		return null;
	}
}