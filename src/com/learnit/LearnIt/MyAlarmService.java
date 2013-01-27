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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class MyAlarmService extends Service {

    private static final int idModificator = 1552235; // some number
    public static DBHelper dbHelper;
    private final int numOfNotif = 5;
    private final String LOG_TAG = "my_logs";

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"created Alarm");
        dbHelper = new DBHelper(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void stop() {
        super.stopSelf();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Random rand = new Random();
        boolean isNoun = rand.nextBoolean();
        ArrayList<Pair<String, String> > randWords = getRandWordsFromDB(isNoun);
        for (int i = randWords.size(); i>0; --i)
        {
            Log.d(LOG_TAG,"isNoun = " + isNoun +" "+randWords.get(i-1).second+" " + randWords.get(i-1).first);
            CreateNotif(i, randWords.get(i - 1).second, randWords.get(i - 1).first, isNoun);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private ArrayList<Pair<String, String> > getRandWordsFromDB(boolean isNoun)
    {
        ArrayList<Long> ids = new ArrayList<Long>();
        ArrayList<Pair<String, String> > result = new ArrayList<Pair<String, String>>();
        for (int i=0; i<numOfNotif; ++i)
        {
            ArticleWordIdStruct struct = dbHelper.getRandomWord(ids, isNoun);
            if (null!=struct)
            {
                result.add(new Pair<String, String>(struct.article, struct.word));
                ids.add(struct.id);
                Log.d(LOG_TAG,"current ids " + ids);
            }
            else
            {
                break;
            }
        }
        return result;
    }

    private String capitalize(String str)
    {
        if (str.length()>0)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        else
            return null;
    }

    private boolean CreateNotif(int wordNum, String word, String article, boolean isNoun) {
        int mId = wordNum + idModificator;
        if (null!=article)
        {
            word = capitalize(word);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setContentTitle(word).setContentText(getString(R.string.notif_text));
        switch (wordNum) {
            case 1:
                mBuilder.setSmallIcon(R.drawable.ic_stat_word_one);
                break;
            case 2:
                mBuilder.setSmallIcon(R.drawable.ic_stat_word_two);
                break;
            case 3:
                mBuilder.setSmallIcon(R.drawable.ic_stat_word_three);
                break;
            case 4:
                mBuilder.setSmallIcon(R.drawable.ic_stat_word_four);
                break;
            case 5:
                mBuilder.setSmallIcon(R.drawable.ic_stat_word_five);
                break;

        }
        Intent resultIntent;
        if (isNoun)
            resultIntent = new Intent(this, HomeworkArticleActivity.class);
        else
            resultIntent = new Intent(this, HomeworkActivity.class);
        resultIntent.putExtra("id", mId);
        resultIntent.putExtra("word", word);
        resultIntent.putExtra("article", article);
        resultIntent.setAction(mId + " " + word + " " + System.currentTimeMillis());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        if (isNoun)
            stackBuilder.addParentStack(HomeworkArticleActivity.class);
        else
            stackBuilder.addParentStack(HomeworkActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendInt = PendingIntent.getActivity(this, mId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendInt);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setOngoing(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
        stopSelf();
        return true;
    }

}
