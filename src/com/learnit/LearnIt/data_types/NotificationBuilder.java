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
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class NotificationBuilder {
    public static final String LOG_TAG = "my_logs";

	public static final String IDS_TAG = "ids";
	public static final String WORDS_TAG = "words";
	public static final String ARTICLES_TAG = "articles";
	public static final String PREFIXES_TAG = "prefixes";
	public static final String TRANSLATIONS_TAG = "translations";
	public static final String DIRECTIONS_OF_TRANS_TAG = "directions_of_trans";
	public static final String CURRENT_NOTIFICATION_INDEX = "current_index";

    static String currentIds = "";
    static SharedPreferences sp;


    public static final int idModificator = 1552235; // some number


    private static ArrayList<ArticleWordId> getRandWordsFromDB(int waytoLearn, int numOfNotif, Context context) {
        DBHelper dbHelper = new DBHelper(context, DBHelper.DB_WORDS);
	    int isNoun;
	    switch (waytoLearn)
	    {
		    case Constants.LEARN_ARTICLES:
			    isNoun = Constants.ONLY_NOUNS;
			    break;
		    default:
			    isNoun = Constants.MIXED;
	    }
        return dbHelper.getRandomWords(numOfNotif, "", isNoun);
    }

    private static int getWayToLearn(Context context, SharedPreferences sp) {
        int wayToLearn = Integer.parseInt(sp.getString(context.getString(R.string.key_way_to_learn), "3"));
	    Log.d(LOG_TAG,"way to learn = " + wayToLearn);
        return wayToLearn;
    }

    private static int setNumberOfWords(Context context, SharedPreferences sp) {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_num_of_words), "5"));
    }

    private static int getDirectionOfTranslation(Context context, SharedPreferences sp, int homeworkActivityType) {
        if (homeworkActivityType==Constants.LEARN_ARTICLES)
        {
            return Constants.FROM_FOREIGN_TO_MY;
        }
	    int currentDirection = Integer.parseInt(sp.getString(context.getString(R.string.key_direction_of_trans), "3"));
	    if (currentDirection == Constants.MIXED)
	    {
		    Random rand = new Random();
		    currentDirection = rand.nextInt(2) + 1;
	    }
	    return currentDirection;
    }

    private static void deleteOldNotifications(Context context, String old_ids) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != old_ids) {
            String[] ids = old_ids.split(" ");
            for (String id : ids) {
                if (null != id && !id.equals("")) {
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
        int wayToLearn = getWayToLearn(context, sp);
        int numberOfWords = setNumberOfWords(context, sp);
        Log.d(LOG_TAG, "number of notifications = " + numberOfWords);
        ArrayList<ArticleWordId> randWords = getRandWordsFromDB(wayToLearn, numberOfWords, context);
	    CreateNotifications(randWords, context, wayToLearn);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("current_ids", currentIds);
        editor.commit();
    }

    private static int getRandomWayToLearn()
    {
        Random r = new Random();
        int randInt = r.nextInt(2);
        if (randInt==0)
        {
            return Constants.LEARN_ARTICLES;
        }
        else
        {
            return Constants.LEARN_TRANSLATIONS;
        }
    }


    private static int getHomeworkType(int wayToLearn, String article)
    {
        int type = Constants.LEARN_TRANSLATIONS;
        switch (wayToLearn)
        {
        case Constants.LEARN_MIXED:
            if (article!=null)
            {
                type = getRandomWayToLearn();
            }
            else
            {
                type = Constants.LEARN_TRANSLATIONS;
            }
            break;
        case Constants.LEARN_TRANSLATIONS:
            type=Constants.LEARN_TRANSLATIONS;
            break;
            case Constants.LEARN_ARTICLES:
            if (article!=null)
            {
                type=Constants.LEARN_ARTICLES;
            }
            else
            {
                type=Constants.LEARN_TRANSLATIONS;
            }
            break;
        }
        return type;
    }

	private static Intent getIntentFromHomeworkType(Context context, int homeworkActivityType)
	{
		switch (homeworkActivityType) {
			case Constants.LEARN_TRANSLATIONS:
				return new Intent(context, HomeworkActivity.class);
			case Constants.LEARN_ARTICLES:
				return new Intent(context, HomeworkArticleActivity.class);
			default:
				return null;
		}
	}

	private static Class getStackTypeFromHomeworkType(int homeworkActivityType)
	{
		switch (homeworkActivityType) {
			case Constants.LEARN_TRANSLATIONS:
				return HomeworkActivity.class;
			case Constants.LEARN_ARTICLES:
				return HomeworkArticleActivity.class;
			default:
				return null;
		}
	}

	private static NotificationCompat.Builder getBuilder(Context context, int currentDirection, ArticleWordId struct)
	{
		if (Constants.FROM_MY_TO_FOREIGN == currentDirection)
			return new NotificationCompat.Builder(context).setContentTitle(struct.translation).setContentText(context.getString(R.string.notif_text));
		else if (Constants.FROM_FOREIGN_TO_MY == currentDirection)
			return new NotificationCompat.Builder(context).setContentTitle(struct.word).setContentText(context.getString(R.string.notif_text));
		else return null;
	}


	private static boolean CreateNotifications(ArrayList<ArticleWordId> randWords, Context context, int wayToLearn) {
		ArrayList<Intent> intents = new ArrayList<Intent>();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> articles = new ArrayList<String>();
		ArrayList<String> translations = new ArrayList<String>();
		ArrayList<String> prefixes = new ArrayList<String>();
		ArrayList<Integer> directionsOfTrans = new ArrayList<Integer>();
		ArrayList<Class> classes = new ArrayList<Class>();
		for (ArticleWordId struct: randWords)
		{
			int homeworkActivityType = getHomeworkType(wayToLearn, struct.article);
			int directionOfTranslation = getDirectionOfTranslation(context, sp, homeworkActivityType);
			ids.add(struct.id + idModificator);
			words.add(struct.word);
			articles.add(struct.article);
			translations.add(struct.translation);
			prefixes.add(struct.prefix);
			intents.add(getIntentFromHomeworkType(context, homeworkActivityType));
			classes.add(getStackTypeFromHomeworkType(homeworkActivityType));
			directionsOfTrans.add(directionOfTranslation);
		}
		for (int i=0; i<intents.size(); ++i)
		{
			Intent intent = intents.get(i);
			intent.putExtra(IDS_TAG, ids);
			intent.putExtra(WORDS_TAG, words);
			intent.putExtra(TRANSLATIONS_TAG, translations);
			intent.putExtra(ARTICLES_TAG, articles);
			intent.putExtra(PREFIXES_TAG, prefixes);
			intent.putExtra(DIRECTIONS_OF_TRANS_TAG, directionsOfTrans);
			intent.putExtra(CURRENT_NOTIFICATION_INDEX, i);
			intent.setAction(ids.get(i) + " " + words.get(i) + " " + System.currentTimeMillis());
			NotificationCompat.Builder mBuilder;
			mBuilder = getBuilder(context, directionsOfTrans.get(i), randWords.get(i));
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			stackBuilder.addParentStack(classes.get(i));
			stackBuilder.addNextIntent(intent);
			PendingIntent pendInt = PendingIntent.getActivity(context, ids.get(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			if (null != mBuilder)
			{
				mBuilder.setSmallIcon(Utils.getIconForWordNumber(i+1));
				mBuilder.setContentIntent(pendInt);
				mBuilder.setPriority(Notification.PRIORITY_MAX);
				mBuilder.setOngoing(true);
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(ids.get(i), mBuilder.build());
				currentIds = currentIds + ids.get(i) + " ";
			}
			else
				return false;
		}
		return true;
	}
}
