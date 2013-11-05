package com.learnit.LearnIt.interfaces;

import java.util.List;

/**
 * Created by igor on 11/3/13.
 */
public interface FragmentUiInterface<T> {
	public void setViewFocused(int id);
	public void setViewText(int id, String text);
	public void addTextToView(int id, String text);
	public void setListEntries(List<T> words);
	public void setViewVisibility(int id, int visibility);
	public Integer getFocusedId();
	public String getTextFromView(int id);
}
