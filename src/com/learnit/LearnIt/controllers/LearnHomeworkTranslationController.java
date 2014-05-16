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

package com.learnit.LearnIt.controllers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetRandomWordsTask;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.NotificationBuilder;
import com.learnit.LearnIt.fragments.LearnHomeworkTranslationFragment;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by igor on 4/2/14.
 */
public class LearnHomeworkTranslationController extends LearnController {
	public static final String LOG_TAG = "my_logs";
	private static final String ALIVE_IDS_TAG = "current_ids";
	private ArrayList<Integer> _ids;
	private ArrayList<String> _words;
	private ArrayList<String> _translations;
	private ArrayList<Integer> _directionsOfTrans;
	private ArrayList<Integer> _typesOfHomework;
	private ArrayList<String> _articles;
	private ArrayList<String> _prefixes;
	private int _currentNotificationIndex;
	ArticleWordId _correctEntry = null;
	int _direction;
	int _currentTypeOfHomework;
	Context _context;

	public LearnHomeworkTranslationController(ILearnFragmentUpdate target, IWorkerJobInput worker, int[] btnIds) {
		super(target, worker, btnIds);
	}

	public void getEverythingFromExtras(Bundle extras, Context context) {
		if (context == null) { throw new NullPointerException("context is null in LearnHomeworkTranslationController"); }
		_context = context;
		if (extras == null) { throw new NullPointerException("extras are null in LearnHomeworkTranslationController"); }
		_ids = extras.getIntegerArrayList(NotificationBuilder.IDS_TAG);
		_words = extras.getStringArrayList(NotificationBuilder.WORDS_TAG);
		_translations = extras.getStringArrayList(NotificationBuilder.TRANSLATIONS_TAG);
		_directionsOfTrans = extras.getIntegerArrayList(NotificationBuilder.DIRECTIONS_OF_TRANS_TAG);
		_articles = extras.getStringArrayList(NotificationBuilder.ARTICLES_TAG);
		_prefixes = extras.getStringArrayList(NotificationBuilder.PREFIXES_TAG);
		_typesOfHomework = extras.getIntegerArrayList(NotificationBuilder.HOMEWORK_TYPE_TAG);
		_currentNotificationIndex = extras.getInt(NotificationBuilder.CURRENT_NOTIFICATION_INDEX, -1);
		showNext();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == _btnIds[_correctAnswerId]) {
			NotificationManager notificationManager =
					(NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(_correctEntry.id);
			_fragmentUpdate.updateWordWeight(_numOfWrongAnswers);
			_numOfWrongAnswers = 0;
			_fragmentUpdate.closeWord();
			_fragmentUpdate.closeButtons();
			updateListOfAliveIds();

		} else {
			if (v instanceof Button) {
				_numOfWrongAnswers++;
				_fragmentUpdate.shakeView(v);
			}
		}
	}

	private boolean findNextId()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
		String idsOld = sp.getString(ALIVE_IDS_TAG, "");
		int counter=0;
		while (!idsOld.contains(_ids.get(_currentNotificationIndex).toString()))
		{
			_currentNotificationIndex++;
			_currentNotificationIndex%=_ids.size();
			if (counter++==_ids.size())
				return false;
		}
		_correctEntry = new ArticleWordId(
				_articles.get(_currentNotificationIndex),
				_prefixes.get(_currentNotificationIndex),
				_words.get(_currentNotificationIndex),
				_translations.get(_currentNotificationIndex),
				_ids.get(_currentNotificationIndex)
		);
		_direction = _directionsOfTrans.get(_currentNotificationIndex);
		_currentTypeOfHomework = _typesOfHomework.get(_currentNotificationIndex);
		Log.d(LOG_TAG, "got intent word=" + _correctEntry.word + " id = "
				+ _correctEntry.id);
		return true;
	}

	protected void updateListOfAliveIds()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
		Log.d(LOG_TAG, sp.toString());
		SharedPreferences.Editor editor = sp.edit();
		String idsOld = sp.getString(ALIVE_IDS_TAG, "");
		Log.d(LOG_TAG, idsOld);
		String idsNew="";
		for (Integer idInt: _ids)
		{
			if (idInt== _correctEntry.id)
				continue;
			if (!idsOld.contains(idInt.toString()))
				continue;
			idsNew+=idInt.toString()+" ";
		}
		editor.putString(ALIVE_IDS_TAG, idsNew);
		Log.d(LOG_TAG, idsNew);
		editor.commit();
	}

	@Override
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds) {
		_worker.onTaskFinished();
		_fragmentUpdate.updateDirectionOfTranslation();
		Random rand = new Random();
		_correctAnswerId = rand.nextInt(articleWordIds.size() + 1);
		articleWordIds.add(_correctAnswerId, _correctEntry);
		_fragmentUpdate.setQueryWordText(_correctEntry, _direction);
		_fragmentUpdate.setButtonTexts(articleWordIds, _direction);
		_fragmentUpdate.openButtons();
		_fragmentUpdate.openWord();
		_fragmentUpdate.setAll(View.VISIBLE);
	}

	@Override
	public void onAnimationFinished(int id, boolean ignore) {
		if (ignore)
			return;
		switch (id)
		{
			case (R.anim.float_away_down_second_row):
				_fragmentUpdate.setAll(View.INVISIBLE);
				break;
			case (R.anim.close_word):
				_fragmentUpdate.setAll(View.INVISIBLE);
				showNext();
				break;
		}
	}

	private boolean checkNextFragmentType() {
		if (_currentTypeOfHomework == Constants.LEARN_ARTICLES) {
			if (_fragmentUpdate instanceof LearnHomeworkTranslationFragment) {
				((LearnHomeworkTranslationFragment) _fragmentUpdate)
						.askActivityToSwitchFragments(_currentTypeOfHomework);
				return false;
			}
		}
		return true;
	}

	@Override
	public void showNext() {
		if (findNextId()) {
			if (checkNextFragmentType()) {
				fetchRandomWords(_btnIds.length - 1, _correctEntry);
			}
		} else {
			if (_fragmentUpdate instanceof LearnHomeworkTranslationFragment) {
				((LearnHomeworkTranslationFragment) _fragmentUpdate).stopActivity();
			}
		}
	}

	@Override
	public void onFail() {
        // the fail means that we have failed to fetch the random words
        // this can be either for the first time (can be caused by the fact that
        // we are looking for nouns and there are none) or for the second time,
        // which means that we have already tried also not nouns, but still failed,
        // so no words present to show to the user.
		_worker.onTaskFinished();
		_failCounter++;
		if (_failCounter > 1) {
			_fragmentUpdate.setQueryWordTextFail();
			_fragmentUpdate.setButtonTexts(null, 0);
			_fragmentUpdate.setAll(View.VISIBLE);
            NotificationManager notificationManager =
                    (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(_correctEntry.id);
            updateListOfAliveIds();
		} else {
			_worker.addTask(new GetRandomWordsTask(_correctEntry.word, _btnIds.length - 1, Constants.NOT_NOUNS), this);
		}
	}
}
