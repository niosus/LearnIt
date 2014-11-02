
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

package com.learnit.LearnIt.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.activities.HomeworkActivity;
import com.learnit.LearnIt.controllers.LearnHomeworkArticlesController;
import com.learnit.LearnIt.controllers.LearnOnTheGoController;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.MyAnimationHelper;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.util.ArrayList;


public class LearnHomeworkArticlesFragment extends LearnFragment {
	public static final String TAG = "articles_homework_frag";

	private int[] _btnIds = Constants.btnIdsArticles;

	@Override
	protected int[] btnIds() {
		return _btnIds;
	}

    public static LearnHomeworkArticlesFragment newInstance(IWorkerJobInput worker) {
        Log.d(Constants.LOG_TAG, "LearnHomeworkArticlesFragment, asking for instance");
        LearnHomeworkArticlesFragment learnHomeworkArticlesFragment =
                new LearnHomeworkArticlesFragment();
        learnHomeworkArticlesFragment.attachWorker(worker);
        return learnHomeworkArticlesFragment;
    }

    public void attachWorker(IWorkerJobInput worker) {
        Log.d(Constants.LOG_TAG, "LearnHomeworkArticlesFragment, attaching worker");
        _listener = new LearnHomeworkArticlesController(this, worker, btnIds());
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.homework_articles, container, false);
		_wordToAsk = (TextView) v.findViewById(R.id.word_to_ask);
		_wordToAsk.setMovementMethod(new ScrollingMovementMethod());
		for (int id: _btnIds) {
			(v.findViewById(id)).setOnClickListener(_listener);
		}
		setAll(View.INVISIBLE);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "LearnHomeworkArticlesFragment.onViewCreated(): saved instance is: " + savedInstanceState);
		super.onViewCreated(view, savedInstanceState);
		Bundle extras = getArguments();
		if (_listener instanceof LearnHomeworkArticlesController) {
			((LearnHomeworkArticlesController) _listener).getEverythingFromExtras(extras, this.getActivity());
		}
		// this looks hacky, but the articles should be set only once
		setButtonTexts(null, 0);
	}

	@Override
	public void openButtons() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
		View[] views = {
				v.findViewById(R.id.btn_first),
				v.findViewById(R.id.btn_second),
				v.findViewById(R.id.btn_third)};
		animationHelper.invokeForAllViews(views, R.anim.float_in_right, _listener);
	}

	@Override
	public void closeButtons() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
		View[] views = {
				v.findViewById(R.id.btn_first),
				v.findViewById(R.id.btn_second),
				v.findViewById(R.id.btn_third)};
		animationHelper.invokeForAllViews(views, R.anim.float_away_right, _listener);
	}

	@Override
	public void setButtonTexts(ArrayList<ArticleWordId> words, int direction) {
        Pair<String, String> langs = Utils.getCurrentLanguages(this.getActivity());
        String currentArticlesString = Constants.mArticlesMap.get(langs.first);
		String[] articles = currentArticlesString.split("\\s");
		for (int i = 0; i < _btnIds.length; ++i) {
			((TextView) v.findViewById(_btnIds[i])).setText(articles[i]);
		}

	}

	@Override
	public void setQueryWordText(ArticleWordId struct, int direction) {
		TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
		queryWord = struct.word;
		if (direction > 0) { _direction = direction; }
		switch (_direction) {
			case Constants.FROM_FOREIGN_TO_MY:
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
				String learnLang = sp.getString(getString(R.string.key_language_from), "null");
				if (null != struct.article) {
					// a small hack for German and capitalization of all nouns
					if ("de".equals(learnLang)) {
						queryWordTextView.setText(StringUtils.capitalize(queryWord));
					} else {
						queryWordTextView.setText(queryWord);
					}
				} else {
					Log.e(LOG_TAG, "No article present in article homework fragment.");
				}
				break;
			default: Log.e(LOG_TAG, "Wrong direction in article homework fragment.");
		}
	}

	public void stopActivity() {
		this.getActivity().finish();
	}

	public void askActivityToSwitchFragments(int homeworkType) {
		if (this.getActivity() instanceof HomeworkActivity) {
			((HomeworkActivity) this.getActivity()).replaceFragment(homeworkType);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
			Utils.removeOldSavedValues(sp, Constants.btnIdsTranslations);
		}
	}
}