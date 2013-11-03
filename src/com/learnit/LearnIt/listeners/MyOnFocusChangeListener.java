package com.learnit.LearnIt.listeners;

import android.view.View;

import com.learnit.LearnIt.interfaces.OnUiAction;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnFocusChangeListener implements View.OnFocusChangeListener{
	private OnUiAction _callback;

	public MyOnFocusChangeListener(OnUiAction callback)
	{
		super();
		_callback = callback;
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (hasFocus)
			_callback.onViewGotFocus(view.getId());
	}
}
