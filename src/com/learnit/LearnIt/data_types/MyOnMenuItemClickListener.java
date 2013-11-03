package com.learnit.LearnIt.data_types;

import android.view.MenuItem;

import com.learnit.LearnIt.fragments.AddWordFragmentNew;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
	AddWordFragmentNew.OnUiAction _callback;

	public MyOnMenuItemClickListener(AddWordFragmentNew.OnUiAction callback)
	{
		super();
		_callback = callback;
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		_callback.onMenuItemClick(menuItem.getItemId());
		return false;
	}
}
