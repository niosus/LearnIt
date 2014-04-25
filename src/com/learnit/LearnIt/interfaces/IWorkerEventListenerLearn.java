package com.learnit.LearnIt.interfaces;

import com.learnit.LearnIt.data_types.ArticleWordId;

import java.util.ArrayList;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListenerLearn extends IWorkerEventListener {
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds);
}
