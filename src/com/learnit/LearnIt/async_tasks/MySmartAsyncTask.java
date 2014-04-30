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
import android.os.AsyncTask;

import com.learnit.LearnIt.interfaces.IWorkerEventListener;

public abstract class MySmartAsyncTask<S> extends AsyncTask<Object, Double, S> {

	public void updateContextAndCallback(Context context,
	                                     IWorkerEventListener taskActionCallback)
	{
		_context = context;
		_taskActionCallback = taskActionCallback;
	}

	@Override
	protected abstract S doInBackground(Object... unused);

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_taskActionCallback.onPreExecute();
	}

	/*
		When implementing this method in inherited
		class it is intended to call:
		_taskActionCallback.onFail()
		or
		_taskActionCallback.onSuccess()
		depending on the outcome.
		*/
	@Override
	protected void onPostExecute(S s) {
		super.onPostExecute(s);
		_taskActionCallback.onProgressUpdate(100.);
	}

	@Override
	protected void onProgressUpdate(Double... values) {
		super.onProgressUpdate(values);
		_taskActionCallback.onProgressUpdate(values);
	}

	/*
	This is a callback to send eventually to a controller activity.
	It needs to be called on every action children of this class
	will perform.
	*/
	protected IWorkerEventListener _taskActionCallback;
	protected Context _context;
}
