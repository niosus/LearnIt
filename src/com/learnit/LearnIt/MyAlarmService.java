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
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.learnit.LearnIt.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

public class MyAlarmService extends Service {
    private final String NONE_STR = "-1";
    private final int NONE = -1;
    public static final int idModificator = 1552235; // some number
    public static DBHelper dbHelper;
    private int numOfNotif = 5;
    private int mode;
    private int directionOfTrans= Constants.MIXED;
    private final String LOG_TAG = "my_logs";

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"created Alarm");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String strNumOfNotif = sp.getString(getString(R.string.key_num_of_words), NONE_STR);
        numOfNotif = Integer.parseInt(strNumOfNotif);
        String strMode = sp.getString(getString(R.string.key_way_to_learn),NONE_STR);
        mode = Integer.parseInt(strMode);
        String strTransDir = sp.getString(getString(R.string.key_direction_of_trans),NONE_STR);
        directionOfTrans = Integer.parseInt(strTransDir);
        if (-1==directionOfTrans)
        {
            directionOfTrans=Constants.MIXED;
        }
        if (-1==numOfNotif)
        {
            numOfNotif=5;
        }
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Random rand = new Random();
        boolean isNoun;
        switch (mode)
        {
            case 1:
                isNoun = false;
                break;
            case 2:
                isNoun = true;
                break;
            case 3:
                isNoun = rand.nextBoolean();
                break;
            default:isNoun=false;
        }
        ArrayList<ArticleWordIdStruct> randWords = getRandWordsFromDB(isNoun);
        for (int i = randWords.size(); i>0; --i)
        {
            Log.d(LOG_TAG,"isNoun = " + isNoun +" "+randWords.get(i-1).word);
            CreateNotification(i, randWords.get(i - 1), isNoun);
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private ArrayList<ArticleWordIdStruct> getRandWordsFromDB(boolean isNoun)
    {
        Log.d(LOG_TAG,"MyAlarmService: get random words executed");
        return dbHelper.getRandomWords(numOfNotif, "", isNoun);
    }

    private boolean CreateNotification(int wordNum, ArticleWordIdStruct struct, boolean isNoun) {
        NotificationCompat.Builder mBuilder = null;
        int currentDirection=-1;
        Intent resultIntent;
        if (isNoun)
        {
            resultIntent = new Intent(this, HomeworkArticleActivity.class);
            directionOfTrans = Constants.FROM_FOREIGN_TO_MY;
        }
        else
            resultIntent = new Intent(this, HomeworkActivity.class);
        int mId = (int)struct.id + idModificator;
        resultIntent.putExtra("id", mId);
        resultIntent.putExtra("word", struct.word);
        resultIntent.putExtra("article", struct.article);
        resultIntent.putExtra("translation", struct.translation);
        resultIntent.putExtra("prefix", struct.prefix);
        switch (directionOfTrans)
        {
            case Constants.MIXED:
                Random rand = new Random();
                currentDirection = rand.nextInt(2)+1;
                if (Constants.FROM_MY_TO_FOREIGN==currentDirection)
                    mBuilder = new NotificationCompat.Builder(
                            this).setContentTitle(struct.translation).setContentText(getString(R.string.notif_text));
                else if (Constants.FROM_FOREIGN_TO_MY==currentDirection)
                    mBuilder = new NotificationCompat.Builder(
                            this).setContentTitle(struct.word).setContentText(getString(R.string.notif_text));

                resultIntent.putExtra("direction", currentDirection);
                break;
            case Constants.FROM_FOREIGN_TO_MY:
                mBuilder = new NotificationCompat.Builder(
                    this).setContentTitle(struct.word).setContentText(getString(R.string.notif_text));
                resultIntent.putExtra("direction", directionOfTrans);
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                mBuilder =  new NotificationCompat.Builder(
                        this).setContentTitle(struct.translation).setContentText(getString(R.string.notif_text));
                resultIntent.putExtra("direction", directionOfTrans);
                break;
            default:
                return false;
        }
        switch (wordNum) {
            case 1:
                mBuilder.setSmallIcon(R.drawable.ic_stat_one);
                break;
            case 2:
                mBuilder.setSmallIcon(R.drawable.ic_stat_two);
                break;
            case 3:
                mBuilder.setSmallIcon(R.drawable.ic_stat_three);
                break;
            case 4:
                mBuilder.setSmallIcon(R.drawable.ic_stat_four);
                break;
            case 5:
                mBuilder.setSmallIcon(R.drawable.ic_stat_five);
                break;
            case 6:
                mBuilder.setSmallIcon(R.drawable.ic_stat_six);
                break;
            case 7:
                mBuilder.setSmallIcon(R.drawable.ic_stat_seven);
                break;
            case 8:
                mBuilder.setSmallIcon(R.drawable.ic_stat_eight);
                break;
            case 9:
                mBuilder.setSmallIcon(R.drawable.ic_stat_nine);
                break;
            case 10:
                mBuilder.setSmallIcon(R.drawable.ic_stat_ten);
                break;

        }
        resultIntent.setAction(mId + " " + struct.word + " " + System.currentTimeMillis());
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
