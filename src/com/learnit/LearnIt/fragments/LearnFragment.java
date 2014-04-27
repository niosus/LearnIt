/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerLearn;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.MyAnimationHelper;
import com.learnit.LearnIt.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public abstract class LearnFragment
		extends MySmartFragment
		implements ILearnFragmentUpdate {
	public static final String TAG = "learn_words_frag";

    View v;
    String queryWord = null;
    protected int _direction = 0;
    final String LOG_TAG = "my_logs";
	protected IListenerLearn _listener;
	TextView _wordToAsk;

	protected abstract int[] btnIds();

	public LearnFragment() {
		super();
		setRetainInstance(true);
	}

    public void setAll(int visibilityState)
    {
	    for (int id: btnIds()) {
		    v.findViewById(id).setVisibility(visibilityState);
	    }
	    v.findViewById(R.id.word_to_ask).setVisibility(visibilityState);
    }

    @SuppressLint("NewApi")
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null!=v)
        {
            if (isVisibleToUser)
            {
                Utils.hideSoftKeyboard(this.getActivity());
            }
        }
    }

	@Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);

	@Override
	public void updateDirectionOfTranslation() {
		Random random = new Random();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String strDirection = sp.getString(getString(R.string.key_direction_of_trans), null);
		if (null != strDirection) {
			_direction = Integer.parseInt(strDirection);
			if (_direction == Constants.MIXED) {
				_direction = random.nextInt(2) + 1;
			}
		}
	}

	@Override
    public void closeWord() {
	    MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
	    TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
	    animationHelper.invokeForView(queryWordTextView, R.anim.close_word, _listener);
    }

	@Override
    public void openWord() {
	    MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
	    TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
	    animationHelper.invokeForView(queryWordTextView, R.anim.open_word, _listener);
    }

	@Override
	public void shakeView(View v) {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
		animationHelper.invokeForView(v, R.anim.shake, _listener);
	}

    public abstract void openButtons();
    public abstract void closeButtons();
	public abstract void setButtonTexts(ArrayList<ArticleWordId> words, int direction);
	public abstract void setQueryWordText(ArticleWordId struct, int direction);


	public void updateWordWeight(int numOfWrongAnswers) {
        Log.d(LOG_TAG, "word to be updated " + queryWord);
	    DBHelper dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
        switch (numOfWrongAnswers) {
            case 0:
                dbHelper.updateWordWeight(queryWord.toLowerCase(), DBHelper.WEIGHT_CORRECT);
                break;
            case 1:
                dbHelper.updateWordWeight(queryWord.toLowerCase(), DBHelper.WEIGHT_ONE_WRONG);
                break;
            case 2:
                dbHelper.updateWordWeight(queryWord.toLowerCase(), DBHelper.WEIGHT_TWO_WRONG);
                break;
            case 3:
                dbHelper.updateWordWeight(queryWord.toLowerCase(), DBHelper.WEIGHT_THREE_WRONG);
                break;
        }
    }
}