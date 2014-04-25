package com.learnit.LearnIt.interfaces;

import android.util.Pair;

import java.util.List;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListenerTranslations extends IWorkerEventListener {
	public void onSuccessTranslations(Pair<String, List<String>> result);
}
