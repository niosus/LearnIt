package com.learnit.LearnIt.utils;

import com.learnit.LearnIt.R;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 2/17/13
 * Time: 1:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
    public static final String LOG_TAG = "my_logs";

    public final static int FROM_MY_TO_FOREIGN = 1;
    public final static int FROM_FOREIGN_TO_MY = 2;
    public final static int MIXED = 3;
    public final static int ONLY_NOUNS = 1;
    public final static int NOT_NOUNS = 2;
    public static final int LEARN_TRANSLATIONS = 1;
    public static final int LEARN_ARTICLES = 2;
    public static final int LEARN_MIXED = 3;
	public static final String CURRENT_HELP_DICT_TAG = "current_help_dict";

	public static int[] btnIdsTranslations = {
			R.id.left_top_button,
			R.id.right_top_button,
			R.id.left_bottom_button,
			R.id.right_bottom_button };
	public static int[] btnIdsArticles = {
			R.id.btn_first,
			R.id.btn_second,
			R.id.btn_third };


}
