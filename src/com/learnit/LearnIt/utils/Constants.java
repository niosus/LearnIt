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

import com.learnit.LearnIt.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 2/17/13
 * Time: 1:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
    public static final String LOG_TAG = "my_logs";

    public static final Map<String, String> mArticlesMap;
    static
    {
        mArticlesMap = new HashMap<>();
        mArticlesMap.put("de", "der die das");
    }

    public static final Map<String, String> existingDictionaries;
    static
    {
        existingDictionaries = new HashMap<>();
        existingDictionaries.put("de-ru", "market://details?id=com.learnit.dict_de_ru");
        existingDictionaries.put("en-ru", "market://details?id=com.learnit.dict_en_ru");
        existingDictionaries.put("en-uk", "market://details?id=com.learnit.dict_en_uk");
        existingDictionaries.put("es-en", "market://details?id=com.learnit.dict_es_en");
    }

    public final static int FROM_MY_TO_FOREIGN = 1;
    public final static int FROM_FOREIGN_TO_MY = 2;
    public final static int MIXED = 3;
    public final static int ONLY_NOUNS = 1;
    public final static int NOT_NOUNS = 2;
    public static final int LEARN_TRANSLATIONS = 1;
    public static final int LEARN_ARTICLES = 2;
    public static final int LEARN_MIXED = 3;
	public static final String CURRENT_HELP_DICT_TAG = "current_help_dict";

    public static final int NONE = -1;

	public static int[] btnIdsTranslations = {
			R.id.left_top_button,
			R.id.right_top_button,
			R.id.left_bottom_button,
			R.id.right_bottom_button };
	public static int[] btnIdsArticles = {
			R.id.btn_first,
			R.id.btn_second,
			R.id.btn_third };

    public final static float TAB_SELECTOR_HEIGHT_DP = 7f;

}
