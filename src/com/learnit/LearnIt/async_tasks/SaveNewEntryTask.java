package com.learnit.LearnIt.async_tasks;

import android.content.Context;

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;

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
	                                     IWorkerEventListener taskActionCallback)
	{
		super.updateContextAndCallback(context, taskActionCallback);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Integer exitCode) {
		super.onPostExecute(exitCode);
		if (exitCode == null)
		{
			_taskActionCallback.onFail();
			return;
		}
		_taskActionCallback.onSuccessCode(exitCode);

	}

	@Override
	protected Integer doInBackground(Object... unused) {
		DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_WORDS);
		int exitCode = dbHelper.writeToDB(_word, _translation);
		return exitCode;
	}
}