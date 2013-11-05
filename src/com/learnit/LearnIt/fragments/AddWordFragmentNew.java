/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.MyTextWatcher;
import com.learnit.LearnIt.interfaces.FragmentUiInterface;
import com.learnit.LearnIt.listeners.MyButtonOnClickListener;
import com.learnit.LearnIt.listeners.MyOnFocusChangeListener;
import com.learnit.LearnIt.listeners.MyOnListItemClickListener;
import com.learnit.LearnIt.listeners.MyOnMenuItemClickListener;

import java.util.List;

public class AddWordFragmentNew extends MySmartFragment implements FragmentUiInterface<String> {
    protected static final String LOG_TAG = "my_logs";
	private EditText _word;
	private EditText _translation;
	private ImageButton _clearButtonWord, _clearButtonTrans;
	private MenuItem _saveMenuItem;

	public AddWordFragmentNew(int tag) {
		super(tag);
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
	    _saveMenuItem.setOnMenuItemClickListener(new MyOnMenuItemClickListener(_callback));
		_saveMenuItem.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    View _view = inflater.inflate(R.layout.add_word_fragment, container, false);
	    if (_view == null)
		    return null;
	    View.OnClickListener myOnClickListener = new MyButtonOnClickListener(_callback);
		_clearButtonWord = (ImageButton) _view.findViewById(R.id.btn_add_word_clear);
		_clearButtonWord.setOnClickListener(myOnClickListener);
	    _clearButtonTrans = (ImageButton) _view.findViewById(R.id.btn_add_trans_clear);
		_clearButtonTrans.setOnClickListener(myOnClickListener);
	    _clearButtonWord.setVisibility(View.INVISIBLE);
	    _clearButtonTrans.setVisibility(View.INVISIBLE);

	    View.OnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener(_callback);
	    _word = (EditText) _view.findViewById(R.id.edv_add_word);
	    _translation = (EditText) _view.findViewById(R.id.edv_add_translation);
	    _word.setOnFocusChangeListener(myOnFocusChangeListener);
	    _translation.setOnFocusChangeListener(myOnFocusChangeListener);
	    TextWatcher textWatcherWord = new MyTextWatcher(_word, _callback);
	    TextWatcher textWatcherTrans = new MyTextWatcher(_translation, _callback);
	    _word.addTextChangedListener(textWatcherWord);
	    _translation.addTextChangedListener(textWatcherTrans);

	    AdapterView.OnItemClickListener myOnListItemClickListener = new MyOnListItemClickListener(_callback);
	    ListView _list = (ListView) _view.findViewById(R.id.list_of_add_words);
	    _list.setOnItemClickListener(myOnListItemClickListener);

        return _view;
    }

	public void setViewFocused(int id)
	{
		if (id == _word.getId())
			_word.requestFocus();
		if (id == _translation.getId())
			_translation.requestFocus();
	}

	@Override
	public void setViewText(int id, String text) {
		if (id == _word.getId())
			_word.setText(text);
		if (id == _translation.getId())
			_translation.setText(text);
	}

	private void addTextToEditText(EditText editText, String text)
	{
		if (editText.getText() == null
				|| editText.getText().toString().isEmpty())
		{
			editText.setText(text);
			editText.setSelection(editText.length());
			return;
		}
		if (editText.getText().toString().contains(text))
			return;
		editText.append(", " + text);
		editText.setSelection(editText.length());
	}

	@Override
	public void addTextToView(int id, String text) {
		if (id == _word.getId())
		{
			addTextToEditText(_word, text);
		}
		if (id == _translation.getId())
		{
			addTextToEditText(_translation, text);
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
	public void setViewVisibility(int id, int visibility) {
		try
		{
			if (id == _clearButtonWord.getId()) {
				_clearButtonWord.setVisibility(visibility);
				return;
			}
			if (id == _clearButtonTrans.getId()) {
				_clearButtonTrans.setVisibility(visibility);
			}
		}
		catch (NullPointerException ex)
		{
			Log.e(LOG_TAG, ex.getMessage() + "in setViewVisibility");
		}
	}

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

	@Override
	public String getTextFromView(int id)
	{
		try
		{
			if (id == _word.getId())
				return _word.getText().toString();
			if (id == _translation.getId())
				return _translation.getText().toString();
			else return null;
		}
		catch (NullPointerException ex)
		{
			Log.e(LOG_TAG, ex.getMessage() + "in getTextFromView");
			return null;
		}
	}

	@Override
	public Integer getFocusedId() {
		try
		{
			if (_word.isFocused())
				return _word.getId();
			if (_translation.isFocused())
				return _translation.getId();
			return null;
		}
		catch (NullPointerException ex)
		{
			Log.e(LOG_TAG, ex.getMessage() + "in getFocusedId");
			return null;
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
}