package com.learnit.LearnIt.listeners;

import android.view.MenuItem;

import com.learnit.LearnIt.interfaces.OnUiAction;

/**
 * Created by igor on 11/2/13.
 */
public class MyOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
	OnUiAction _callback;
	int _fragmentId;

	public MyOnMenuItemClickListener(OnUiAction callback, int fragmentId)
	{
		super();
		_callback = callback;
		_fragmentId = fragmentId;
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		_callback.onMenuItemClick(_fragmentId, menuItem.getItemId());
		return false;
	}
}
