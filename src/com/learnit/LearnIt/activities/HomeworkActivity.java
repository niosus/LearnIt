
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
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.learnit.LearnIt.data_types.NotificationBuilder;
import com.learnit.LearnIt.fragments.LearnHomeworkArticlesFragment;
import com.learnit.LearnIt.fragments.LearnHomeworkTranslationFragment;
import com.learnit.LearnIt.fragments.TaskSchedulerFragment;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;

import java.util.ArrayList;

public class HomeworkActivity extends Activity {
	Fragment _uiTranslationsFragment;
	Fragment _uiArticlesFragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) { actionBar.setTitle(""); }
        FragmentManager fragmentManager = getFragmentManager();

		// add a headless worker fragment to stack if not yet there
		Fragment worker = fragmentManager
				.findFragmentByTag(TaskSchedulerFragment.TAG);
		if (worker == null)
		{
			worker = new TaskSchedulerFragment();
			fragmentManager.beginTransaction()
					.add(worker, TaskSchedulerFragment.TAG)
					.commit();
		}

        _uiTranslationsFragment = LearnHomeworkTranslationFragment
                .newInstance((IWorkerJobInput) worker);
        // extras contain words, translations and so on that we need to show
        // the data in the homework fragment. We pass them on to the fragment.
        _uiTranslationsFragment.setArguments(getIntent().getExtras());

        _uiArticlesFragment = LearnHomeworkArticlesFragment
                .newInstance((IWorkerJobInput) worker);
        // extras contain words, translations and so on that we need to show
        // the data in the homework fragment. We pass them on to the fragment.
        _uiArticlesFragment.setArguments(getIntent().getExtras());

		ArrayList<Integer> types = getIntent().getIntegerArrayListExtra(NotificationBuilder.HOMEWORK_TYPE_TAG);
		if (types == null || types.isEmpty()) return;
		switch (types.get(0)) {
			case Constants.LEARN_TRANSLATIONS:
				fragmentManager.beginTransaction()
						.replace(android.R.id.content, _uiTranslationsFragment, LearnHomeworkTranslationFragment.TAG)
						.commit();
				break;
			case Constants.LEARN_ARTICLES:
				fragmentManager.beginTransaction()
						.replace(android.R.id.content, _uiArticlesFragment, LearnHomeworkArticlesFragment.TAG)
						.commit();
				break;
		}

	}

	public void replaceFragment(int homeworkType) {
		FragmentManager fragmentManager = getFragmentManager();
		switch (homeworkType) {
			case Constants.LEARN_TRANSLATIONS:
				fragmentManager
						.beginTransaction()
						.replace(android.R.id.content, _uiTranslationsFragment, LearnHomeworkTranslationFragment.TAG)
						.commit();
				break;
			case Constants.LEARN_ARTICLES:
				fragmentManager
						.beginTransaction()
						.replace(android.R.id.content, _uiArticlesFragment, LearnHomeworkArticlesFragment.TAG)
						.commit();
				break;
		}
	}

}