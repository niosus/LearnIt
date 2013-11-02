package com.learnit.LearnIt.data_types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.stardict.StarDict;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;

public class GetDictTaskSmart extends MySmartAsyncTask {
	private String _langFrom, _langTo;

	public static final String CLASS_NAME = "GetDictTaskSmart";

	public void updateContextAndCallback(Context context,
	                                     WorkerFragment.OnTaskActionListener taskActionCallback)
	{
		super.updateContextAndCallback(context, taskActionCallback);
		Pair<String, String> langPair = Utils.getCurrentLanguages(context);
		if (langPair == null)
		{
			Log.d(Constants.LOG_TAG, "NULL!!!!!!!!!!!!!!!!");
		}
		_langFrom = langPair.first;
		_langTo = langPair.second;
	}

	private StarDict getDict(String langFrom, String langTo) {
		File sd = Environment.getExternalStorageDirectory();
		sd = new File(sd, "LearnIt");
		sd = new File(sd, langFrom + "-" + langTo);
		sd = new File(sd, "dict.ifo");
		StarDict dict = new StarDict(sd.getPath());
		if (!dict.boolAvailable) {
			dict = null;
		}
		return dict;
	}

	private void updateDatabaseFromDict(StarDict dict)
	{
		DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_DICT_FROM);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int numOfWords = dict.getTotalWords();
		String sql = "INSERT INTO " + DBHelper.DB_DICT_FROM + " (" + dbHelper.DICT_OFFSET_COLUMN_NAME + ", " + dbHelper.DICT_CHUNK_SIZE_COLUMN_NAME + ", " + dbHelper.WORD_COLUMN_NAME + ")  VALUES (?, ?, ?)";
		SQLiteStatement stmt = db.compileStatement(sql);
		db.beginTransaction();
		for (int i = 0; i < numOfWords; ++i) {
			Pair<Long, Long> position = dict.findWordMemoryOffsets(i);
			String wordTemp = dict.getWordByIndex(i);
			stmt.bindLong(1, position.first);
			stmt.bindLong(2, position.second);
			stmt.bindString(3, wordTemp);
			stmt.execute();
			stmt.clearBindings();
			float ratio = (float) i / numOfWords;
			int percent = (int) (ratio * 100);
			publishProgress(percent);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_DICT_FROM);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBHelper.DB_DICT_FROM, null, null);
	}

	@Override
	protected void onPostExecute(String dictName) {
		super.onPostExecute(dictName);
		if (dictName == null)
		{
			_taskActionCallback.onFail();
			return;
		}
		_taskActionCallback.onSuccess(dictName);

	}

	@Override
	protected String doInBackground(Void... unused) {
		StarDict dict = getDict(_langFrom, _langTo);
		if (null == dict) {
			return null;
		}
		updateDatabaseFromDict(dict);
		return dict.getDictName();
	}
}