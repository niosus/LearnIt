package com.learnit.LearnIt.listeners;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.learnit.LearnIt.interfaces.OnUiAction;

import java.util.HashMap;
import java.util.Map;

public class MyOnListItemClickListener implements AdapterView.OnItemClickListener {
	private OnUiAction _callback;
	private int _fragmentId;

	public MyOnListItemClickListener(OnUiAction callback, int fragmentId)
	{
		_callback = callback;
		_fragmentId = fragmentId;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
		String queryWord;
		String queryTranlation;
		if (view instanceof TextView)
		{
			queryWord = ((TextView)view).getText().toString();
			_callback.onListItemClick(_fragmentId, parent.getId(), queryWord);
		}
		if (parent.getAdapter().getItem(position) instanceof Map)
		{
			queryWord = ((HashMap<String,String>)parent.getAdapter().getItem(position)).get("word");
			queryTranlation = ((HashMap<String,String>)parent.getAdapter().getItem(position)).get("translation");
			_callback.onListItemClick(_fragmentId, parent.getId(), new Pair<>(queryWord,queryTranlation));
		}

	}
}
