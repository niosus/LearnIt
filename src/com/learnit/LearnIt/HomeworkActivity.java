/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;

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
import android.widget.Button;
import android.widget.TextView;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class HomeworkActivity extends FragmentActivity{
    int notificationId = -1;
    int fromLearnToKnow = 0;
    String queryWord = null;
    int numOfWrongAnswers=0;
    String article = null;
    String prefix = null;
    String translation = null;
    Utils utils;
    final String LOG_TAG = "my_logs";
    DBHelper dbHelper;
    int[] btnIds = {R.id.left_top_button,
                    R.id.right_top_button,
                    R.id.left_bottom_button,
                    R.id.right_bottom_button};

    private void getEverythingFromIntent() {
        Intent intent = getIntent();
        article = intent.getStringExtra("article");
        prefix = intent.getStringExtra("prefix");
        translation = intent.getStringExtra("translation");
        queryWord = intent.getStringExtra("word");
        notificationId = intent.getIntExtra("id", -1);
        fromLearnToKnow = intent.getIntExtra("direction", -1);
        Log.d(LOG_TAG, "got intent word=" + queryWord + " id = "
                + notificationId);
        dbHelper = new DBHelper(this);
    }

    private void setBtnTexts(int correctId)
    {

        ArrayList<ArticleWordIdStruct> randomWords = dbHelper.getRandomWords(btnIds.length,queryWord,false);
        switch (fromLearnToKnow)
        {
            case Constants.FROM_FOREIGN_TO_MY:
                for (int i=0; i<randomWords.size(); ++i)
                {
                    if (correctId==i)
                    {
                        ((Button) findViewById(btnIds[i])).setText(translation);
                    }
                    else
                    {
                        findViewById(btnIds[i]).setEnabled(true);
                        ((Button) findViewById(btnIds[i])).setText(randomWords.get(i).translation);
                    }
                }
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                for (int i=0; i<randomWords.size(); ++i)
                {
                    if (correctId==i)
                    {
                        ((Button) findViewById(btnIds[i])).setText(queryWord);
                    }
                    else
                    {
                        findViewById(btnIds[i]).setEnabled(true);
                        ((Button) findViewById(btnIds[i])).setText(randomWords.get(i).word);
                    }
                }
                break;
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
        myButtonOnClick.correct=btnIds[randIdx];
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

    private void showDialogWrong()
    {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_GUESS);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "wrong_guess");
    }

    protected void stopActivity()
    {
        this.finish();
    }

    private void updateWordWeight()
    {
        Log.d(LOG_TAG,"word to be updated " + queryWord);
        switch (numOfWrongAnswers)
        {
            case 0:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_CORRECT_BUTTON);
                break;
            case 1:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_ONE_WRONG);
                break;
            case 2:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_TWO_WRONG);
                break;
            case 3:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_THREE_WRONG);
                break;
        }
    }

    private class MyButtonOnClick implements OnClickListener
    {
        public int correct = 0;
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (correct==id)
            {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(notificationId);
                updateWordWeight();
                stopActivity();
            }
            else
            {
                numOfWrongAnswers++;
                showDialogWrong();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
        switch (fromLearnToKnow)
        {
            case Constants.FROM_FOREIGN_TO_MY:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String learnLang = sp.getString(getString(R.string.key_language_from),"null");
                if (null!=article)
                {
                    if ("de".equals(learnLang))
                    {
                        queryWordTextView.setText(article + " " + utils.capitalize(queryWord));
                    }
                    else
                    {
                        queryWordTextView.setText(article + " " + queryWord);
                    }
                }
                else if (null!=prefix)
                {
                    queryWordTextView.setText(prefix + " " + queryWord);
                }
                else
                {
                    queryWordTextView.setText(queryWord);
                }
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                queryWordTextView.setText(translation);
                break;
        }
    }
}