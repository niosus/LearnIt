package com.learnit.LearnIt.data_types;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.stardict.DictFile;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.util.List;

public class GetTranslationsTask extends MySmartAsyncTask {
	private String _word;
	private String _langFrom, _langTo;

	public GetTranslationsTask(String word)
	{
		super();
		_word = word;
	}

	public void updateContextAndCallback(Context context,
	                                     WorkerFragment.OnTaskActionListener taskActionCallback)
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
	protected void onPostExecute(List<String> dictName) {
		super.onPostExecute(dictName);
		if (dictName == null)
		{
			_taskActionCallback.onFail();
			return;
		}
		_taskActionCallback.onSuccess(dictName);
	}

	@Override
	protected List<String> doInBackground(Void... unused) {
		File sd = Environment.getExternalStorageDirectory();
		sd = new File(sd, "LearnIt");
		sd = new File(sd, _langFrom + "-" + _langTo);
		sd = new File(sd, "dict.dict");
		DictFile dictFile = new DictFile(sd.getPath());
		if (null != _word) {
			String newWord = StringUtils.stripFromArticle(_context, _word);
			DBHelper dbHelperDict = new DBHelper(_context, DBHelper.DB_DICT_FROM);
			Pair<Long, Long> pair = dbHelperDict.getDictOffsetAndSize(newWord);
			return StringUtils.parseDictOutput(dictFile.getWordData(pair.first, pair.second), _langFrom);
		}
		return null;
	}
}