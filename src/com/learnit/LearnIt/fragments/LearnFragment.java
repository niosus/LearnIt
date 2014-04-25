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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.controllers.RandomWordsController;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IListenerLearn;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.MyAnimationHelper;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;
import com.learnit.LearnIt.views.WordButton;

import java.util.ArrayList;
import java.util.Random;

public class LearnFragment
		extends MySmartFragment
		implements ILearnFragmentUpdate {

    View v;
    String queryWord = null;
    int direction = 0;
    final String LOG_TAG = "my_logs";
    DBHelper dbHelper;
	IListenerLearn _uiCallback;
    int[] btnIds = {R.id.left_top_button,
            R.id.right_top_button,
            R.id.left_bottom_button,
            R.id.right_bottom_button};
	TextView _wordToAsk;

	private LearnFragment(IWorkerJobInput worker) {
		super();
		_uiCallback = new RandomWordsController(this, worker);
		setRetainInstance(true);
	}

	public static LearnFragment newInstance(WorkerFragment worker) {
		LearnFragment fragment = new LearnFragment(worker);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

    public void setAll(int visibilityState)
    {
	    for (int id: btnIds) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.homework, container, false);
		updateDirectionOfTranslation();
	    _wordToAsk = (TextView) v.findViewById(R.id.word_to_ask);
		_wordToAsk.setMovementMethod(new ScrollingMovementMethod());
		(v.findViewById(R.id.left_top_button))
				.setOnClickListener(_uiCallback);
		(v.findViewById(R.id.right_bottom_button))
				.setOnClickListener(_uiCallback);
		(v.findViewById(R.id.left_bottom_button))
				.setOnClickListener(_uiCallback);
		(v.findViewById(R.id.right_top_button))
				.setOnClickListener(_uiCallback);
        return v;
    }

//	TODO: change the names of the strings to some constants !!! important
	boolean restoreFromPreferences() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String word = sp.getString("word", null);
		String button1Text = sp.getString("button1", null);
		String button2Text = sp.getString("button2", null);
		String button3Text = sp.getString("button3", null);
		String button4Text = sp.getString("button4", null);
		int correctId = sp.getInt("correctId", 0);
		if (word == null
				|| button1Text == null
				|| button2Text == null
				|| button3Text == null
				|| button4Text == null
				|| correctId == 0) { return false; }
		((TextView) v.findViewById(R.id.word_to_ask)).setText(word);
		((TextView) v.findViewById(R.id.left_top_button)).setText(button1Text);
		((TextView) v.findViewById(R.id.right_bottom_button)).setText(button2Text);
		((TextView) v.findViewById(R.id.left_bottom_button)).setText(button3Text);
		((TextView) v.findViewById(R.id.right_top_button)).setText(button4Text);
		_uiCallback.setCorrectWordIdFromPrefs(correctId);
		String[] split = word.split("\n");
		queryWord = split[split.length - 1];
		sp.edit().remove("word")
				.remove("button1")
				.remove("button2")
				.remove("button3")
				.remove("button4")
				.remove("correctId")
				.commit();
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!restoreFromPreferences()) {
			_uiCallback.fetchRandomWords(btnIds.length, null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		sp.edit().putString("word", _wordToAsk.getText().toString())
				.putString("button1", ((TextView) v.findViewById(R.id.left_top_button)).getText().toString())
				.putString("button2", ((TextView) v.findViewById(R.id.right_bottom_button)).getText().toString())
				.putString("button3", ((TextView) v.findViewById(R.id.left_bottom_button)).getText().toString())
				.putString("button4", ((TextView) v.findViewById(R.id.right_top_button)).getText().toString())
				.putInt("correctId", _uiCallback.getCorrectWordId())
				.commit();
	}

	@Override
	public void updateDirectionOfTranslation() {
		Random random = new Random();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String strDirection = sp.getString(getString(R.string.key_direction_of_trans), null);
		if (null != strDirection) {
			direction = Integer.parseInt(strDirection);
			if (direction == Constants.MIXED) {
				direction = random.nextInt(2) + 1;
			}
		}
	}

	@Override
    public void setQueryWordText(ArticleWordId struct) {
        TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
        queryWord = struct.word;
        switch (direction) {
            case Constants.FROM_FOREIGN_TO_MY:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                String learnLang = sp.getString(getString(R.string.key_language_from), "null");
                if (null != struct.article) {
	                // a small hack for German and capitalization of all nouns
                    if ("de".equals(learnLang)) {
                        queryWordTextView.setText(struct.article + "\n" + StringUtils.capitalize(queryWord));
                    } else {
                        queryWordTextView.setText(struct.article + "\n" + queryWord);
                    }
                } else if (null != struct.prefix) {
                    queryWordTextView.setText(struct.prefix + " " + queryWord);
                } else {
                    queryWordTextView.setText(queryWord);
                }
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                queryWordTextView.setText(StringUtils.splitOnRegex(struct.translation, ",|\\s"));
                break;
        }

    }

	@Override
    public void setButtonTexts(ArrayList<ArticleWordId> words) {
        int showOnButton;
        switch (direction) {
            case Constants.FROM_FOREIGN_TO_MY:
                showOnButton = WordButton.SHOW_TRANSLATION;
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                showOnButton = WordButton.SHOW_WORD;
                break;
            default:
                showOnButton = 0; //won't show anything on button
        }

		// actually set the texts to the buttons
        for (int i = 0; i < btnIds.length; ++i) {
            WordButton tempButton = (WordButton) v.findViewById(btnIds[i]);
            if (i < words.size() && showOnButton > 0) {
                tempButton.setEnabled(true);
                tempButton.setText(words.get(i), showOnButton);
            } else {
                tempButton.setEnabled(false);
                tempButton.setText("");
            }
        }
    }

    public void closeWord() {
	    MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
	    TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
	    animationHelper.invokeForView(queryWordTextView, R.anim.close_word, _uiCallback);
    }

    public void openWord() {
	    MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
	    TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
	    animationHelper.invokeForView(queryWordTextView, R.anim.open_word, _uiCallback);
    }

	public void shakeView(View v) {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
		animationHelper.invokeForView(v, R.anim.shake, _uiCallback);
	}

    public void openButtons() {
	    MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
	    View[] viewsBottom = {
			    v.findViewById(R.id.second_button_layout),
			    v.findViewById(R.id.right_bottom_button),
			    v.findViewById(R.id.left_bottom_button)};
	    View[] viewsTop = {
			    v.findViewById(R.id.first_button_layout),
			    v.findViewById(R.id.left_top_button),
			    v.findViewById(R.id.right_top_button)};
	    animationHelper.invokeForAllViews(viewsBottom, R.anim.float_in_up_first_row, _uiCallback);
	    animationHelper.invokeForAllViews(viewsTop, R.anim.float_in_up_second_row, _uiCallback);
    }

    public void closeButtons() {
	    MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
	    View[] viewsBottom = {
			    v.findViewById(R.id.second_button_layout),
			    v.findViewById(R.id.right_bottom_button),
			    v.findViewById(R.id.left_bottom_button)};
	    View[] viewsTop = {
			    v.findViewById(R.id.first_button_layout),
			    v.findViewById(R.id.left_top_button),
			    v.findViewById(R.id.right_top_button)};
	    animationHelper.invokeForAllViews(viewsBottom, R.anim.float_away_down_first_row, _uiCallback);
	    animationHelper.invokeForAllViews(viewsTop, R.anim.float_away_down_second_row, _uiCallback);
    }

    public void updateWordWeight(int numOfWrongAnswers) {
        Log.d(LOG_TAG, "word to be updated " + queryWord);
	    dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
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