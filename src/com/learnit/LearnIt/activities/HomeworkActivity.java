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
import android.widget.TextView;
import com.learnit.LearnIt.fragments.MyDialogFragment;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.ArticleWordIdStruct;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;
import com.learnit.LearnIt.views.WordButton;

import java.util.ArrayList;
import java.util.Random;

public class HomeworkActivity extends FragmentActivity {
    int fromLearnToKnow = 0;
    int numOfWrongAnswers = 0;
    ArticleWordIdStruct correctEntry = null;
    int isNoun = 3;
    Utils utils;
    final String LOG_TAG = "my_logs";
    DBHelper dbHelper;
    int[] btnIds = {R.id.left_top_button,
            R.id.right_top_button,
            R.id.left_bottom_button,
            R.id.right_bottom_button};

    private void getEverythingFromIntent() {
        Intent intent = getIntent();
        correctEntry = new ArticleWordIdStruct(
                intent.getStringExtra("article"),
                intent.getStringExtra("prefix"),
                intent.getStringExtra("word"),
                intent.getStringExtra("translation"),
                intent.getIntExtra("id", -1)
        );

        fromLearnToKnow = intent.getIntExtra("direction", -1);
        isNoun = intent.getIntExtra("is_noun", 3);
        Log.d(LOG_TAG, "got intent word=" + correctEntry.word + " id = "
                + correctEntry.id);
        dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
    }

    private void setBtnTexts(int correctId) {
        if (null == correctEntry.article) {
            isNoun = Constants.NOT_NOUNS;
        } else {
            isNoun = Constants.ONLY_NOUNS;
        }
        ArrayList<ArticleWordIdStruct> randomWords = dbHelper.getRandomWords(btnIds.length, correctEntry.word, isNoun);
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEverythingFromIntent();
        utils = new Utils();
        setContentView(R.layout.homework);
        MyButtonOnClick myButtonOnClick = new MyButtonOnClick();
        Random random = new Random();
        int randIdx = random.nextInt(btnIds.length);
        myButtonOnClick.correct = btnIds[randIdx];
        (findViewById(R.id.left_top_button))
                .setOnClickListener(myButtonOnClick);
        (findViewById(R.id.right_bottom_button))
                .setOnClickListener(myButtonOnClick);
        (findViewById(R.id.left_bottom_button))
                .setOnClickListener(myButtonOnClick);
        (findViewById(R.id.right_top_button))
                .setOnClickListener(myButtonOnClick);
        setBtnTexts(randIdx);
    }

    private void showDialogWrong() {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_GUESS);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "wrong_guess");
    }

    protected void stopActivity() {
        this.finish();
    }

    private void updateWordWeight() {
        Log.d(LOG_TAG, "word to be updated " + correctEntry.word);
        switch (numOfWrongAnswers) {
            case 0:
                dbHelper.updateWordWeight(correctEntry.word.toLowerCase(), DBHelper.WEIGHT_CORRECT_BUTTON);
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

    private class MyButtonOnClick implements OnClickListener {
        public int correct = 0;

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (correct == id) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel((int) correctEntry.id);
                updateWordWeight();
                stopActivity();
            } else {
                numOfWrongAnswers++;
                showDialogWrong();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
        switch (fromLearnToKnow) {
            case Constants.FROM_FOREIGN_TO_MY:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String learnLang = sp.getString(getString(R.string.key_language_from), "null");
                if (null != correctEntry.article) {
                    if ("de".equals(learnLang)) {
                        queryWordTextView.setText(correctEntry.article + " " + utils.capitalize(correctEntry.word));
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
}