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