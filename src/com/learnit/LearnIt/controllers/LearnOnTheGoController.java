package com.learnit.LearnIt.controllers;

import android.view.View;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by igor on 4/2/14.
 */
public class LearnOnTheGoController extends LearnController {

	public LearnOnTheGoController(ILearnFragmentUpdate target, IWorkerJobInput worker, int[] btnIds) {
		super(target, worker, btnIds);
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
				fetchRandomWords(_btnIds.length, null);
				break;
		}
	}
}
