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

import android.util.Log;
import android.view.View;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetRandomWordsTask;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by igor on 4/2/14.
 */
public class LearnOnTheGoController extends LearnController {

	public LearnOnTheGoController(ILearnFragmentUpdate target, IWorkerJobInput worker, int[] btnIds) {
		super(target, worker, btnIds);
	}

	@Override
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds) {
		_worker.onTaskFinished();
		_failCounter = 0;
		Log.d("my_logs", "success random words LearnOnTheGo size " + articleWordIds.size());
		if (articleWordIds.size() < 1) return;
		Random rand = new Random();
		_fragmentUpdate.updateDirectionOfTranslation();
		_correctAnswerId = rand.nextInt(articleWordIds.size());
		_fragmentUpdate.setQueryWordText(
				articleWordIds.get(_correctAnswerId), 0);
		_fragmentUpdate.setButtonTexts(articleWordIds, 0);
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

	@Override
	public void showNext() {
		fetchRandomWords(_btnIds.length, null);
	}

	@Override
	public void onFail() {
		_worker.onTaskFinished();
		_failCounter++;
		if (_failCounter > 2) {
			_fragmentUpdate.setQueryWordTextFail();
			_fragmentUpdate.setButtonTexts(null, 0);
		} else if (_failCounter == 1) {
			_worker.addTask(new GetRandomWordsTask(null, _btnIds.length, Constants.NOT_NOUNS), this);
		} else {
            _worker.addTask(new GetRandomWordsTask(null, _btnIds.length, Constants.ONLY_NOUNS), this);
        }
	}
}
