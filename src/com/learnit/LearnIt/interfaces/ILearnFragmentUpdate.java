package com.learnit.LearnIt.interfaces;

import android.view.View;

import com.learnit.LearnIt.data_types.ArticleWordId;

import java.util.ArrayList;

/**
 * Created by igor on 4/2/14.
 */
public interface ILearnFragmentUpdate {
	public void setQueryWordText(ArticleWordId struct);
	public void setButtonTexts(ArrayList<ArticleWordId> words);
	public void setAll(int visibilityState);
	public void openButtons();
	public void openWord();
	public void closeButtons();
	public void closeWord();
	public void shakeView(View v);
	public void updateWordWeight(int numOfWrongAnswers);
	public void updateDirectionOfTranslation();
}
