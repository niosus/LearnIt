package com.learnit.LearnIt.data_types;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.activities.HomeworkActivity;
import com.learnit.LearnIt.activities.HomeworkArticleActivity;
import com.learnit.LearnIt.data_types.ArticleWordIdStruct;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

public class NotificationBuilder {
    public static final String LOG_TAG = "my_logs";

    private static final int LEARN_TRANSLATIONS = 1;
    private static final int LEARN_ARTICLES = 2;
    private static final int LEARN_MIXED = 3;

    static String currentIds = "";
    static SharedPreferences sp;


    public static final int idModificator = 1552235; // some number


    private static ArrayList<ArticleWordIdStruct> getRandWordsFromDB(int isNoun, int numOfNotif, Context context) {
        DBHelper dbHelper = new DBHelper(context, DBHelper.DB_WORDS);
        return dbHelper.getRandomWords(numOfNotif, "", isNoun);
    }

    private static int setWayToLearn(Context context, SharedPreferences sp) {
        Random rand = new Random();
        int wayToLearn = Integer.parseInt(sp.getString(context.getString(R.string.key_way_to_learn), "3"));
        if (wayToLearn == LEARN_MIXED) {
            wayToLearn = rand.nextInt(2) + 1;
        }
        return wayToLearn;
    }

    private static int setNumberOfWords(Context context, SharedPreferences sp) {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_num_of_words), "5"));
    }

    private static int setDirectionOfTranslation(Context context, SharedPreferences sp) {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_direction_of_trans), "3"));
    }

    private static void deleteOldNotifications(Context context, String old_ids) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(LOG_TAG, "old ids = " + old_ids);
        if (null != old_ids) {
            String[] ids = old_ids.split(" ");
            for (String id : ids) {
                if (null != id && !id.equals("")) {
                    Log.d(LOG_TAG, "trying to delete notif with id " + Integer.parseInt(id));
                    mNotificationManager.cancel(Integer.parseInt(id));
                }
            }
        }
    }


    public static void show(Context context) {
        Log.d(LOG_TAG, "context class = " + context.getClass().getName());
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        String old_ids = sp.getString("current_ids", "");
        deleteOldNotifications(context, old_ids);
        DBHelper.updateDBName(context, sp);
        int wayToLearn = setWayToLearn(context, sp);
        int numberOfWords = setNumberOfWords(context, sp);
        Log.d(LOG_TAG, "number of notifications = " + numberOfWords);
        int isNoun;
        int directionOfTrans;
        switch (wayToLearn) {
            case LEARN_TRANSLATIONS:
                directionOfTrans = setDirectionOfTranslation(context, sp);
                isNoun = Constants.MIXED;
                break;
            case LEARN_ARTICLES:
                directionOfTrans = Constants.FROM_FOREIGN_TO_MY;
                isNoun = Constants.ONLY_NOUNS;
                break;
            default:
                isNoun = Constants.MIXED;
                directionOfTrans = setDirectionOfTranslation(context, sp);
        }
        ArrayList<ArticleWordIdStruct> randWords = getRandWordsFromDB(isNoun, numberOfWords, context);
        for (int i = randWords.size(); i > 0; --i) {
            Log.d(LOG_TAG, "isNoun = " + isNoun + " " + randWords.get(i - 1).word);
            CreateNotification(i, randWords.get(i - 1), wayToLearn, directionOfTrans, context);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("current_ids", currentIds);
        editor.commit();
    }

    private static boolean CreateNotification(int wordNum, ArticleWordIdStruct struct, int wayToLearn, int mDirectionOfTrans, Context context) {
        Log.d(LOG_TAG, "starting to create notification");
        NotificationCompat.Builder mBuilder = null;
        int currentDirection;
        Intent resultIntent;
        switch (wayToLearn) {
            case LEARN_TRANSLATIONS:
                resultIntent = new Intent(context, HomeworkActivity.class);
                break;
            case LEARN_ARTICLES:
                resultIntent = new Intent(context, HomeworkArticleActivity.class);
                break;
            default:
                resultIntent = new Intent(context, HomeworkActivity.class);
        }
        int mId = (int) struct.id + idModificator;
        resultIntent.putExtra("id", mId);
        resultIntent.putExtra("word", struct.word);
        resultIntent.putExtra("article", struct.article);
        resultIntent.putExtra("translation", struct.translation);
        resultIntent.putExtra("prefix", struct.prefix);

        currentIds = currentIds + mId + " ";

        switch (mDirectionOfTrans) {
            case Constants.MIXED:
                Random rand = new Random();
                currentDirection = rand.nextInt(2) + 1;
                if (Constants.FROM_MY_TO_FOREIGN == currentDirection)
                    mBuilder = new NotificationCompat.Builder(context).setContentTitle(struct.translation).setContentText(context.getString(R.string.notif_text));
                else if (Constants.FROM_FOREIGN_TO_MY == currentDirection)
                    mBuilder = new NotificationCompat.Builder(context).setContentTitle(struct.word).setContentText(context.getString(R.string.notif_text));

                resultIntent.putExtra("direction", currentDirection);
                break;
            case Constants.FROM_FOREIGN_TO_MY:
                mBuilder = new NotificationCompat.Builder(context).setContentTitle(struct.word).setContentText(context.getString(R.string.notif_text));
                resultIntent.putExtra("direction", mDirectionOfTrans);
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                mBuilder = new NotificationCompat.Builder(context).setContentTitle(struct.translation).setContentText(context.getString(R.string.notif_text));
                resultIntent.putExtra("direction", mDirectionOfTrans);
                break;
            default:
                return false;
        }
        if (null != mBuilder) {
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
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            if (wayToLearn == LEARN_ARTICLES)
                stackBuilder.addParentStack(HomeworkArticleActivity.class);
            else
                stackBuilder.addParentStack(HomeworkActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent pendInt = PendingIntent.getActivity(context, mId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendInt);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setOngoing(true);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(mId, mBuilder.build());
            return true;
        } else {
            return false;
        }

    }
}