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
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerTranslations;
import com.learnit.LearnIt.stardict.DictFile;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.util.List;

public class GetTranslationsTask extends MySmartAsyncTask<Pair<String, List<String>>> {
	private String _word;
	private String _langFrom, _langTo;

	public GetTranslationsTask(String word)
	{
		super();
		_word = word;
	}

	public void updateContextAndCallback(Context context,
	                                     IWorkerEventListener taskActionCallback)
	{
		super.updateContextAndCallback(context, taskActionCallback);
		Pair<String, String> langPair = Utils.getCurrentLanguages(context);
		if (langPair == null)
		{
			Log.e(Constants.LOG_TAG, "updateContextAndCallback pair is null!!!!!!!!!!!!!!!!");
		}
		_langFrom = langPair.first;
		_langTo = langPair.second;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Pair<String, List<String>> articleTranslationsListPair) {
		super.onPostExecute(articleTranslationsListPair);
		if (articleTranslationsListPair == null)
		{
			_taskActionCallback.onFail();
			return;
		}
		if (_taskActionCallback instanceof IWorkerEventListenerTranslations) {
			((IWorkerEventListenerTranslations) _taskActionCallback).onSuccessTranslations(articleTranslationsListPair);
		} else {
			throw new ClassCastException(
					_taskActionCallback.getClass().getSimpleName()
							+ " must implement "
							+ IWorkerEventListenerTranslations.class.getSimpleName());
		}
	}

	@Override
	protected Pair<String, List<String>> doInBackground(Object... unused) {
		File sd = Environment.getExternalStorageDirectory();
		sd = new File(sd, "LearnIt");
		sd = new File(sd, _langFrom + "-" + _langTo);
		sd = new File(sd, "dict.dict");
		DictFile dictFile = new DictFile(sd.getPath());
		if (null != _word) {
			String newWord = StringUtils.stripFromArticle(_context, _word);
			DBHelper dbHelperDict = new DBHelper(_context, DBHelper.DB_DICT_FROM);
			Pair<Long, Long> pair = dbHelperDict.getDictOffsetAndSize(newWord);
			if (pair == null)
				return null;
			return StringUtils.parseDictOutput(dictFile.getWordData(pair.first, pair.second), _langFrom);
		}
		return null;
	}
}