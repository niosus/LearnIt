package com.learnit.LearnIt.controllers;

import android.view.View;
import android.widget.Button;

import com.learnit.LearnIt.R;
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
public class RandomWordsController implements
		IListenerLearn,
		IWorkerEventListenerRandomWords {
	ILearnFragmentUpdate _fragmentUpdate;
	IWorkerJobInput _worker;
	int[] btnIds = {R.id.left_top_button,
			R.id.right_top_button,
			R.id.left_bottom_button,
			R.id.right_bottom_button};
	int _correctAnswerId;
	int _numOfWrongAnswers;

	public RandomWordsController(ILearnFragmentUpdate target, IWorkerJobInput worker) {
		_fragmentUpdate = target;
		_worker = worker;
		_numOfWrongAnswers = 0;
	}

	//
	// implements listener to UI
	//
	@Override
	public void onClick(View v) {
		if (v.getId() == btnIds[_correctAnswerId]) {
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
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds) {
		_worker.onTaskFinished();
		Random rand = new Random();
		_fragmentUpdate.updateDirectionOfTranslation();
		_correctAnswerId = rand.nextInt(articleWordIds.size());
		_fragmentUpdate.setQueryWordText(
				articleWordIds.get(_correctAnswerId));
		_fragmentUpdate.setButtonTexts(articleWordIds);
		_fragmentUpdate.openButtons();
		_fragmentUpdate.openWord();
		_fragmentUpdate.setAll(View.VISIBLE);
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
	public void fetchRandomWords(
			int numOfWords, String omitWord) {
//		start fetching numOfWords random words by creating a task for that
//		result comes as a callback from worker listener
		Random random = new Random();
		int nouns = random.nextInt(2) + 1;
		_worker.addTask(new GetRandomWordsTask(omitWord, numOfWords, nouns), this);
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
	public void onAnimationFinished(int id, boolean ignore) {
		if (ignore)
			return;
		switch (id)
		{
			case (R.anim.float_away_down_second_row):
				_fragmentUpdate.setAll(View.INVISIBLE);
				break;
			case (R.anim.close_word):
				_fragmentUpdate.setAll(View.INVISIBLE);
				fetchRandomWords(btnIds.length, null);
				break;
		}
	}
}
