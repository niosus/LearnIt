package com.learnit.LearnIt.interfaces;

import android.content.Context;
import android.view.View;

import com.learnit.LearnIt.data_types.ArticleWordId;

import java.util.ArrayList;

/**
 * Created by igor on 4/2/14.
 */
public interface IListenerLearn extends
		View.OnClickListener {
	public ArrayList<ArticleWordId> fetchRandomWords(
			Context context,
			int numOfWords,
			String ommitWord);
}
