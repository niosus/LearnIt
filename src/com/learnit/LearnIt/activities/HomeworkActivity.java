/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.learnit.LearnIt.data_types.NotificationBuilder;
import com.learnit.LearnIt.fragments.ArticlesHomeworkFragment;
import com.learnit.LearnIt.fragments.HomeworkFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;

import java.util.ArrayList;

public class HomeworkActivity extends Activity {
	Fragment _uiTranslationsFragment;
	Fragment _uiArticlesFragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fragmentManager = getFragmentManager();

		// add a headless worker fragment to stack if not yet there
		Fragment worker = fragmentManager
				.findFragmentByTag(WorkerFragment.TAG);
		if (worker == null)
		{
			worker = new WorkerFragment();
			fragmentManager.beginTransaction()
					.add(worker, WorkerFragment.TAG)
					.commit();
		}

		// add a ui fragment to stack
		_uiTranslationsFragment = fragmentManager
				.findFragmentByTag(HomeworkFragment.TAG);
		if (_uiTranslationsFragment == null) {
			if (worker instanceof IWorkerJobInput) {
				_uiTranslationsFragment = new HomeworkFragment((IWorkerJobInput) worker);
				// extras contain words, translations and so on that we need to show
				// the data in the homework fragment. We pass them on to the fragment.
				_uiTranslationsFragment.setArguments(getIntent().getExtras());
			}
		}

		// add a ui fragment to stack
		_uiArticlesFragment = fragmentManager
				.findFragmentByTag(ArticlesHomeworkFragment.TAG);
		if (_uiArticlesFragment == null) {
			if (worker instanceof IWorkerJobInput) {
				_uiArticlesFragment = new ArticlesHomeworkFragment((IWorkerJobInput) worker);
				// extras contain words, translations and so on that we need to show
				// the data in the homework fragment. We pass them on to the fragment.
				_uiArticlesFragment.setArguments(getIntent().getExtras());
			}
		}

		ArrayList<Integer> types = getIntent().getIntegerArrayListExtra(NotificationBuilder.HOMEWORK_TYPE_TAG);
		if (types == null || types.isEmpty()) return;
		switch (types.get(0)) {
			case Constants.LEARN_TRANSLATIONS:
				fragmentManager.beginTransaction()
						.replace(android.R.id.content, _uiTranslationsFragment, HomeworkFragment.TAG)
						.commit();
				break;
			case Constants.LEARN_ARTICLES:
				fragmentManager.beginTransaction()
						.replace(android.R.id.content, _uiArticlesFragment, ArticlesHomeworkFragment.TAG)
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
						.replace(android.R.id.content, _uiTranslationsFragment, HomeworkFragment.TAG)
						.commit();
				break;
			case Constants.LEARN_ARTICLES:
				fragmentManager
						.beginTransaction()
						.replace(android.R.id.content, _uiArticlesFragment, ArticlesHomeworkFragment.TAG)
						.commit();
				break;
		}
	}

}