package com.learnit.LearnIt.listeners;

import android.text.Editable;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.learnit.LearnIt.async_tasks.GetMyWordsTask;
import com.learnit.LearnIt.interfaces.IDictFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerDict;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public class DictController implements
		IListenerDict,
		IWorkerEventListener {
	IDictFragmentUpdate _fragmentUpdate;
	IWorkerJobInput _worker;

	public DictController(IDictFragmentUpdate target, IWorkerJobInput worker) {
		_fragmentUpdate = target;
		_worker = worker;
	}

	//
	// implements listener to UI
	//
	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean onLongClick(View v) {
		Log.d("my_logs", "onLongClick " + v.getId());
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

	}

	//
	// implements listener to AsyncTask worker
	//
	@Override
	public void onPreExecute() {

	}

	@Override
	public void onProgressUpdate(Integer... values) {

	}

	@Override
	public void onSuccessWords(List<String> result) {

	}

	@Override
	public void onSuccessTranslations(Pair<String, List<String>> result) {

	}

	@Override
	public void onSuccessMyWords(List<Map<String,String>> result) {
		_worker.onTaskFinished();
		_fragmentUpdate.setListEntries(result);
	}

	@Override
	public void onSuccessString(String result) {

	}

	@Override
	public void onSuccessCode(Integer errorCode) {

	}

	@Override
	public void onFail() {
		_worker.onTaskFinished();
	}

	@Override
	public void noTaskSpecified() {

	}

	@Override
	public void onTaskFinished() {

	}

	@Override
	public boolean taskRunning() {
		return _worker.taskRunning();
	}

	/*
	Text watcher implementation
	 */

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0) {
			_worker.attach(this);
			_worker.addTask(new GetMyWordsTask(s.toString()));
		}
	}
}
