package com.learnit.LearnIt.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.services.NotificationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String LOG_TAG = "my_logs";

    static boolean isArticle(Context context, String article) {

        String articles = context.getString(R.string.articles_de);
        return articles.contains(article.toLowerCase());
    }

    static boolean isPrefix(Context context, String word) {
        String prefix = context.getString(R.string.help_words_de);
        return prefix.contains(word.toLowerCase());
    }

    public static String cutAwayFirstWord(String input) {
        return input.split("\\s", 2)[1];
    }


    public static String stripFromArticle(Context context, String str) {
        String[] tempArray = str.split("\\s");
        Log.d(LOG_TAG, "str = " + str + ", array length = " + tempArray.length);
        if (tempArray.length == 1) {
            return str;
        } else if (tempArray.length > 1) {
            if (isArticle(context, tempArray[0])) {
                return cutAwayFirstWord(str);
            } else if (isPrefix(context, tempArray[0])) {
                return cutAwayFirstWord(str);
            }
            return str;
        } else return null;
    }

    public static String capitalize(String str) {
        if (str.length() > 0)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        else
            return null;
    }

    public static String getGermanArticle(String sex) {
        if ("m".equals(sex))
            return "der";
        else if ("f".equals(sex))
            return "die";
        else if ("n".equals(sex))
            return "das";
        else return null;
    }


    public static String getArticleFromDictOutput(String str, String languageFrom) {
        String selectSexI = "<i>(m|f|n)</i>";
        String selectSexAbr = "<abr>(m|f|n)</abr>";
        Pattern p;
        Matcher matcher;
        if (languageFrom.equals("de")) {
            p = Pattern.compile(selectSexI);
            matcher = p.matcher(str);
            String sexI = null;
            String sexAbr = null;
            if (matcher.find()) {
                sexI = matcher.group(1);
                Log.d(LOG_TAG, "sexI = " + sexI);
            }
            p = Pattern.compile(selectSexAbr);
            matcher = p.matcher(str);
            while (matcher.find()) {
                sexAbr = matcher.group(1);
                Log.d(LOG_TAG, "sexAbr = " + sexAbr);
            }
            String article = null;
            if (null != sexI)
                article = getGermanArticle(sexI);
            else if (null != sexAbr)
                article = getGermanArticle(sexAbr);
            if (null != article) {
                return article;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static ArrayList<String> getHelpWordsFromDictOutput(String str) {
        ArrayList<String> tagValues = new ArrayList<String>();
        if (str.contains("<dtrn>")) {
            String deleteCo = "(<tr>(.*)</tr>)|(<co>(.+?)</co>)|(<abr>(.+?)</abr>)|(<c>(.*)</c>)|(<i>(.+?)</i>)|(<nu />(.+?)<nu />)";
            String selectDtrn = "<dtrn>(.+?)</dtrn>";
            Pattern p;
            Matcher matcher;
            p = Pattern.compile(deleteCo);
            matcher = p.matcher(str);
            while (matcher.find()) {
                str = matcher.replaceAll("");
                matcher = p.matcher(str);
            }
            p = Pattern.compile(selectDtrn);
            matcher = p.matcher(str);
            while (matcher.find()) {
                String[] temp = matcher.group(1).split("\\s*(,|;)\\s*");
                for (String s : temp) {
                    tagValues.add(s);
                }
            }
            return tagValues;
        } else {
            String[] temp = str.split("\\s*(\\n|,)\\s*");
            for (String s : temp) {
                if (!s.equals(temp[0])) {
                    tagValues.add(s);
                }
            }
            return tagValues;
        }
    }

}
