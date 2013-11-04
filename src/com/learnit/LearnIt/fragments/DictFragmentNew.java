/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.MyTextWatcher;
import com.learnit.LearnIt.interfaces.FragmentUiInterface;
import com.learnit.LearnIt.interfaces.OnUiAction;
import com.learnit.LearnIt.listeners.MyButtonOnClickListener;
import com.learnit.LearnIt.listeners.MyOnFocusChangeListener;
import com.learnit.LearnIt.listeners.MyOnListItemClickListener;
import com.learnit.LearnIt.listeners.MyOnListItemLongClickListener;
import com.learnit.LearnIt.utils.Utils;

import java.util.List;
import java.util.Map;

public class DictFragmentNew extends Fragment implements FragmentUiInterface{
    protected static final String LOG_TAG = "my_logs";
    private EditText _edtWord;
    private ImageButton _btnClear;
	private OnUiAction _callback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnUiAction)
		{
			_callback = (OnUiAction) activity;
		}
		else
		{
			throw new ClassCastException(activity.getClass().getName() + " should implement " + OnUiAction.class.getName());
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        Utils.updateCurrentDBName(this.getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    View _view = inflater.inflate(R.layout.dict_fragment, container, false);

        _edtWord = (EditText) _view.findViewById(R.id.edv_search_word);
        _edtWord.clearFocus();
        _edtWord.addTextChangedListener(new MyTextWatcher(_edtWord, _callback));
	    _edtWord.setOnFocusChangeListener(new MyOnFocusChangeListener(_callback));
        _btnClear = (ImageButton) _view.findViewById(R.id.btn_search_clear);
        _btnClear.setOnClickListener(new MyButtonOnClickListener(_callback));
        _btnClear.setVisibility(View.INVISIBLE);
        final ListView listView = (ListView) _view.findViewById(R.id.list_of_words);
        listView.setOnItemLongClickListener(new MyOnListItemLongClickListener(_callback));
        listView.setOnItemClickListener(new MyOnListItemClickListener(_callback));
	    return _view;
    }

	@Override
	public void setViewFocused(int id) {

	}

	@Override
	public void setViewText(int id, String text) {
		_edtWord.setText(text);
	}

	@Override
	public void addTextToView(int id, String text) {

	}

	@Override
	public <T> void setListEntries(List<T> words) {
		SimpleAdapter adapter;
		if (words==null)
		{
			((ListView) this.getView().findViewById(R.id.list_of_words))
					.setAdapter(null);
			return;
		}
		List<Map<String, String>> strings = (List<Map<String,String>>) words;
		adapter = new SimpleAdapter(this.getActivity(), strings,
				android.R.layout.simple_list_item_2,
				new String[]{"word", "translation"},
				new int[]{android.R.id.text1, android.R.id.text2});
		((ListView) this.getView().findViewById(R.id.list_of_words))
				.setAdapter(adapter);
	}

	@Override
	public void setViewVisibility(int id, int visibility) {
		_btnClear.setVisibility(visibility);
	}

	@Override
	public Integer getFocusedId() {
		return null;
	}

	@Override
	public String getTextFromView(int id) {
		return _edtWord.getText().toString();
	}
}