/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.app.DialogFragment;
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
import com.learnit.LearnIt.interfaces.IAddWordsFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerAddWords;
import com.learnit.LearnIt.listeners.AddWordsController;
import com.learnit.LearnIt.utils.StringUtils;

import java.util.List;

public class AddWordFragment extends MySmartFragment
		implements IAddWordsFragmentUpdate {
    protected static final String LOG_TAG = "my_logs";
	private EditText _word;
	private EditText _translation;
	private ImageButton _clearButtonWord, _clearButtonTrans;
	private MenuItem _saveMenuItem;
	protected IListenerAddWords _listener;

	private AddWordFragment(WorkerFragment worker) {
		super();
		_listener = new AddWordsController(this, worker);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	public static AddWordFragment newInstance(WorkerFragment worker) {
		AddWordFragment fragment = new AddWordFragment(worker);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actions_add_words, menu);
	    _saveMenuItem = menu.findItem(R.id.save_item);
		if (_listener != null && _saveMenuItem != null) {
			_saveMenuItem.setOnMenuItemClickListener(_listener);
		}
//		_saveMenuItem.setVisible(false);
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
		return StringUtils.stripFromArticle(this.getActivity(), _word.getText().toString());
	}

	@Override
	public String getTrans() {
		return StringUtils.stripFromArticle(this.getActivity(), _translation.getText().toString());
	}

	// ended implementing interface

	public void setMenuItemVisible(boolean visible)
	{
		try
		{
			_saveMenuItem.setVisible(visible);
		}
		catch (NullPointerException ex)
		{
			Log.e(LOG_TAG, ex.getMessage() + "in setMenuItemVisible");
		}
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
		DialogFragment frag = new MyDialogFragment();
		frag.show(getFragmentManager(), String.valueOf(exitCode));
	}
}