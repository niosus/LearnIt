package com.learnit.LearnIt.data_types;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.learnit.LearnIt.fragments.AddWordFragmentNew;

/**
 * Created by igor on 11/2/13.
 */
public class MyTextWatcher implements TextWatcher {
	AddWordFragmentNew.OnUiAction _callback;
	View _invoker;

	public MyTextWatcher(View invoker, AddWordFragmentNew.OnUiAction callback)
	{
		super();
		_invoker = invoker;
		_callback = callback;
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

	}

	@Override
	public void afterTextChanged(Editable editable) {
		_callback.onTextChange(_invoker.getId(), editable.toString().isEmpty());
	}
}
