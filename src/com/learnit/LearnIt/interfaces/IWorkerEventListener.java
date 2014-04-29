package com.learnit.LearnIt.interfaces;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerEventListener {
	public void onPreExecute();
	public void onProgressUpdate(Double... values);
	public void onFail();
	public void noTaskSpecified();
	public void onTaskFinished();
	public boolean taskRunning();
}
