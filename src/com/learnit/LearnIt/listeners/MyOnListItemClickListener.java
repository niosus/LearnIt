package com.learnit.LearnIt.listeners;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.learnit.LearnIt.interfaces.OnUiAction;

import java.util.HashMap;
import java.util.Map;

public class MyOnListItemClickListener implements AdapterView.OnItemClickListener {
	OnUiAction _callback;

	public MyOnListItemClickListener(OnUiAction callback)
	{
		_callback = callback;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
		String queryWord;
		String queryTranlation;
		if (view instanceof TextView)
		{
			queryWord = ((TextView)view).getText().toString();
			_callback.onListItemClick(parent.getId(), queryWord);
		}
		if (parent.getAdapter().getItem(position) instanceof Map)
		{
			queryWord = ((HashMap<String,String>)parent.getAdapter().getItem(position)).get("word");
			queryTranlation = ((HashMap<String,String>)parent.getAdapter().getItem(position)).get("translation");
			_callback.onListItemClick(parent.getId(), new Pair<>(queryWord,queryTranlation));
		}

	}
}
