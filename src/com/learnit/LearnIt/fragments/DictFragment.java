/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.interfaces.IDictFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerDict;
import com.learnit.LearnIt.listeners.DictController;

import java.util.List;
import java.util.Map;

public class DictFragment extends MySmartFragment
		implements IDictFragmentUpdate{
    private EditText _edtWord;
    private ImageButton _btnClear;
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
        final ListView listView = (ListView) _v.findViewById(R.id.list_of_words);
//        listView.setOnItemLongClickListener(new MyOnListItemLongClickListener(_callback, this.getId()));
//        listView.setOnItemClickListener(new MyOnListItemClickListener(_callback, this.getId()));
	    return _v;
    }

	private DictFragment(WorkerFragment worker) {
		super();
		_listener = new DictController(this, worker);
	}

	public static DictFragment newInstance(WorkerFragment worker) {
		DictFragment fragment = new DictFragment(worker);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public void setViewText(int id, String text) {
		TextView edit = (TextView) _v.findViewById(id);
		edit.setText(text);
	}

	public void addTextToView(int id, String text) {

	}

	public void setViewVisibility(int id, int visibility) {
		_btnClear.setVisibility(visibility);
	}

	public Integer getFocusedId() {
		return null;
	}

	public String getTextFromView(int id) {
		return _edtWord.getText().toString();
	}

	@Override
	public void setQueryWordText(String word) {

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