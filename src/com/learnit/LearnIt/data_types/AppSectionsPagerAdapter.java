package com.learnit.LearnIt.data_types;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.activities.MainActivityController;
import com.learnit.LearnIt.fragments.AddWordFragmentNew;
import com.learnit.LearnIt.fragments.DictFragment;
import com.learnit.LearnIt.fragments.LearnFragment;

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
		Fragment fragment = null;
		switch (i) {
			case MainActivityController.DICTIONARY_FRAGMENT:
				return new DictFragment();
			case MainActivityController.ADD_WORDS_FRAGMENT:
				return new AddWordFragmentNew();
			case MainActivityController.LEARN_WORDS_FRAGMENT:
				return new LearnFragment();
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