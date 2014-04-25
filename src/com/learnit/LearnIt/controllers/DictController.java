package com.learnit.LearnIt.controllers;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetMyWordsTask;
import com.learnit.LearnIt.interfaces.IDictFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerDict;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerMyWords;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public class DictController implements
		IListenerDict,
		IWorkerEventListenerMyWords{
	IDictFragmentUpdate _fragmentUpdate;
	IWorkerJobInput _worker;

	String _longPressedWord;

	public DictController(IDictFragmentUpdate target, IWorkerJobInput worker) {
		_fragmentUpdate = target;
		_worker = worker;
	}

	//
	// implements listener to UI
	//
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search_clear:
				_fragmentUpdate.setListEntries(null);
				_fragmentUpdate.setWordText("");
				_fragmentUpdate.setWordClearButtonVisible(false);
				break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		Log.d("my_logs", "onLongClick " + v.getId());
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.getId() == R.id.edv_search_word && hasFocus) {
			_worker.addTask(new GetMyWordsTask(""), this);
		}
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
	public void onSuccessMyWords(List<Map<String,String>> result) {
		_worker.onTaskFinished();
		_fragmentUpdate.setListEntries(result);
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
		_worker.addTask(new GetMyWordsTask(s.toString()), this);
		if (s.toString().isEmpty()) {
			_fragmentUpdate.setWordClearButtonVisible(false);
		} else {
			_fragmentUpdate.setWordClearButtonVisible(true);
		}
	}

	@Override
	public boolean onCreateActionMode(final ActionMode actionMode, Menu menu) {
		MenuInflater inflater = actionMode.getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		new CountDownTimer(DateUtils.SECOND_IN_MILLIS * 5, DateUtils.SECOND_IN_MILLIS) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				actionMode.finish();
			}
		}.start();
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
//		String queryWord = _wordForActionMode;
		switch (menuItem.getItemId()) {
			case R.id.context_menu_edit:
				_fragmentUpdate.startEditWordActivity(_longPressedWord);
				actionMode.finish(); // Action picked, so close the CAB
				return true;
			case R.id.context_menu_delete:
				_fragmentUpdate.deleteWord(_longPressedWord);
				_fragmentUpdate.setWordText("");
				actionMode.finish();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		ActionMode.Callback mActionModeCallback = this;
		_fragmentUpdate.startActionMode(mActionModeCallback);
		_longPressedWord = ((HashMap<String,String>)parent.getAdapter().getItem(position)).get("word");
		_fragmentUpdate.setWordText(_longPressedWord);
		return false;
	}
}
