package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.LearnIt.async_tasks.MySmartAsyncTask;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.LinkedList;
import java.util.Queue;

/*
This class is a headless fragment with no GUI.
It is used to carry out heavy async tasks
*/
public class WorkerFragment extends Fragment
		implements IWorkerJobInput {
    final String LOG_TAG = "my_logs";
    public static String TAG = "work_fragment";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_context = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	// Defines a headless fragment with no GUI
	public View onCreateView(
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		return null;
	}

	public void attach(IWorkerEventListener listener) {
		_taskActionCallback = listener;
	}

	public void addTask(MySmartAsyncTask task)
	{
		task.updateContextAndCallback(_context, _taskActionCallback);
		_taskQueue.add(task);
		startNextTaskIfNeeded();
	}

	public void onTaskFinished() {
		Log.d(LOG_TAG, "task set to not running !!!!!!!!!!!!!!!! finished");
		_taskRunning = false;
		_taskQueue.poll();
		startNextTaskIfNeeded();
	}

	public boolean taskRunning()
	{
		return _taskRunning;
	}

	public boolean startNextTaskIfNeeded()
	{
		// if we are not yet attached to an activity do nothing
		Log.d(LOG_TAG, "startNextTaskIfNeeded, queue size = " + _taskQueue.size());
		if (null == this.getActivity())
			return false;
		if (!_taskRunning && _taskQueue.size() > 0)
		{
			_taskQueue.peek();
			if (_taskQueue.peek() == null) {
				_taskActionCallback.noTaskSpecified();
				return false;
			}
			_taskQueue.peek().execute();
			Log.d(LOG_TAG, "task set to RUNNING !!!!!!!!!!!!!!!!");
			_taskRunning = true;
		}
		return true;
	}

	private static Queue<MySmartAsyncTask> _taskQueue = new LinkedList<>();
	private static boolean _taskRunning = false;
	private IWorkerEventListener _taskActionCallback;
	private Context _context;

}
