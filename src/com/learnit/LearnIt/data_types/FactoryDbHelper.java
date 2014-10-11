package com.learnit.LearnIt.data_types;

import android.content.Context;

import com.learnit.LearnIt.utils.Utils;

/**
 * Created by igor on 11/10/14.
 */
public class FactoryDbHelper {
    public static DBHelper createDbHelper(Context context, String dbName) {
        String localizedDbName = Utils.localizeDBName(context, dbName);
        return new DBHelper(context, localizedDbName, true);
    }
}
