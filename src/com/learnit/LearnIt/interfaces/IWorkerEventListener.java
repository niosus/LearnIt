package com.learnit.LearnIt.interfaces;

import android.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListener {
	public void onPreExecute();
	public void onProgressUpdate(Integer... values);
	public void onSuccessWords(List<String> result);
	public void onSuccessTranslations(Pair<String, List<String>> result);
	public void onSuccessMyWords(List<Map<String, String>> result);
	public void onSuccessString(String result);
	public void onSuccessCode(Integer errorCode);
	public void onFail();
	public void noTaskSpecified();
	public void onTaskFinished();
	public boolean taskRunning();
}
