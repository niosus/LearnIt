package com.learnit.LearnIt.interfaces;

import android.view.ActionMode;

import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public interface IDictFragmentUpdate {
	public void setListEntries(List<Map<String,String>> words);
	public void startActionMode(ActionMode.Callback callback);
	public void startEditWordActivity(String word);
	public void deleteWord(String word);
	public void setWordClearButtonVisible(boolean state);
	public void setWordText(String word);
}
