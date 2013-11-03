package com.learnit.LearnIt.listeners;

import android.view.View;
import android.widget.AdapterView;

import com.learnit.LearnIt.interfaces.OnUiAction;

import java.util.HashMap;

public class MyOnListItemLongClickListener implements AdapterView.OnItemLongClickListener {
	OnUiAction _callback;

	public MyOnListItemLongClickListener(OnUiAction callback)
	{
		_callback = callback;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
		String queryWord;
		queryWord = ((HashMap<String,String>)parent.getAdapter().getItem(i)).get("word");
		_callback.onListItemLongClick(parent.getId(), queryWord);
		return true;
	}
}
