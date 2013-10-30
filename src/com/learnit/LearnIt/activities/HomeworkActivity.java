/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.NotificationBuilder;
import com.learnit.LearnIt.fragments.MyDialogFragment;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.MyAnimationHelper;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;
import com.learnit.LearnIt.views.WordButton;

import java.util.ArrayList;
import java.util.Random;

public class HomeworkActivity extends FragmentActivity implements MyAnimationHelper.OnAnimationActionListener {
	private static final String ALIVE_IDS_TAG = "current_ids";
    int fromLearnToKnow = 0;
    int numOfWrongAnswers = 0;
	SharedPreferences _sp;
	MyButtonOnClick _myBtnOnClick;
	DBHelper dbHelper;
    ArticleWordId correctEntry = null;
    final String LOG_TAG = "my_logs";
    int[] btnIds = {R.id.left_top_button,
            R.id.right_top_button,
            R.id.left_bottom_button,
            R.id.right_bottom_button};
	private ArrayList<Integer> _ids;
	private ArrayList<String> _words;
	private ArrayList<String> _translations;
	private ArrayList<Integer> _directionsOfTrans;
	private ArrayList<String> _articles;
	private ArrayList<String> _prefixes;
	private int _currentNotificationIndex;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homework);
		getEverythingFromIntent();
	}

	private void setAllTexts()
	{
		Utils.updateCurrentDBName(this);
		dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
		Random random = new Random();
		int randIdx = random.nextInt(btnIds.length);
		_myBtnOnClick.correct = btnIds[randIdx];
		TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
		setBtnTexts(randIdx);
		switch (fromLearnToKnow) {
			case Constants.FROM_FOREIGN_TO_MY:
				_sp = PreferenceManager.getDefaultSharedPreferences(this);
				String learnLang = _sp.getString(getString(R.string.key_language_from), "null");
				if (null != correctEntry.article) {
					if ("de".equals(learnLang)) {
						queryWordTextView.setText(correctEntry.article + " " + StringUtils.capitalize(correctEntry.word));
					} else {
						queryWordTextView.setText(correctEntry.article + " " + correctEntry.word);
					}
				} else if (null != correctEntry.prefix) {
					queryWordTextView.setText(correctEntry.prefix + " " + correctEntry.word);
				} else {
					queryWordTextView.setText(correctEntry.word);
				}
				break;
			case Constants.FROM_MY_TO_FOREIGN:
				queryWordTextView.setText(correctEntry.translation);
				break;
		}
	}

	protected void onResume() {
		super.onResume();
		_myBtnOnClick = new MyButtonOnClick();
		(findViewById(R.id.left_top_button))
				.setOnClickListener(_myBtnOnClick);
		(findViewById(R.id.right_bottom_button))
				.setOnClickListener(_myBtnOnClick);
		(findViewById(R.id.left_bottom_button))
				.setOnClickListener(_myBtnOnClick);
		(findViewById(R.id.right_top_button))
				.setOnClickListener(_myBtnOnClick);
		findNextId();
		setAllTexts();
	}

	private boolean findNextId()
	{
		numOfWrongAnswers=0;
		_sp = PreferenceManager.getDefaultSharedPreferences(this);
		String idsOld = _sp.getString(ALIVE_IDS_TAG, "");
		int counter=0;
		while (!idsOld.contains(_ids.get(_currentNotificationIndex).toString()))
		{
			_currentNotificationIndex++;
			_currentNotificationIndex%=_ids.size();
			if (counter++==_ids.size())
				return false;
		}
		correctEntry = new ArticleWordId(
				_articles.get(_currentNotificationIndex),
				_prefixes.get(_currentNotificationIndex),
				_words.get(_currentNotificationIndex),
				_translations.get(_currentNotificationIndex),
				_ids.get(_currentNotificationIndex)
		);
		fromLearnToKnow = _directionsOfTrans.get(_currentNotificationIndex);
		Log.d(LOG_TAG, "got intent word=" + correctEntry.word + " id = "
				+ correctEntry.id);
		return true;
	}

    private void getEverythingFromIntent() {
        Intent intent = getIntent();
	    _ids = intent.getIntegerArrayListExtra(NotificationBuilder.IDS_TAG);
	    _words = intent.getStringArrayListExtra(NotificationBuilder.WORDS_TAG);
	    _translations = intent.getStringArrayListExtra(NotificationBuilder.TRANSLATIONS_TAG);
	    _directionsOfTrans = intent.getIntegerArrayListExtra(NotificationBuilder.DIRECTIONS_OF_TRANS_TAG);
	    _articles = intent.getStringArrayListExtra(NotificationBuilder.ARTICLES_TAG);
	    _prefixes = intent.getStringArrayListExtra(NotificationBuilder.PREFIXES_TAG);
	    _currentNotificationIndex = intent.getIntExtra(NotificationBuilder.CURRENT_NOTIFICATION_INDEX, -1);
    }

    private void setBtnTexts(int correctId) {
	    int isNoun;
        if (null == correctEntry.article) {
            isNoun = Constants.NOT_NOUNS;
        } else {
            isNoun = Constants.ONLY_NOUNS;
        }
        ArrayList<ArticleWordId> randomWords = dbHelper.getRandomWords(btnIds.length, correctEntry.word, isNoun);
        Log.d(Constants.LOG_TAG, "number of words for buttons = " + randomWords.size());
        int showOnButton;
        switch (fromLearnToKnow)
        {
            case Constants.FROM_FOREIGN_TO_MY:
                showOnButton = WordButton.SHOW_TRANSLATION;
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                showOnButton = WordButton.SHOW_WORD;
                break;
            default:
                showOnButton = 0; //won't show anything on button
        }
        for (int i = 0; i < 4; ++i) {
            WordButton tempButton = (WordButton) findViewById(btnIds[i]);
            if (correctId == i) {
                tempButton.setText(correctEntry, showOnButton);
            } else if (i < randomWords.size()) {
                tempButton.setEnabled(true);
                tempButton.setText(randomWords.get(i), showOnButton);
            } else {
                tempButton.setEnabled(false);
                tempButton.setText("");
            }
        }

    }

    protected void stopActivity() {
        finish();
    }

    private void updateWordWeight() {
        Log.d(LOG_TAG, "word to be updated " + correctEntry.word);
        switch (numOfWrongAnswers) {
            case 0:
                dbHelper.updateWordWeight(correctEntry.word.toLowerCase(), DBHelper.WEIGHT_CORRECT);
                break;
            case 1:
                dbHelper.updateWordWeight(correctEntry.word.toLowerCase(), DBHelper.WEIGHT_ONE_WRONG);
                break;
            case 2:
                dbHelper.updateWordWeight(correctEntry.word.toLowerCase(), DBHelper.WEIGHT_TWO_WRONG);
                break;
            case 3:
                dbHelper.updateWordWeight(correctEntry.word.toLowerCase(), DBHelper.WEIGHT_THREE_WRONG);
                break;
        }
    }

	private void setAll(int visibilityState)
	{
		findViewById(R.id.left_top_button).setVisibility(visibilityState);
		findViewById(R.id.right_bottom_button).setVisibility(visibilityState);
		findViewById(R.id.left_bottom_button).setVisibility(visibilityState);
		findViewById(R.id.right_top_button).setVisibility(visibilityState);
//        v.findViewById(R.id.word_to_ask).setVisibility(visibilityState);
	}

	protected void updateListOfAliveIds()
	{
		_sp = PreferenceManager.getDefaultSharedPreferences(this);
		Log.d(LOG_TAG, _sp.toString());
		SharedPreferences.Editor editor = _sp.edit();
		String idsOld = _sp.getString(ALIVE_IDS_TAG, "");
		Log.d(LOG_TAG, idsOld);
		String idsNew="";
		for (Integer idInt: _ids)
		{
			if (idInt==correctEntry.id)
				continue;
			if (!idsOld.contains(idInt.toString()))
				continue;
			idsNew+=idInt.toString()+" ";
		}
		editor.putString(ALIVE_IDS_TAG, idsNew);
		Log.d(LOG_TAG, idsNew);
		editor.commit();
	}

	private void closeWord() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this);
		TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
		animationHelper.invokeForView(queryWordTextView, R.anim.close_word, this);
	}

	private void openWord() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this);
		TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
		animationHelper.invokeForView(queryWordTextView, R.anim.open_word, this);
	}

	private void openButtons() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this);
		View[] views = {findViewById(R.id.left_top_button),
				findViewById(R.id.right_bottom_button),
				findViewById(R.id.left_bottom_button),
				findViewById(R.id.right_top_button)};
		animationHelper.invokeForAllViews(views, R.anim.open_fade_in, this);
	}

	private void closeButtons() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this);
		View[] views = {findViewById(R.id.left_top_button),
				findViewById(R.id.right_bottom_button),
				findViewById(R.id.left_bottom_button),
				findViewById(R.id.right_top_button)};
		animationHelper.invokeForAllViews(views, R.anim.close_fade_out, this);
	}

	private void shakeView(View v) {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this);
		animationHelper.invokeForView(v, R.anim.shake, this);
	}

	@Override
	public void onAnimationFinished(int id, boolean ignore) {
		if (ignore)
			return;
		Log.d(LOG_TAG,"got animation id = "+id);
		switch (id)
		{
			case (R.anim.close_fade_out):
				setAll(View.INVISIBLE);
				break;
			case (R.anim.close_word):
				setAllTexts();
				setAll(View.VISIBLE);
				openButtons();
				openWord();
				break;
		}
	}

    private class MyButtonOnClick implements OnClickListener {
        public int correct = 0;

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (correct == id) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(correctEntry.id);
                updateWordWeight();
	            updateListOfAliveIds();
	            if (findNextId())
	            {
		            closeWord();
		            closeButtons();
	            }
	            else
                    stopActivity();
            } else {
                numOfWrongAnswers++;
	            shakeView(v);
            }
        }
    }

}