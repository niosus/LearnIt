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
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class HomeworkArticleActivity extends FragmentActivity{
    int notificationId = -1;
    String queryWord = null;
    String article = null;
    final String LOG_TAG = "my_logs";
    DBHelper dbHelper;
    int[] btnIds = {R.id.btn_first,
                    R.id.btn_second,
                    R.id.btn_third};

    private void getEverythingFromIntent() {
        Intent intent = getIntent();
        article = intent.getStringExtra("article");
        queryWord = intent.getStringExtra("word");
        notificationId = intent.getIntExtra("id", -1);
        Log.d(LOG_TAG, "got intent word=" + queryWord + " id = "
                + notificationId);
        dbHelper = new DBHelper(this);
    }

    private String getRandArticle(String correctArt)
    {
        Resources res = getResources();
        String[] articles = res.getStringArray(R.array.german_articles);
        Log.d(LOG_TAG,"articles are " + articles);
        Random rand = new Random();
        int length = articles.length;
        int randId;
        do {
             randId = rand.nextInt(length);
        }
        while (correctArt.equals(articles[randId]));
        return articles[randId];
    }

    private void setBtnTexts(int correctId)
    {
        for (int i = 0; i<btnIds.length; ++i)
        {
            if (correctId==i)
            {
                ((Button) findViewById(btnIds[i])).setText(article);
            }
            else
            {
                ((Button) findViewById(btnIds[i])).setText(getRandArticle(article));
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEverythingFromIntent();
        setContentView(R.layout.homework_articles);
        MyButtonOnClick myButtonOnClick = new MyButtonOnClick();
        Random random = new Random();
        int randIdx = random.nextInt(btnIds.length);
        myButtonOnClick.correct=btnIds[randIdx];
        (findViewById(R.id.btn_first))
                .setOnClickListener(myButtonOnClick);
        (findViewById(R.id.btn_second))
                .setOnClickListener(myButtonOnClick);
        (findViewById(R.id.btn_third))
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

    protected void onResume() {
        super.onResume();
        TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
        queryWordTextView.setText(queryWord);
    }
}