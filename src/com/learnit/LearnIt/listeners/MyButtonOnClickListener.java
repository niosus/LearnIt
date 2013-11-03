package com.learnit.LearnIt.listeners;

import android.view.View;

import com.learnit.LearnIt.interfaces.OnUiAction;

public class MyButtonOnClickListener implements View.OnClickListener {
	private OnUiAction _callback;

	public MyButtonOnClickListener(OnUiAction callback)
	{
		super();
		_callback=callback;
	}

	@Override
	public void onClick(View view) {
		_callback.onUiClick(view.getId());
	}
}
