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