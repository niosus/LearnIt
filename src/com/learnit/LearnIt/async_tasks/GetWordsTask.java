package com.learnit.LearnIt.async_tasks;

import android.content.Context;

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.utils.StringUtils;

import java.util.List;

public class GetWordsTask extends MySmartAsyncTask<List<String>> {
	String _word;

	public GetWordsTask(String word)
	{
		super();
		_word = word;
	}

	public void updateContextAndCallback(Context context,
	                                     WorkerFragment.OnTaskActionListener taskActionCallback,
	                                     int fragmentId)
	{
		super.updateContextAndCallback(context, taskActionCallback, fragmentId);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(List<String> dictName) {
		super.onPostExecute(dictName);
		if (dictName == null)
		{
			_taskActionCallback.onFail(_fragmentId);
			return;
		}
		_taskActionCallback.onSuccess(_fragmentId, dictName);

	}

	@Override
	protected List<String> doInBackground(Object... unused) {
		DBHelper dbHelperDict = new DBHelper(_context, DBHelper.DB_DICT_FROM);
		String newWord = StringUtils.stripFromArticle(_context, _word);
		return dbHelperDict.getHelpWords(newWord);
	}
}