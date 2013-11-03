package com.learnit.LearnIt.data_types;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.learnit.LearnIt.fragments.AddWordFragmentNew;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnItemClickListener implements AdapterView.OnItemClickListener {
	AddWordFragmentNew.OnUiAction _callback;

	public MyOnItemClickListener(AddWordFragmentNew.OnUiAction callback)
	{
		_callback = callback;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
		String queryWord = ((TextView) view).getText().toString();
		_callback.onListItemClick(parent.getId(), queryWord);
	}
}
