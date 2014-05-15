/*
 * Copyright (C) 2014  Igor Bogoslavskyi
 * This file is part of LearnIt.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.learnit.LearnIt.utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String LOG_TAG = "my_logs";

    public static boolean isArticle(Context context, String article) {
		if (article==null) return false;
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

    public static String splitOnRegex(String stringToSplit, String regex)
    {
        String[] separateParts = stringToSplit.split(regex, -1);
        String res = "";
        for (String w: separateParts)
        {
            if (res.isEmpty())
                res = w;
            else if (w.length()>0)
                res +="\n"+w;
        }
        return res;
    }

    public static String prepareForDatabaseQuery(final String inputWord) {
        if (inputWord == null || inputWord.isEmpty()) {
            return "";
        }
        String result;
        result = inputWord.toLowerCase();
        return result;
    }

	public static Pair<String, List<String>> parseDictOutput(String str, String langFrom) {
		Log.d(Constants.LOG_TAG, "input = " + str);
		List<String> tagValues = StringUtils.getHelpWordsFromDictOutput(str);
		String article = StringUtils.getArticleFromDictOutput(str, langFrom);
		return new Pair<>(article, tagValues);
	}

    public static String stripFromArticle(Context context, String str) {
	    if (str == null) return null;
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
        ArrayList<String> tagValues = new ArrayList<>();
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
