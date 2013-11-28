package com.learnit.LearnIt.listeners;

import android.view.View;
import android.widget.AdapterView;

import com.learnit.LearnIt.interfaces.OnUiAction;

import java.util.HashMap;

public class MyOnListItemLongClickListener implements AdapterView.OnItemLongClickListener {
	OnUiAction _callback;
	int _fragmentId;

	public MyOnListItemLongClickListener(OnUiAction callback, int fragmentId)
	{
		_callback = callback;
		_fragmentId = fragmentId;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
		String queryWord;
		queryWord = ((HashMap<String,String>)parent.getAdapter().getItem(i)).get("word");
		view.setSelected(true);
		_callback.onListItemLongClick(_fragmentId, parent.getId(), queryWord);
		return true;
	}
}
