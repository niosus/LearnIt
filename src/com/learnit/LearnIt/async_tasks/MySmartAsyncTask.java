package com.learnit.LearnIt.async_tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.learnit.LearnIt.fragments.WorkerFragment;

public abstract class MySmartAsyncTask<S> extends AsyncTask<Object, Integer, S> {

	public static final String CLASS_NAME = "MySmartAsyncTask";


	public void updateContextAndCallback(Context context,
	                                     WorkerFragment.OnTaskActionListener taskActionCallback)
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
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		_taskActionCallback.onProgressUpdate(values);
	}

	/*
	This is a callback to send eventually to a controller activity.
	It needs to be called on every action children of this class
	will perform.
	*/
	protected WorkerFragment.OnTaskActionListener _taskActionCallback;

	protected Context _context;
}
