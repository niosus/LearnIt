package com.learnit.LearnIt.interfaces;

public interface OnUiAction
{
	public void onUiClick(int id);
	public void onViewGotFocus(int id);
	public void onTextChange(int id, boolean isEmpty);
	public <T> void onListItemClick(int id, T text);
	public void onListItemLongClick(int id, String text);
	public void onMenuItemClick(int id);
}