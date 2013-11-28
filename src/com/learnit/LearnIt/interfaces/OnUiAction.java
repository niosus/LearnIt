package com.learnit.LearnIt.interfaces;

public interface OnUiAction
{
	public void onUiClick(int fragmentId, int viewId);
	public void onViewGotFocus(int fragmentId, int viewId);
	public void onTextChange(int fragmentId, int viewId, boolean isEmpty);
	public <T> void onListItemClick(int fragmentId, int viewId, T text);
	public void onListItemLongClick(int fragmentId, int viewId, String text);
	public void onMenuItemClick(int fragmentId, int viewId);
}