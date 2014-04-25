package com.learnit.LearnIt.interfaces;

import java.util.List;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListenerHelpWords extends IWorkerEventListener {
	public void onSuccessWords(List<String> result);
}
