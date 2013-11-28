package com.learnit.LearnIt.listeners;

import android.view.View;

import com.learnit.LearnIt.interfaces.OnUiAction;

public class MyButtonOnClickListener implements View.OnClickListener {
	private OnUiAction _callback;
	private int _fragmentId;

	public MyButtonOnClickListener(OnUiAction callback, int fragmentId)
	{
		super();
		_callback=callback;
		_fragmentId = fragmentId;
	}

	@Override
	public void onClick(View view) {
		_callback.onUiClick(_fragmentId, view.getId());
	}
}
