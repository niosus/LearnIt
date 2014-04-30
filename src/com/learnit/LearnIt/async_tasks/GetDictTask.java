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
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerGetDict;
import com.learnit.LearnIt.stardict.StarDict;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;

public class GetDictTask extends MySmartAsyncTask<String> {
	private String _langFrom, _langTo;

	@Override
	public void updateContextAndCallback(Context context,
	                                     IWorkerEventListener taskActionCallback)
	{
		super.updateContextAndCallback(context, taskActionCallback);
		Pair<String, String> langPair = Utils.getCurrentLanguages(context);
		if (langPair == null)
		{
			Log.e(Constants.LOG_TAG, "NULL!!!!!!!!!!!!!!!!");
			return;
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
			Double percent = (ratio * 100.);
			publishProgress(percent);
			// stop the madness if someone has cancelled the task
			if (isCancelled()) {
				Log.d("my_logs", "oh maaan, seems I am being killed... :( so sad...");
				break;
			}
		}
		if (!isCancelled()) {
			db.setTransactionSuccessful();
		}
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
	protected void onCancelled() {
		super.onCancelled();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
		sp.edit().putString(Constants.CURRENT_HELP_DICT_TAG, "null").commit();
		DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_DICT_FROM);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBHelper.DB_DICT_FROM, null, null);
		Log.d("my_logs", "cancelled getting the dictionary :(");
		_taskActionCallback.onFail();
	}

	@Override
	protected void onPostExecute(String dictName) {
		super.onPostExecute(dictName);
		if (dictName == null)
		{
			_taskActionCallback.onFail();
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
			sp.edit().putString(Constants.CURRENT_HELP_DICT_TAG, "null").commit();
			return;
		}
		if (_taskActionCallback instanceof IWorkerEventListenerGetDict) {
			((IWorkerEventListenerGetDict) _taskActionCallback).onSuccessDictName(dictName);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
			sp.edit().putString(Constants.CURRENT_HELP_DICT_TAG, _langFrom + " " + _langTo).commit();
		} else {
			throw new ClassCastException(
					_taskActionCallback.getClass().getSimpleName()
							+ " must implement "
							+ IWorkerEventListenerGetDict.class.getSimpleName());
		}
	}

	@Override
	protected String doInBackground(Object... unused) {
		StarDict dict = getDict(_langFrom, _langTo);
		if (null == dict) {
			return null;
		}
		updateDatabaseFromDict(dict);
		return dict.getDictName();
	}
}