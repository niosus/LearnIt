
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

package com.learnit.LearnIt.data_types;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.widget.Toast;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    final static int DB_VERSION = 1;
    public static final String LOG_TAG = "my_logs";
    public static String DB_WORDS = "myDB"; //gets changed when the languages are updated
    final public static String DB_DICT_FROM = "dictFROM";
    final public String WORD_COLUMN_NAME = "word";
    final public String ID_COLUMN_NAME = "id";
    final public String ARTICLE_COLUMN_NAME = "article";
    final public String WEIGHT_COLUMN_NAME = "weight";
    final public String PREFIX_COLUMN_NAME = "prefix";
    final public String TRANSLATION_COLUMN_NAME = "translation";


    final public String DICT_OFFSET_COLUMN_NAME = "start_offset";
    final public String DICT_CHUNK_SIZE_COLUMN_NAME = "end_offset";

    public String currentDBName;

    public static final int EXIT_CODE_EMPTY_INPUT = -10;
    public static final int EXIT_CODE_WORD_ALREADY_IN_DB = -11;
    public static final int EXIT_CODE_WORD_UPDATED = 1;
    public static final int EXIT_CODE_OK = 0;
    public static final int EXIT_CODE_WRONG_ARTICLE = -12;
    public static final int EXIT_CODE_WRONG_FORMAT = -13;

    public static final int WEIGHT_NEW = 100;

    public static final int WEIGHT_ONE_WRONG = 100;
    public static final int WEIGHT_TWO_WRONG = 1000;
    public static final int WEIGHT_THREE_WRONG = 10000;

    public static final int WEIGHT_CORRECT = 10;
//    public static final int WEIGHT_NO_MORE_LEARNING=0;
//    public static final int WEIGHT_CORRECT_INPUT=1;


    long maxId = 0;

    private Context mContext;

    SQLiteDatabase db;

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
        currentDBName = dbName;
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database " + currentDBName + "---");
        switch (currentDBName.toCharArray()[0]) {
            case 'm':
                db.execSQL("CREATE TABLE " + currentDBName + " ("
                        + ID_COLUMN_NAME + " integer primary key autoincrement,"
                        + ARTICLE_COLUMN_NAME + " text,"
                        + WORD_COLUMN_NAME + " text,"
                        + TRANSLATION_COLUMN_NAME + " text,"
                        + WEIGHT_COLUMN_NAME + " integer,"
                        + PREFIX_COLUMN_NAME + " text" + ");");
                break;
            case 'd':
                db.execSQL("CREATE TABLE " + currentDBName + " ("
                        + ID_COLUMN_NAME + " integer primary key autoincrement,"
                        + DICT_OFFSET_COLUMN_NAME + " long,"
                        + DICT_CHUNK_SIZE_COLUMN_NAME + " long,"
                        + WORD_COLUMN_NAME + " text" + ");");
                break;
        }

    }

    public boolean deleteWord(String word) {
        word = StringUtils.prepareForDatabaseQuery(word);
        Log.d(LOG_TAG, "delete word = " + word);
        db = this.getWritableDatabase();
        int id = this.getId(word);
        db.delete(currentDBName, WORD_COLUMN_NAME + "=?", new String[] { word });
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id + NotificationBuilder.idModificator);
        return true;
    }

    public static void updateDBName(Context context, SharedPreferences sp) {
        String selectedLanguageFrom = sp.getString(context.getString(R.string.key_language_from), "NONE");
        String selectedLanguageTo = sp.getString(context.getString(R.string.key_language_to), "NONE");
        Resources res = context.getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        String currentLanguage;
        if (allLanguages.contains(selectedLanguageTo)) {
            currentLanguage = selectedLanguageTo;
        } else {
            currentLanguage = Locale.getDefault().getLanguage();
        }
        DBHelper.DB_WORDS = "myDB" + selectedLanguageFrom + currentLanguage;

    }

    boolean isPrefix(String word) {
        String prefix = this.mContext.getString(R.string.help_words_de);
        return prefix.contains(word.toLowerCase());
    }

    public int checkEmptyString(String str) {
        if ("".equals(str)) {
            return EXIT_CODE_EMPTY_INPUT;
        } else
            return EXIT_CODE_OK;
    }

    private String cutAwayFirstWord(String input) {
        return input.split("\\s", 2)[1];
    }

    public int writeToDB(String word, String translation) {
        try {
            word = StringUtils.prepareForDatabaseQuery(word);
            translation = StringUtils.prepareForDatabaseQuery(translation);
            List<String> wordsList = Arrays.asList(word.split("\\s"));
            ContentValues cv = new ContentValues();
            switch (wordsList.size()) {
                case 0:
                    return EXIT_CODE_EMPTY_INPUT;
                case 1:
                    if (word.isEmpty()) {
                        return EXIT_CODE_EMPTY_INPUT;
                    }
                    cv.put(WORD_COLUMN_NAME, word);
                    cv.put(ARTICLE_COLUMN_NAME, (String) null);
                    cv.put(PREFIX_COLUMN_NAME, (String) null);
                    break;
                default:
                    if (StringUtils.isArticle(mContext, wordsList.get(0))) {
                        cv.put(WORD_COLUMN_NAME, cutAwayFirstWord(word));
                        cv.put(ARTICLE_COLUMN_NAME, wordsList.get(0));
                        cv.put(PREFIX_COLUMN_NAME, (String) null);
                    } else if (isPrefix(wordsList.get(0))) {
                        cv.put(WORD_COLUMN_NAME, cutAwayFirstWord(word));
                        cv.put(ARTICLE_COLUMN_NAME, (String) null);
                        cv.put(PREFIX_COLUMN_NAME, wordsList.get(0));
                    } else {
                        cv.put(WORD_COLUMN_NAME, word);
                        cv.put(ARTICLE_COLUMN_NAME, (String) null);
                        cv.put(PREFIX_COLUMN_NAME, (String) null);
                    }
                    break;
            }
            long key = -1;
            boolean updatedFlag = false;
            db = this.getWritableDatabase();
            Cursor c = db.query(currentDBName,
                    new String[]{ID_COLUMN_NAME, ARTICLE_COLUMN_NAME,
                            WORD_COLUMN_NAME, TRANSLATION_COLUMN_NAME},
                    WORD_COLUMN_NAME + " like ?",
                    new String[] { cv.getAsString(WORD_COLUMN_NAME) },
                    null, null, null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
                    int translationColIndex = c.getColumnIndex(TRANSLATION_COLUMN_NAME);
                    int idColIndex = c.getColumnIndex(ID_COLUMN_NAME);
                    String tempWord = (c.getString(wordColIndex));
                    String trans = (c.getString(translationColIndex));
                    List<String> transInDBList = Arrays.asList(trans.split(", "));
                    List<String> inputTransList = Arrays.asList(translation.split(", "));
                    String tempTranslation = trans;
                    boolean anyNewValue = false;
                    for (String anInputTransList : inputTransList) {
                        if (transInDBList.contains(anInputTransList)) {
                            Log.d(LOG_TAG, "translation already in table... "
                                    + anInputTransList);
                        } else {
                            tempTranslation += (", " + anInputTransList);
                            anyNewValue = true;
                        }
                    }
                    translation = tempTranslation;
                    if (!anyNewValue) {
                        c.close();
                        db.close();
                        return EXIT_CODE_WORD_ALREADY_IN_DB;
                    }
                    key = c.getLong(idColIndex);
                    updatedFlag = true;
                    Log.d(LOG_TAG, "word already in table = " + tempWord
                            + " translations = " + translation);

                }
            }
            cv.put(TRANSLATION_COLUMN_NAME, translation);
            cv.put(WEIGHT_COLUMN_NAME, WEIGHT_NEW);
            if (!updatedFlag) {
                long rowID = db.insert(currentDBName, null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID + " rows total = "
                        + maxId);
                c.close();
                db.close();
                return EXIT_CODE_OK;
            } else {
                long rowID = db.update(currentDBName, cv,
                        String.format("%s = ?", ID_COLUMN_NAME),
                        new String[]{String.valueOf(key)});
                Log.d(LOG_TAG, "row updated, ID = " + rowID + " rows total = "
                        + maxId);
                c.close();
                db.close();
                return EXIT_CODE_WORD_UPDATED;
            }
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "got exception " + e.toString());
            return EXIT_CODE_EMPTY_INPUT;
        }

    }

    public boolean updateWordWeight(String word, int newWeight) {
        try {
            word = StringUtils.prepareForDatabaseQuery(word);
            ContentValues valuesToUpdate = new ContentValues();
            valuesToUpdate.put(WEIGHT_COLUMN_NAME, newWeight);
            String whereClause = WORD_COLUMN_NAME + "=?";
            String[] whereArgs = new String[] { word };
            db = this.getWritableDatabase();
            db.update(currentDBName, valuesToUpdate, whereClause, whereArgs);
            db.close();
            Log.d(LOG_TAG, "word " + word + " updated weight to " + newWeight);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "error in update weight in DbHelper class");
            return false;
        }
    }

    public ArrayList<ArticleWordId> getRandomWords(int numOfWords, String omitWord, int noun) {
        db = this.getReadableDatabase();
        omitWord = StringUtils.prepareForDatabaseQuery(omitWord);
        ArrayList<ArticleWordId> structArray = new ArrayList<ArticleWordId>();
        Log.d(LOG_TAG, "trying to get " + numOfWords + " random words != '" + omitWord + "' and isnoun = " + noun + " from " + currentDBName);
        String[] columns = new String[]{
                ID_COLUMN_NAME,
                WORD_COLUMN_NAME,
                TRANSLATION_COLUMN_NAME,
                ARTICLE_COLUMN_NAME,
                PREFIX_COLUMN_NAME,
                WEIGHT_COLUMN_NAME};
        String queryMixed = WORD_COLUMN_NAME + " != ?";
        String queryNouns = ARTICLE_COLUMN_NAME + " is not null and " + queryMixed;
        String queryNotNouns = ARTICLE_COLUMN_NAME + " is null and " + queryMixed;
        String[] queryParameters = new String[] { omitWord };
        String orderBy = WEIGHT_COLUMN_NAME + "*random() desc";
        String limit = String.valueOf(numOfWords);
        String chosenQuery;
        Cursor c;
        switch (noun) {
            case Constants.MIXED:
                chosenQuery = queryMixed;
                break;
            case Constants.NOT_NOUNS:
                chosenQuery = queryNotNouns;
                break;
            case Constants.ONLY_NOUNS:
                chosenQuery = queryNouns;
                break;
            default:
                chosenQuery = queryMixed;
        }
        c = db.query(currentDBName, columns, chosenQuery, queryParameters, null, null, orderBy, limit);
        String word, translation;
        String article;
        int id;
        String prefix;
        if (c.moveToFirst()) {
            int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
            int transColIndex = c.getColumnIndex(TRANSLATION_COLUMN_NAME);
            int idColIndex = c.getColumnIndex(ID_COLUMN_NAME);
            int articleColIndex = c.getColumnIndex(ARTICLE_COLUMN_NAME);
            int prefixColIndex = c.getColumnIndex(PREFIX_COLUMN_NAME);
            do {
                word = (c.getString(wordColIndex));
                translation = (c.getString(transColIndex));
                article = (c.getString(articleColIndex));
                id = (c.getInt(idColIndex));
                prefix = (c.getString(prefixColIndex));
                structArray.add(new ArticleWordId(article, prefix, word, translation, id));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        db.close();
        return structArray;
    }

    private String capitalize(String str) {
        if (str.length() > 0)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        else
            return null;
    }

    public boolean exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            sd = new File(sd, "LearnIt");
            sd.mkdirs();
            Log.d(LOG_TAG, "searching file in " + sd.getPath());
            if (sd.canWrite()) {
                String backupDBPath = "DB_Backup.db";
                File currentDB = mContext.getDatabasePath(currentDBName);
                Log.d(LOG_TAG, "current db path = " + currentDB.getPath());
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    Log.d(LOG_TAG, "db not exist");
                }

                Log.d(LOG_TAG, "db exported to " + backupDB.getPath());
                Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.toast_db_exported), backupDB.getPath()), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            return true;
        } catch (Exception e) {
            Log.d(LOG_TAG, "export failed - " + e.getMessage());
            return false;
        }
    }

    public boolean importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            sd = new File(sd, "LearnIt");
            String backupDBPath = "DB_Backup.db";
            File dbfile = new File(sd, backupDBPath);
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            Log.d(LOG_TAG, "Its open? " + db.isOpen());
            Cursor c_name = db.rawQuery("SELECT name FROM sqlite_sequence", null);
            String name = null;
            if (c_name.moveToFirst()) {
                int name_index = c_name.getColumnIndex("name");
                name = c_name.getString(name_index);
                Log.d(LOG_TAG, name);
            }
            c_name.close();
            Cursor c = db.rawQuery("select * from " + name, null);
            SQLiteDatabase db_local = getWritableDatabase();
            if (c.moveToFirst()) {
                int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
                int articleColIndex = c.getColumnIndex(ARTICLE_COLUMN_NAME);
                int prefixColIndex = c.getColumnIndex(PREFIX_COLUMN_NAME);
                int translationColIndex = c.getColumnIndex(TRANSLATION_COLUMN_NAME);
                int weight = 100;
                do {
                    String word = c.getString(wordColIndex);
                    String article = c.getString(articleColIndex);
                    String prefix = c.getString(prefixColIndex);
                    String translation = c.getString(translationColIndex);
                    ContentValues cv = new ContentValues();
                    cv.put(WORD_COLUMN_NAME, word);
                    cv.put(ARTICLE_COLUMN_NAME, article);
                    cv.put(PREFIX_COLUMN_NAME, prefix);
                    cv.put(TRANSLATION_COLUMN_NAME, translation);
                    cv.put(WEIGHT_COLUMN_NAME, weight);
                    if (db_local.query(currentDBName,
                            new String[]{TRANSLATION_COLUMN_NAME},
                            WORD_COLUMN_NAME + " like ? ",
                            new String[] { word }, null, null,
                            null, null).getCount() == 0) {
                        db_local.insert(currentDBName, null, cv);
                    }
                } while (c.moveToNext());
            }
            db_local.close();
            db.close();
            Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.toast_db_imported), dbfile.getPath()), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return true;

        } catch (Exception e) {
            Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.toast_db_import_error)), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            e.printStackTrace();
            return false;
        }
    }


    public String getTranslation(String word) {
        word = StringUtils.prepareForDatabaseQuery(word);
        db = this.getReadableDatabase();
        Cursor c = db.query(currentDBName,
                new String[]{TRANSLATION_COLUMN_NAME, WORD_COLUMN_NAME},
                WORD_COLUMN_NAME + " like ? ",
                new String[] { word }, null, null,
                null, null);
        if (c.moveToFirst()) {
            int translationColIndex = c.getColumnIndex(TRANSLATION_COLUMN_NAME);
            String trans = c.getString(translationColIndex);
            if (trans != null) {
                c.close();
                return trans;
            }
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        db.close();
        return null;
    }

    public int getId(String word) {
        word = StringUtils.prepareForDatabaseQuery(word);
        db = this.getReadableDatabase();
        Cursor c = db.query(currentDBName,
                new String[]{ID_COLUMN_NAME, WORD_COLUMN_NAME},
                WORD_COLUMN_NAME + " like ? ",
                new String[] { word }, null, null,
                null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(ID_COLUMN_NAME);
            int id = c.getInt(idColIndex);
            if (id != 0) {
                c.close();
                return id;
            }
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        db.close();
        return 0;
    }

    public List<Map<String, String>> getWords(String word) {
	    if (word==null) word = "";
        word = StringUtils.prepareForDatabaseQuery(word);
        db = this.getReadableDatabase();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Cursor c = db.query(currentDBName,
                new String[]{ID_COLUMN_NAME, ARTICLE_COLUMN_NAME,
                        WORD_COLUMN_NAME, TRANSLATION_COLUMN_NAME,
                        PREFIX_COLUMN_NAME, WEIGHT_COLUMN_NAME},
                WORD_COLUMN_NAME + " like ? or " + TRANSLATION_COLUMN_NAME + " like ?",
                new String[] { "%" + word + "%",  "%" + word + "%" }, null, null,
                WORD_COLUMN_NAME, null);
        String tempWord;
        String tempTrans;
        if (c.moveToFirst()) {
            int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
            int articleColIndex = c.getColumnIndex(ARTICLE_COLUMN_NAME);
            int prefixColIndex = c.getColumnIndex(PREFIX_COLUMN_NAME);
            int translationColIndex = c.getColumnIndex(TRANSLATION_COLUMN_NAME);
            do {
                tempWord = c.getString(wordColIndex);
                tempTrans = c.getString(translationColIndex);
                if (null != (c.getString(articleColIndex))) {
                    tempWord = capitalize(tempWord);
                    tempWord = String.format("%s %s", c.getString(articleColIndex), tempWord);
                }
                if (null != (c.getString(prefixColIndex))) {
                    tempWord = String.format("%s %s", c.getString(prefixColIndex), tempWord);
                }
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("word", tempWord);
                datum.put("translation", tempTrans);
                data.add(datum);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        db.close();
        return data;
    }

    public List<String> getHelpWords(String word) {
        if (!currentDBName.equals(DB_DICT_FROM)) {
            return null;
        }
	    Log.d(LOG_TAG, "getHelpWords word is " + word);
	    if (word == null) return null;
        word = StringUtils.prepareForDatabaseQuery(word);
        db = this.getReadableDatabase();
        List<String> data = new ArrayList<String>();
        Cursor c = db.query(currentDBName,
                new String[]{WORD_COLUMN_NAME, DICT_OFFSET_COLUMN_NAME,
                        DICT_CHUNK_SIZE_COLUMN_NAME},
                WORD_COLUMN_NAME + " like ?", new String[] { word + "%" }, null, null,
                WORD_COLUMN_NAME, "20");
        String tempWord;
        if (c.moveToFirst()) {
            int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
            do {
                tempWord = c.getString(wordColIndex);
                data.add(tempWord);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        db.close();
        return data;
    }

    public Pair<Long, Long> getDictOffsetAndSize(String word) {
        if (!currentDBName.equals(DB_DICT_FROM)) {
            return null;
        }
        word = StringUtils.prepareForDatabaseQuery(word);
        db = this.getReadableDatabase();
        Cursor c = db.query(currentDBName,
                new String[]{WORD_COLUMN_NAME, DICT_OFFSET_COLUMN_NAME,
                        DICT_CHUNK_SIZE_COLUMN_NAME},
                WORD_COLUMN_NAME + " like ?", new String[] { word }, null, null,
                null, null);
        Long offset, size;
        Pair<Long, Long> pair = null;
        if (c.moveToFirst()) {
            int offsetColIndex = c.getColumnIndex(DICT_OFFSET_COLUMN_NAME);
            int sizeColIndex = c.getColumnIndex(DICT_CHUNK_SIZE_COLUMN_NAME);
            do {
                offset = c.getLong(offsetColIndex);
                size = c.getLong(sizeColIndex);
                pair = new Pair<>(offset, size);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        db.close();
        return pair;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
