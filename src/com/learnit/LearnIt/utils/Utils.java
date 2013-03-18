package com.learnit.LearnIt.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import com.learnit.LearnIt.DBHelper;
import com.learnit.LearnIt.R;

import java.util.Arrays;
import java.util.Locale;

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

    public Pair<String,String> updateLanguages(Context context)
    {
        String currentLanguage;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedLanguageFrom = sp.getString(context.getString(R.string.key_language_from),"NONE");
        String selectedLanguageTo = sp.getString(context.getString(R.string.key_language_to),"NONE");
        Resources res = context.getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        if (allLanguages.contains(selectedLanguageTo))
        {
            currentLanguage = selectedLanguageTo;
        }
        else
        {
            currentLanguage = Locale.getDefault().getLanguage();
        }
        DBHelper.DB_WORDS = "myDB"+selectedLanguageFrom+currentLanguage;
        return new Pair<String, String>(selectedLanguageFrom,currentLanguage);
    }

    public String getGermanArticle(String sex)
    {
        if ("m"==sex)
            return "der";
        else if ("f"==sex)
            return "die";
        else if ("n"==sex)
            return "das";
        else return null;
    }
}
