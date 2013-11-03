/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.learnit.LearnIt.data_types.MyButtonOnClickListener;
import com.learnit.LearnIt.data_types.MyOnFocusChangeListener;
import com.learnit.LearnIt.data_types.MyOnItemClickListener;
import com.learnit.LearnIt.data_types.MyOnMenuItemClickListener;
import com.learnit.LearnIt.data_types.MyTextWatcher;

import java.util.List;

public class AddWordFragmentNew extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    private View _view;
	private EditText _word;
	private EditText _translation;
	private ListView _list;
	private MenuItem _saveMenuItem;

	private OnUiAction _callback;

	public interface OnUiAction
	{
		public void onUiClick(int id);
		public void onUiGotFocus(int id);
		public void onTextChange(int id, boolean isEmpty);
		public void onListItemClick(int id, String text);
		public void onMenuItemClick(int id);
	}

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actions_add_words, menu);
	    _saveMenuItem = menu.findItem(R.id.save_item);
	    _saveMenuItem.setOnMenuItemClickListener(new MyOnMenuItemClickListener(_callback));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    _view = inflater.inflate(R.layout.add_word_fragment, container, false);
	    if (_view == null)
		    return null;
	    MyButtonOnClickListener myOnClickListener = new MyButtonOnClickListener(_callback);
		ImageButton clearButtonWord = (ImageButton) _view.findViewById(R.id.btn_add_word_clear);
		clearButtonWord.setOnClickListener(myOnClickListener);
	    ImageButton clearButtonTrans = (ImageButton) _view.findViewById(R.id.btn_add_trans_clear);
		clearButtonTrans.setOnClickListener(myOnClickListener);

	    MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener(_callback);
	    _word = (EditText) _view.findViewById(R.id.edv_add_word);
	    _translation = (EditText) _view.findViewById(R.id.edv_add_translation);
	    _word.setOnFocusChangeListener(myOnFocusChangeListener);
	    _translation.setOnFocusChangeListener(myOnFocusChangeListener);
	    MyTextWatcher textWatcherWord = new MyTextWatcher(_word, _callback);
	    MyTextWatcher textWatcherTrans = new MyTextWatcher(_translation, _callback);
	    _word.addTextChangedListener(textWatcherWord);
	    _translation.addTextChangedListener(textWatcherTrans);

	    MyOnItemClickListener myOnItemClickListener = new MyOnItemClickListener(_callback);
	    _list = (ListView) _view.findViewById(R.id.list_of_add_words);
	    _list.setOnItemClickListener(myOnItemClickListener);

        return _view;
    }

	public boolean isWordFocused()
	{
		return _word.isFocused();
	}

	public boolean isTranslationFocused()
	{
		return _translation.isFocused();
	}

	public void setWordFocused()
	{
		_word.requestFocus();
	}

	public void setTranslationFocused()
	{
		_translation.requestFocus();
	}

	public void clearWord()
	{
		_word.setText("");
	}

	public void clearTranslation()
	{
		_translation.setText("");
	}

	public boolean isWordEmpty()
	{
		return _word.getText().toString().isEmpty();
	}

	public boolean isTransEmpty()
	{
		return _translation.getText().toString().isEmpty();
	}

	public String getWord()
	{
		return _word.getText().toString();
	}

	public String getTranslation()
	{
		return _translation.getText().toString();
	}

	public void updateList(List<String> words)
	{
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
}