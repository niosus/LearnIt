package com.learnit.LearnIt.data_types;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.learnit.LearnIt.interfaces.OnUiAction;

/**
 * Created by igor on 11/2/13.
 */
public class MyTextWatcher implements TextWatcher {
	OnUiAction _callback;
	View _invoker;
	int _fragmentId;

	public MyTextWatcher(View invoker, OnUiAction callback, int fragmentId)
	{
		super();
		_invoker = invoker;
		_callback = callback;
		_fragmentId = fragmentId;
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

	}

	@Override
	public void afterTextChanged(Editable editable) {
		_callback.onTextChange(_fragmentId, _invoker.getId(), editable.toString().isEmpty());
		Log.d("my_logs", "text changed, invoker = " + _fragmentId);
	}
}
