package com.learnit.LearnIt.listeners;

import android.view.MenuItem;

import com.learnit.LearnIt.interfaces.OnUiAction;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
	OnUiAction _callback;

	public MyOnMenuItemClickListener(OnUiAction callback)
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
