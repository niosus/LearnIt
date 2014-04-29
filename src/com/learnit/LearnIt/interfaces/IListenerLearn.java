package com.learnit.LearnIt.interfaces;

import android.view.View;

import com.learnit.LearnIt.data_types.ArticleWordId;

/**
 * Created by igor on 4/2/14.
 */
public interface IListenerLearn extends
		View.OnClickListener {
	public void fetchRandomWords(int numOfWords, ArticleWordId omitWord);
	public void setCorrectWordIdFromPrefs(int num);
	public int getCorrectWordId();
	public void showNext();
}
