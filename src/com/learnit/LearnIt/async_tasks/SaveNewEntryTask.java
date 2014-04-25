package com.learnit.LearnIt.async_tasks;

import android.content.Context;

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerNewEntry;

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
		if (_taskActionCallback instanceof IWorkerEventListenerNewEntry) {
			((IWorkerEventListenerNewEntry) _taskActionCallback).onSuccessCode(exitCode);
		} else {
			throw new ClassCastException(
					_taskActionCallback.getClass().getSimpleName()
							+ " must implement "
							+ IWorkerEventListenerNewEntry.class.getSimpleName());
		}
	}

	@Override
	protected Integer doInBackground(Object... unused) {
		DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_WORDS);
		int exitCode = dbHelper.writeToDB(_word, _translation);
		return exitCode;
	}
}