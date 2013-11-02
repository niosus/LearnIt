package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.LearnIt.data_types.MySmartAsyncTask;

import java.util.LinkedList;
import java.util.Queue;

/*
This class is a headless fragment with no GUI.
It is used to carry out heavy async tasks
*/
public class WorkerFragment extends Fragment{
    final String LOG_TAG = "my_logs";
    public static String TAG = "work_fragment";

	public static final int GET_DICT_TYPE = 1;

	// Container Activity must implement this interface
	public interface OnTaskActionListener {
		public void onPreExecute();
		public void onFail();
		public void onSuccess(String name);
		public void onProgressUpdate(Integer... values);
		public void noTaskSpecified();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (_taskQueue == null)
		{
			_taskRunning = false;
		}
		try {
			_taskActionCallback = (OnTaskActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " " + _taskActionCallback.getClass().getName());
		}
		for (MySmartAsyncTask task: _taskQueue)
		{
			task.updateContextAndCallback(activity, _taskActionCallback);
		}
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

	public void addNewTask(MySmartAsyncTask task)
	{
		if (_taskQueue==null)
		{
			_taskQueue = new LinkedList<>();
		}
		_taskQueue.add(task);
		startNextTaskIfNeeded();
	}

	private boolean startNextTaskIfNeeded()
	{
		// if we are not yet attached to an activity do nothing
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

	// Defines a headless fragment with no GUI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

	private static Queue<MySmartAsyncTask> _taskQueue;
	private static boolean _taskRunning;
	private OnTaskActionListener _taskActionCallback;

}
