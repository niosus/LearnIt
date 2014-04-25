package com.learnit.LearnIt.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListenerMyWords extends IWorkerEventListener {
	public void onSuccessMyWords(List<Map<String, String>> result);
}
