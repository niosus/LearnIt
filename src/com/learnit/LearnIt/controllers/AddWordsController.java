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

package com.learnit.LearnIt.controllers;

import android.text.Editable;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetHelpWordsGoogleTask;
import com.learnit.LearnIt.async_tasks.GetTranslationsWebTask;
import com.learnit.LearnIt.async_tasks.SaveNewEntryTask;
import com.learnit.LearnIt.interfaces.IAddWordsFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerAddWords;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerHelpWords;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerNewEntry;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerTranslations;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.List;

/**
 * Created by igor on 4/2/14.
 */
public class AddWordsController implements
		IListenerAddWords,
		IWorkerEventListenerHelpWords,
		IWorkerEventListenerTranslations,
		IWorkerEventListenerNewEntry {
	IAddWordsFragmentUpdate _fragmentUpdate;
	IWorkerJobInput _worker;
	View _focused;

	public AddWordsController(IAddWordsFragmentUpdate target, IWorkerJobInput worker) {
		_fragmentUpdate = target;
		_worker = worker;
	}

	//
	// implements listener to UI
	//
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_add_word_clear:
				_fragmentUpdate.setWordText("");
				_fragmentUpdate.setTranslationText("");
				_fragmentUpdate.setViewFocused(R.id.edv_add_word);
				_fragmentUpdate.setWordClearButtonVisible(false);
				_fragmentUpdate.setListEntries(null);
				_fragmentUpdate.setMenuItemVisible(false);
				break;
			case R.id.btn_add_trans_clear:
				_fragmentUpdate.setTranslationText("");
				_fragmentUpdate.setViewFocused(R.id.edv_add_translation);
				_fragmentUpdate.setTranslationClearButtonVisible(false);
				_fragmentUpdate.setMenuItemVisible(false);
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
		if (hasFocus) { _focused = v; }
		if (v.getId() == R.id.edv_add_translation) {
			_worker.addTask(new GetTranslationsWebTask(_fragmentUpdate.getWord()), this);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (_focused == null) return;
		switch (_focused.getId()) {
			case R.id.edv_add_word:
				if (_fragmentUpdate.getWord().length() > 0) {
//                    _worker.addTask(new GetHelpWordsTask(s.toString()), this);
                    _worker.addTask(new GetHelpWordsGoogleTask(s.toString()), this);
					_fragmentUpdate.setWordClearButtonVisible(true);
				}
				else {
					_fragmentUpdate.setWordClearButtonVisible(false);
					_fragmentUpdate.setListEntries(null);
				}
				break;
			case R.id.edv_add_translation:
				if (_fragmentUpdate.getTrans().length() > 0) {
					_fragmentUpdate.setTranslationClearButtonVisible(true);
				} else {
					_fragmentUpdate.setTranslationClearButtonVisible(false);
				}
				break;
		}
		if (_fragmentUpdate.getWord().length() > 0 &&
				_fragmentUpdate.getTrans().length() > 0) {
			_fragmentUpdate.setMenuItemVisible(true);
		}
	}

	//
	// implements listener to AsyncTask worker
	//
	@Override
	public void onPreExecute() {

	}

	@Override
	public void onProgressUpdate(Double... values) {

	}

	@Override
	public void onSuccessWords(List<String> result) {
		_worker.onTaskFinished();
		switch (_focused.getId()) {
			case R.id.edv_add_word:
				_fragmentUpdate.setListEntries(result);
				break;
		}
	}

	@Override
	public void onSuccessTranslations(Pair<String, List<String>> result) {
		_worker.onTaskFinished();
		switch (_focused.getId()) {
			case R.id.edv_add_word:
				break;
			case R.id.edv_add_translation:
				if (result.first != null) {
					_fragmentUpdate.addArticle(result.first);
				}
				_fragmentUpdate.setListEntries(result.second);
				break;
		}
	}

	@Override
	public void onSuccessCode(Integer errorCode) {
		_worker.onTaskFinished();
		_fragmentUpdate.showMessage(errorCode);
		_fragmentUpdate.toInitialState();
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

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.save_item:
				_worker.addTask(new SaveNewEntryTask(
						_fragmentUpdate.getWord(),
						_fragmentUpdate.getTrans()), this);
				break;
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (_focused.getId())
		{
			case R.id.edv_add_word:
				if (view instanceof TextView) {
					_fragmentUpdate.setWordText(((TextView) view).getText().toString());
					_fragmentUpdate.setViewFocused(R.id.edv_add_translation);
				}
//				frag.setViewFocused(R.id.edv_add_translation);
				break;
			case R.id.edv_add_translation:
				if (view instanceof TextView) {
					_fragmentUpdate.appendTranslationText(((TextView) view).getText().toString());
				}
				break;
		}
	}
}
