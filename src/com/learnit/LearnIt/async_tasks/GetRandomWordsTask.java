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

package com.learnit.LearnIt.async_tasks;

import android.content.Context;

import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.data_types.FactoryDbHelper;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerRandomWords;

import java.util.ArrayList;

public class GetRandomWordsTask extends MySmartAsyncTask<ArrayList<ArticleWordId>>{
	String _omitWord;
	int _numOfWords;
	int _nounsOnlyIndicator;

	public GetRandomWordsTask(String omitWord, int numOfWords, int nounsOnlyIndicator)
	{
		super();
		_omitWord = omitWord;
		_numOfWords = numOfWords;
		_nounsOnlyIndicator = nounsOnlyIndicator;
	}

	public void updateContextAndCallback(Context context,
	                                     IWorkerEventListener taskActionCallback)
	{
		super.updateContextAndCallback(context, taskActionCallback);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(ArrayList<ArticleWordId> words) {
		super.onPostExecute(words);
		if (words == null || words.isEmpty())
		{
			_taskActionCallback.onFail();
			return;
		}
		if (_taskActionCallback instanceof IWorkerEventListenerRandomWords) {
			((IWorkerEventListenerRandomWords) _taskActionCallback).onSuccessRandomWords(words);
		} else {
			throw new ClassCastException(
					_taskActionCallback.getClass().getSimpleName()
							+ " must implement "
							+ IWorkerEventListenerRandomWords.class.getSimpleName());
		}

	}

	@Override
	protected ArrayList<ArticleWordId> doInBackground(Object... unused) {
		DBHelper dbHelperDict = FactoryDbHelper.createDbHelper(_context, DBHelper.DB_WORDS);
		return dbHelperDict.getRandomWords(_numOfWords, _omitWord, _nounsOnlyIndicator);
	}
}