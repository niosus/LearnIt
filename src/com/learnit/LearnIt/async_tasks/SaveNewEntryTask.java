package com.learnit.LearnIt.async_tasks;

import android.content.Context;

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.fragments.WorkerFragment;

public class SaveNewEntryTask extends MySmartAsyncTask<Integer> {
	String _word;
	String _translation;

	public SaveNewEntryTask(String word, String translation)
	{
		super();
		_word = word;
		_translation = translation;
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
	protected void onPostExecute(Integer dictName) {
		super.onPostExecute(dictName);
		if (dictName == null)
		{
			_taskActionCallback.onFail(_fragmentId);
			return;
		}
		_taskActionCallback.onSuccess(_fragmentId, dictName);

	}

	@Override
	protected Integer doInBackground(Object... unused) {
		DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_WORDS);
		int exitCode = dbHelper.writeToDB(_word, _translation);
		return exitCode;
	}
}