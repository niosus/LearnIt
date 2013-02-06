package com.learnit.LearnIt.utils;

import android.content.Context;
import android.util.Log;
import com.learnit.LearnIt.R;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 2/6/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static final String LOG_TAG = "my_logs";
    boolean isArticle( Context context, String article) {

        String articles = context.getString(R.string.articles_de);
        return articles.contains(article.toLowerCase());
    }

    boolean isPrefix(Context context, String word) {
        String prefix = context.getString(R.string.help_words_de);
        return prefix.contains(word.toLowerCase());
    }

    private String cutAwayFirstWord(String input)
    {
        return input.split(" ", 2)[1];
    }


    public String stripFromArticle(Context context, String str)
    {
        String[] tempArray = str.split("\\s");
        Log.d(LOG_TAG, "str = " + str + ", array length = " + tempArray.length);
        if (tempArray.length==1)
        {
            return str;
        }
        else if (tempArray.length>1)
        {
            if (isArticle(context, tempArray[0]))
            {
                return cutAwayFirstWord(str);
            }
            else if (isPrefix(context, tempArray[0]))
            {
                return cutAwayFirstWord(str);
            }
            return str;
        }
        else return null;
    }

    public String capitalize(String str)
    {
        if (str.length()>0)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        else
            return null;
    }
}
