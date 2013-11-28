package com.learnit.LearnIt.listeners;

import android.view.View;

import com.learnit.LearnIt.interfaces.OnUiAction;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnFocusChangeListener implements View.OnFocusChangeListener{
	private OnUiAction _callback;
	private int _fragmentId;

	public MyOnFocusChangeListener(OnUiAction callback, int fragmentId)
	{
		super();
		_callback = callback;
		_fragmentId = fragmentId;
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (hasFocus)
			_callback.onViewGotFocus(_fragmentId, view.getId());
	}
}
