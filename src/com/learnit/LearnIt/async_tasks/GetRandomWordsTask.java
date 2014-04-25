package com.learnit.LearnIt.async_tasks;

import android.content.Context;

import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.DBHelper;
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
		if (words == null)
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
		DBHelper dbHelperDict = new DBHelper(_context, DBHelper.DB_WORDS);
		return dbHelperDict.getRandomWords(_numOfWords, _omitWord, _nounsOnlyIndicator);
	}
}