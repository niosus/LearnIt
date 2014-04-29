package com.learnit.LearnIt.interfaces;

import com.learnit.LearnIt.async_tasks.MySmartAsyncTask;

/**
 * Created by igor on 4/2/14.
 */
public interface IWorkerJobInput {
	void attach(IWorkerEventListener listener);
	void addTask(MySmartAsyncTask task, IWorkerEventListener listener);
	void onTaskFinished();
	boolean startNextTaskIfNeeded();
	boolean taskRunning();
	void cancelCurrentTask();
}
