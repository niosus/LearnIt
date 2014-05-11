
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.activities.EditWord;
import com.learnit.LearnIt.controllers.DictController;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.IDictFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerDict;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.util.List;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DictFragment extends MySmartFragment
		implements IDictFragmentUpdate {
    private EditText _edtWord;
    private ImageButton _btnClear;
	private ListView _listView;
	protected IListenerDict _listener;
	public static final String TAG = "dict_fragment";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    _v = inflater.inflate(R.layout.dict_fragment, container, false);

        _edtWord = (EditText) _v.findViewById(R.id.edv_search_word);
        _edtWord.clearFocus();
        _edtWord.addTextChangedListener(_listener);
	    _edtWord.setOnFocusChangeListener(_listener);
        _btnClear = (ImageButton) _v.findViewById(R.id.btn_search_clear);
        _btnClear.setOnClickListener(_listener);
        _btnClear.setVisibility(View.INVISIBLE);
        _listView = (ListView) _v.findViewById(R.id.list_of_words);
        _listView.setOnItemLongClickListener(_listener);
		_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    return _v;
    }

	@Override
	public void onResume() {
		super.onResume();
		if (isAdded()) {
			setWordText("");
			setListEntries(null);
		}
	}

    public static DictFragment newInstance(IWorkerJobInput worker) {
        DictFragment dictFragment = new DictFragment();
        dictFragment.attachWorker(worker);
        return dictFragment;
    }

	public void attachWorker(IWorkerJobInput worker) {
        _listener = new DictController(this, worker);
	}

	public void startActionMode(ActionMode.Callback callback) {
		if (this.isAdded()) {
			this.getActivity().startActionMode(callback);
		}
	}

	@Override
	public void startEditWordActivity(String word) {
		Intent intent = new Intent(this.getActivity(), EditWord.class);
		intent.putExtra("word", word);
		startActivity(intent);
		Log.d(LOG_TAG, "start info activity called");
	}

	@Override
	public void deleteWord(String word) {
		DBHelper dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
		dbHelper.deleteWord(StringUtils.stripFromArticle(this.getActivity(), word));
		Crouton crouton = Crouton.makeText(getActivity(), getString(R.string.crouton_word_deleted, word), Style.CONFIRM);
		crouton.setConfiguration(new Configuration.Builder().setDuration(1000).build());
		crouton.show();
	}

	@Override
	public void setWordClearButtonVisible(boolean state) {
		if (state) { _btnClear.setVisibility(View.VISIBLE); }
		else { _btnClear.setVisibility(View.INVISIBLE); }
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (null!=_v)
		{
			if (isVisibleToUser)
			{
				Utils.hideSoftKeyboard(this.getActivity());
			}
		}
	}

	@Override
	public void setWordText(String word) {
		if (_edtWord == null || word == null || _listener == null) { return; }
		_edtWord.setText(word);
		_edtWord.setSelection(word.length());
	}

	@Override
	public void setListEntries(List<Map<String, String>> words) {
		SimpleAdapter adapter;
		if (words==null)
		{
			((ListView) this.getView().findViewById(R.id.list_of_words))
					.setAdapter(null);
			return;
		}
		adapter = new SimpleAdapter(this.getActivity(), words,
				android.R.layout.simple_list_item_2,
				new String[]{"word", "translation"},
				new int[]{android.R.id.text1, android.R.id.text2});
		((ListView) this.getView().findViewById(R.id.list_of_words))
				.setAdapter(adapter);
	}
}