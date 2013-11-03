package com.learnit.LearnIt.data_types;

import android.view.View;

import com.learnit.LearnIt.fragments.AddWordFragmentNew;

public class MyButtonOnClickListener implements View.OnClickListener {
	private AddWordFragmentNew.OnUiAction _callback;

	public MyButtonOnClickListener(AddWordFragmentNew.OnUiAction callback)
	{
		super();
		_callback=callback;
	}

	@Override
	public void onClick(View view) {
		_callback.onUiClick(view.getId());
	}
}
