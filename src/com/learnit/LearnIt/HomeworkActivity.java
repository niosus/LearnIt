/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class HomeworkActivity extends FragmentActivity{
    int notificationId = -1;
    String queryWord = null;
    String article = null;
    final String LOG_TAG = "my_logs";
    DBHelper dbHelper;
    int[] btnIds = {R.id.left_top_button,
                    R.id.right_top_button,
                    R.id.left_bottom_button,
                    R.id.right_bottom_button};

    private void getEverythingFromIntent() {
        Intent intent = getIntent();
        article = intent.getStringExtra("article");
        queryWord = intent.getStringExtra("word");
        notificationId = intent.getIntExtra("id", -1);
        Log.d(LOG_TAG, "got intent word=" + queryWord + " id = "
                + notificationId);
        dbHelper = new DBHelper(this);
    }

    private void setBtnTexts(int correctId)
    {
        ArrayList<String> usedWords = new ArrayList<String>();
        usedWords.add(queryWord);
        String temp;
        for (int i = 0; i<btnIds.length; ++i)
        {
            if (correctId!=i)
            {
                temp = dbHelper.getRandomTranslation(usedWords);
                if (null==temp)
                {
                    break;
                }
                ((Button) findViewById(btnIds[i])).setText(temp);
            }
            else
            {
                ((Button) findViewById(btnIds[i])).setText(dbHelper.getTranslation(queryWord));
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEverythingFromIntent();
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
                stopActivity();
            }
            else
            {
                showDialogWrong();
            }
        }
    }

    private String capitalize(String str)
    {
        if (str.length()>0)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        else
            return null;
    }

    protected void onResume() {
        super.onResume();

        TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
        if (null==article)
            queryWordTextView.setText(queryWord);
        else
        {
            queryWord = capitalize(queryWord);
            queryWordTextView.setText(article + " " + queryWord);
        }
    }
}