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

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerMyWords;
import com.learnit.LearnIt.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class GetMyWordsTask extends MySmartAsyncTask<List<Map<String,String>>>{
	String _word;

	public GetMyWordsTask(String word)
	{
		super();
		_word = word;
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
	protected void onPostExecute(List<Map<String,String>> words) {
		super.onPostExecute(words);
		if (words == null)
		{
			_taskActionCallback.onFail();
			return;
		}
		if (_taskActionCallback instanceof IWorkerEventListenerMyWords) {
			((IWorkerEventListenerMyWords) _taskActionCallback).onSuccessMyWords(words);
		} else {
			throw new ClassCastException(
					_taskActionCallback.getClass().getSimpleName()
							+ " must implement "
							+ IWorkerEventListenerMyWords.class.getSimpleName());
		}

	}

	@Override
	protected List<Map<String,String>> doInBackground(Object... unused) {
		DBHelper dbHelperDict = new DBHelper(_context, DBHelper.DB_WORDS);
		String newWord = StringUtils.stripFromArticle(_context, _word);
		return dbHelperDict.getWords(newWord);
	}
}