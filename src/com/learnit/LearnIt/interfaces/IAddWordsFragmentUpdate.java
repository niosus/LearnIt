package com.learnit.LearnIt.interfaces;

import java.util.List;

/**
 * Created by igor on 4/2/14.
 */
public interface IAddWordsFragmentUpdate {
	public void setWordText(String word);
	public void setTranslationText(String translation);
	public void appendWordText(String word);
	public void appendTranslationText(String translation);
	public void setListEntries(List<String> words);
	public void setWordClearButtonVisible(boolean state);
	public void setTranslationClearButtonVisible(boolean state);
	public void setMenuItemVisible(boolean visible);
	public void setViewFocused(int id);
	public void addArticle(String article);
	public String getWord();
	public String getTrans();
	public void toInitialState();

	public void showMessage(int exitCode);
}
