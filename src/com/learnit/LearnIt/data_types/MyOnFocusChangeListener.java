package com.learnit.LearnIt.data_types;

import android.view.View;

import com.learnit.LearnIt.fragments.AddWordFragmentNew;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnFocusChangeListener implements View.OnFocusChangeListener{
	private AddWordFragmentNew.OnUiAction _callback;

	public MyOnFocusChangeListener(AddWordFragmentNew.OnUiAction callback)
	{
		super();
		_callback = callback;
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (hasFocus)
			_callback.onUiGotFocus(view.getId());
	}
}
