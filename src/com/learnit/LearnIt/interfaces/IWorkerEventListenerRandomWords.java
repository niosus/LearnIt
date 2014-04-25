package com.learnit.LearnIt.interfaces;

import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.utils.MyAnimationHelper;

import java.util.ArrayList;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListenerRandomWords extends
		IWorkerEventListener,
		MyAnimationHelper.OnAnimationActionListener {
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds);
}
