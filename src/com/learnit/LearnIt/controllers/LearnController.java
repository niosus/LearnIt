package com.learnit.LearnIt.controllers;

import android.view.View;
import android.widget.Button;

import com.learnit.LearnIt.async_tasks.GetRandomWordsTask;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerLearn;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerRandomWords;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by igor on 4/2/14.
 */
public abstract class LearnController implements
		IListenerLearn,
		IWorkerEventListenerRandomWords {
	ILearnFragmentUpdate _fragmentUpdate;
	IWorkerJobInput _worker;
	int[] _btnIds;
	int _correctAnswerId;
	int _numOfWrongAnswers;

	public LearnController(ILearnFragmentUpdate target, IWorkerJobInput worker, int[] btnIds) {
		_fragmentUpdate = target;
		_worker = worker;
		_numOfWrongAnswers = 0;
		_btnIds = btnIds;
	}

	//
	// implements listener to UI
	//
	@Override
	public void onClick(View v) {
		if (v.getId() == _btnIds[_correctAnswerId]) {
			_fragmentUpdate.closeWord();
			_fragmentUpdate.closeButtons();
			_fragmentUpdate.updateWordWeight(_numOfWrongAnswers);
			_numOfWrongAnswers = 0;
		} else {
			if (v instanceof Button) {
				_numOfWrongAnswers++;
				_fragmentUpdate.shakeView(v);
			}
		}
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
	public abstract void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds);

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
	public void fetchRandomWords(int numOfWords, String omitWord) {
		// start fetching numOfWords random words by creating a task for that
		// result comes as a callback from worker listener
		Random random = new Random();
		int nouns = random.nextInt(2) + 1;
		_worker.addTask(new GetRandomWordsTask(omitWord, numOfWords, nouns), this);
	}

	@Override
	public boolean taskRunning() {
		return _worker.taskRunning();
	}

	@Override
	public void setCorrectWordIdFromPrefs(int num) {
		_correctAnswerId = num;
	}

	@Override
	public int getCorrectWordId() {
		return _correctAnswerId;
	}

	@Override
	public abstract void onAnimationFinished(int id, boolean ignore);
}
