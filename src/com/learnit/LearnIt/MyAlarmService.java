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

public class MyAlarmService extends Service {

    private static final int idModificator = 1552235; // some number
    public static DBHelper dbHelper;
    private final int numOfNotif = 5;
    private final String LOG_TAG = "my_logs";

    @Override
    public void onCreate() {
//        Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG)
//                .show();
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
//        Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG)
//                .show();

        ArrayList<Long> ids = new ArrayList<Long>();
        ArrayList<Pair<String, String> > randWords = getRandWordsFromDB();
        for (int i = randWords.size(); i>0; --i)
        {
            CreateNotif(i, randWords.get(i-1).second, randWords.get(i-1).first);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private ArrayList<Pair<String, String> > getRandWordsFromDB()
    {
        long DBSize = dbHelper.getDBSize();
        ArrayList<Long> ids = new ArrayList<Long>();
        ArrayList<Pair<String, String> > result = new ArrayList<Pair<String, String>>();
        for (int i=0; i<numOfNotif; ++i)
        {
            ArticleWordIdStruct struct = dbHelper.getRandomWord(ids, DBSize);
            if (null!=struct)
            {
                result.add(new Pair<String, String>(struct.article, struct.word));
                ids.add(struct.id);
                Log.d(LOG_TAG,"current ids " + ids);
            }
            else
            {
                //stop in case there are not enough distinct words
                break;
            }
        }
        return result;
    }

    private boolean CreateNotif(int wordNum, String word, String article) {
        int mId = wordNum + idModificator;

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
        Intent resultIntent = new Intent(this, HomeworkActivity.class);
        resultIntent.putExtra("id", mId);
        resultIntent.putExtra("word", word);
        resultIntent.putExtra("article", article);
        resultIntent.setAction(mId + " " + word + " " + System.currentTimeMillis());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
