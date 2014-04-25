package com.learnit.LearnIt.controllers;

import android.content.Context;
import android.view.View;

import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerLearn;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerLearn;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.ArrayList;

/**
 * Created by igor on 4/2/14.
 */
public class LearnController implements
		IListenerLearn,
		IWorkerEventListenerLearn {
	ILearnFragmentUpdate _fragmentUpdate;
	IWorkerJobInput _worker;
	int _correctAnswerId;

	public LearnController(ILearnFragmentUpdate target, IWorkerJobInput worker) {
		_fragmentUpdate = target;
		_worker = worker;
	}

	//
	// implements listener to UI
	//
	@Override
	public void onClick(View v) {
		/*
		check the id of the view
		if the id is one of the button, check if it's correct
		if it is - show next word
		if not - show animation for the wrong guess
		 */
	}

	//
	// implements listener to AsyncTask worker
	//
	@Override
	public void onPreExecute() {
	}

	@Override
	public void onProgressUpdate(Integer... values) {
	}

	@Override
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds) {
//		_fragmentUpdate.setQueryWordText();
	}

	@Override
	public void onFail() {
		_worker.onTaskFinished();
	}

	@Override
	public void noTaskSpecified() {

	}

	@Override
	public void onTaskFinished() {

	}

	@Override
	public boolean taskRunning() {
		return _worker.taskRunning();
	}

	@Override
	public ArrayList<ArticleWordId> fetchRandomWords(
			Context context,
			int numOfWords,
			String ommitWord) {
		/*
		return null if no context

		fetch numOfWords random words by creating a task for that
		 */
		return null;

	}
}
