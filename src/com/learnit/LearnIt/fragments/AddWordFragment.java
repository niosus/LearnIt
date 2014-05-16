

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

package com.learnit.LearnIt.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.controllers.AddWordsController;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IAddWordsFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerAddWords;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AddWordFragment extends MySmartFragment
		implements IAddWordsFragmentUpdate {
    protected static final String LOG_TAG = "my_logs";
	public static final String TAG = "add_words_fragment";
	private EditText _word;
	private EditText _translation;
	private ImageButton _clearButtonWord, _clearButtonTrans;
	private MenuItem _saveMenuItem;
	protected IListenerAddWords _listener;

    public static AddWordFragment newInstance(IWorkerJobInput worker) {
        AddWordFragment addWordFragment = new AddWordFragment();
        addWordFragment.attachWorker(worker);
        return addWordFragment;
    }

	public void attachWorker(IWorkerJobInput worker) {
		_listener = new AddWordsController(this, worker);
	}

	@Override
	public void onPause() {
		super.onPause();
		Crouton.cancelAllCroutons();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actions_add_words, menu);
	    _saveMenuItem = menu.findItem(R.id.save_item);
		if (_listener != null && _saveMenuItem != null) {
			_saveMenuItem.setOnMenuItemClickListener(_listener);
		}
		if (_saveMenuItem != null) {
			_saveMenuItem.setVisible(false);
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    View _view = inflater.inflate(R.layout.add_word_fragment, container, false);
	    if (_view == null)
		    return null;
		_clearButtonWord = (ImageButton) _view.findViewById(R.id.btn_add_word_clear);
	    _clearButtonTrans = (ImageButton) _view.findViewById(R.id.btn_add_trans_clear);
		_clearButtonWord.setOnClickListener(_listener);
		_clearButtonTrans.setOnClickListener(_listener);
	    _clearButtonWord.setVisibility(View.INVISIBLE);
	    _clearButtonTrans.setVisibility(View.INVISIBLE);

	    _word = (EditText) _view.findViewById(R.id.edv_add_word);
	    _translation = (EditText) _view.findViewById(R.id.edv_add_translation);
	    _word.setOnFocusChangeListener(_listener);
	    _translation.setOnFocusChangeListener(_listener);
	    _word.addTextChangedListener(_listener);
	    _translation.addTextChangedListener(_listener);
//
	    ListView _list = (ListView) _view.findViewById(R.id.list_of_add_words);
	    _list.setOnItemClickListener(_listener);

        return _view;
    }

	/*
	*
	*   Implementing IAddWordsFragmentUpdate interface
	*
	 */

	@Override
	public void setWordText(String word) {
		if (_word != null) {
			_word.setText(word);
			_word.setSelection(_word.length());
		}
	}

	@Override
	public void setTranslationText(String translation) {
		if (_translation != null) {
			_translation.setText(translation);
			_translation.setSelection(_translation.length());
		}
	}

	@Override
	public void appendWordText(String word) {

	}

	@Override
	public void appendTranslationText(String translation) {
		if (_translation != null && _translation.getText() != null) {
			if (_translation.getText().toString().contains(translation)) { return; }
			if (_translation.getText().toString().isEmpty()) {
				_translation.setText(translation);
				_translation.setSelection(_translation.length());
				return;
			}
			_translation.setText(_translation.getText().toString() + ", " + translation);
			_translation.setSelection(_translation.length());
		}
	}

	@Override
	public void setListEntries(List<String> words) {
		if (words == null)
		{
			((ListView) this.getView().findViewById(R.id.list_of_add_words))
					.setAdapter(null);
			return;
		}
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<>(this.getActivity(),
				android.R.layout.simple_list_item_1, words);
		((ListView) this.getView().findViewById(R.id.list_of_add_words))
				.setAdapter(adapter);
	}

	@Override
	public void setWordClearButtonVisible(boolean state) {
		if (state) { _clearButtonWord.setVisibility(View.VISIBLE); }
		else { _clearButtonWord.setVisibility(View.INVISIBLE); }
	}

	@Override
	public void setTranslationClearButtonVisible(boolean state) {
		if (state) { _clearButtonTrans.setVisibility(View.VISIBLE); }
		else { _clearButtonTrans.setVisibility(View.INVISIBLE); }
	}

	public void setViewFocused(int id)
	{
		if (id == _word.getId())
			_word.requestFocus();
		if (id == _translation.getId())
			_translation.requestFocus();
	}

	@Override
	public String getWord() {
		return _word.getText().toString();
	}

	@Override
	public String getTrans() {
		return _translation.getText().toString();
	}

	@Override
	public void toInitialState() {
		setViewFocused(R.id.edv_add_word);
		setWordText("");
		setTranslationText("");
		setListEntries(null);
		setWordClearButtonVisible(false);
		setTranslationClearButtonVisible(false);
		setMenuItemVisible(false);
	}

	// ended implementing interface

	public void setMenuItemVisible(boolean visible)
	{
		_saveMenuItem.setVisible(visible);
	}

	public void addArticle(String article)
	{
		try
		{
			if (_word.getText().toString().contains(article))
				return;
			_word.setText(article + " " + _word.getText());
		}
		catch (NullPointerException ex)
		{
			Log.e(LOG_TAG, ex.getMessage() + "in addArticle");
		}
	}

	public void showMessage(int exitCode) {
		Crouton crouton = null;
		switch (exitCode) {
			case DBHelper.EXIT_CODE_OK:
				crouton = Crouton.makeText(getActivity(), getString(R.string.crouton_word_saved, getWord()), Style.CONFIRM);
				break;
			case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
				crouton = Crouton.makeText(getActivity(), getString(R.string.crouton_word_already_present, getWord()), Style.ALERT);
				break;
			case DBHelper.EXIT_CODE_WORD_UPDATED:
				crouton = Crouton.makeText(getActivity(), getString(R.string.crouton_word_updated, getWord()), Style.CONFIRM);
				break;
            case DBHelper.EXIT_CODE_EMPTY_INPUT:
                crouton = Crouton.makeText(getActivity(), getString(R.string.crouton_empty_input), Style.ALERT);
                break;
		}
		if (crouton == null) { return; }
        final int duration_half_second = 500;
		crouton.setConfiguration(new Configuration.Builder().setDuration(3 * duration_half_second).build());
		crouton.show();
	}
}