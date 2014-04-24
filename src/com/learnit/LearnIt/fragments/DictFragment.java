/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DictFragment extends MySmartFragment
		implements IDictFragmentUpdate{
    private EditText _edtWord;
    private ImageButton _btnClear;
	private ListView _listView;
	protected IListenerDict _listener;

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
		setWordText("");
		setListEntries(null);
	}

	private DictFragment(IWorkerJobInput worker) {
		super();
		_listener = new DictController(this, worker);
	}

	public static DictFragment newInstance(WorkerFragment worker) {
		DictFragment fragment = new DictFragment(worker);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
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
		Crouton.makeText(getActivity(),  "DUMMY " + word + " DELETED", Style.CONFIRM).show();
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