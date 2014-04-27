package com.learnit.LearnIt.interfaces;

import android.view.View;

/**
 * Created by igor on 4/2/14.
 */
public interface IListenerLearn extends
		View.OnClickListener {
	public void fetchRandomWords(
			int numOfWords,
			String omitWord);
	public void setCorrectWordIdFromPrefs(int num);
	public int getCorrectWordId();
	public void showNext();
}
