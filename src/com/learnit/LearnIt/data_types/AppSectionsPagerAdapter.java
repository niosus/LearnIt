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

package com.learnit.LearnIt.data_types;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.activities.MainActivity;
import com.learnit.LearnIt.fragments.AddWordFragment;
import com.learnit.LearnIt.fragments.DictFragment;
import com.learnit.LearnIt.fragments.LearnCasualFragment;
import com.learnit.LearnIt.fragments.MySmartFragment;
import com.learnit.LearnIt.fragments.TaskSchedulerFragment;
import com.learnit.LearnIt.utils.Constants;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a listOfFragments corresponding to one of the primary
 * sections of the app.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
	private Context _context;
	private TaskSchedulerFragment _worker;

	public AppSectionsPagerAdapter(FragmentManager fm, Context context, TaskSchedulerFragment worker) {
		super(fm);
		_context = context;
		_worker = worker;
	}

	@Override
	public Fragment getItem(int i) {
		MySmartFragment fragment = null;
		switch (i) {
			case MainActivity.DICTIONARY_FRAGMENT:
				fragment = new DictFragment();
				fragment.identifier = MainActivity.DICTIONARY_FRAGMENT;
				Log.d(Constants.LOG_TAG, "Created Dictionary Fragment with tag " + fragment.identifier);
				break;
			case MainActivity.ADD_WORDS_FRAGMENT:
				fragment = new AddWordFragment();
				fragment.identifier = MainActivity.ADD_WORDS_FRAGMENT;
				Log.d(Constants.LOG_TAG,"Created AddWordFragment with tag " + fragment.identifier);
				break;
			case MainActivity.LEARN_WORDS_FRAGMENT:
				fragment = new LearnCasualFragment();
				fragment.identifier = MainActivity.LEARN_WORDS_FRAGMENT;
				Log.d(Constants.LOG_TAG,"Created LearnFragment with tag " + fragment.identifier);
				break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return MainActivity.NUMBER_OF_FRAGMENTS;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case MainActivity.DICTIONARY_FRAGMENT:
				return _context.getString(R.string.dictionary_frag_title);
			case MainActivity.ADD_WORDS_FRAGMENT:
				return _context.getString(R.string.add_words_frag_title);
			case MainActivity.LEARN_WORDS_FRAGMENT:
				return _context.getString(R.string.learn_words_frag_title);
		}
		return null;
	}
}