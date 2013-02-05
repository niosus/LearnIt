/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper{
    final static int DB_VERSION = 1;
    final String LOG_TAG = "my_logs";
    final static String DB_NAME = "myDB";
    final String WORD_COLUMN_NAME = "word";
    final String ID_COLUMN_NAME = "id";
    final String ARTICLE_COLUMN_NAME = "article";
    final String WEIGHT_COLUMN_NAME = "weight";
    final String PREFIX_COLUMN_NAME = "prefix";
    final String TRANSLATION_COLUMN_NAME = "translation";

    public static final int EXIT_CODE_EMPTY_INPUT = -10;
    public static final int EXIT_CODE_WORD_ALREADY_IN_DB = -11;
    public static final int EXIT_CODE_WORD_UPDATED = 1;
    public static final int EXIT_CODE_OK = 0;
    public static final int EXIT_CODE_WRONG_ARTICLE = -12;
    public static final int EXIT_CODE_WRONG_FORMAT = -13;

    public static final int WEIGHT_NEW=10;

    public static final int WEIGHT_ONE_WRONG=10;
    public static final int WEIGHT_TWO_WRONG=12;
    public static final int WEIGHT_THREE_WRONG=15;

    public static final int WEIGHT_CORRECT_BUTTON=5;
    public static final int WEIGHT_NO_MORE_LEARNING=0;
    public static final int WEIGHT_CORRECT_INPUT=1;

    public static final int[] WEIGHTS = {
            WEIGHT_NO_MORE_LEARNING,
            WEIGHT_CORRECT_INPUT,
            WEIGHT_CORRECT_BUTTON,
            WEIGHT_NEW,
            WEIGHT_ONE_WRONG,
            WEIGHT_TWO_WRONG,
            WEIGHT_THREE_WRONG};

    long maxId = 0;

    private Context mContext;

    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        db.execSQL("CREATE TABLE " + DB_NAME + " ("
                + ID_COLUMN_NAME + " integer primary key autoincrement,"
                + ARTICLE_COLUMN_NAME + " text,"
                + WORD_COLUMN_NAME + " text,"
                + TRANSLATION_COLUMN_NAME + " text,"
                + WEIGHT_COLUMN_NAME + " integer,"
                + PREFIX_COLUMN_NAME + " text"+ ");");
    }

    public boolean deleteWord(String word)
    {
        word = word.toLowerCase();
        Log.d(LOG_TAG,"delete word = " + word);
        db = this.getWritableDatabase();
        db.delete(DB_NAME,WORD_COLUMN_NAME + "='" + word + "'", null);
        return true;
    }

    public long getDBSize(boolean noun) {
        db = this.getReadableDatabase();
        Cursor cursor;
        if (noun)
        {
            cursor = db.rawQuery("select * from " + DB_NAME + " where " + ARTICLE_COLUMN_NAME + " is not null", null);
        }
        else
        {
            cursor = db.rawQuery("select * from " + DB_NAME, null);
        }
        return cursor.getCount();
    }

    public long getDBWeightSize(boolean noun, int weight) {
        db = this.getReadableDatabase();
        Cursor cursor;
        if (noun)
        {
            cursor = db.rawQuery("select * from " + DB_NAME + " where " + ARTICLE_COLUMN_NAME + " is not null and "+WEIGHT_COLUMN_NAME+"=="+weight, null);
        }
        else
        {
            cursor = db.rawQuery("select * from " + DB_NAME + " where "+WEIGHT_COLUMN_NAME+"=="+weight, null);
        }
        return cursor.getCount();
    }

    public Cursor getRandRow(boolean noun, int weight)
    {
        db = this.getReadableDatabase();
        Cursor temp = null;
        if (!noun)
        {
            temp = db.rawQuery("select * from " + DB_NAME + " where "+WEIGHT_COLUMN_NAME+"=="+weight+" order by random() limit 1", null);
            if (0!=temp.getCount())
            {
                return temp;
            }
            else
            {
                return  db.rawQuery("select * from " + DB_NAME + " order by random() limit 1", null);
            }
        }
        else
        {
            temp = db.rawQuery("select * from " + DB_NAME + " where " + ARTICLE_COLUMN_NAME + " is not null and "+WEIGHT_COLUMN_NAME+"=="+weight+" order by random() limit 1", null);
            if (0!=temp.getCount())
            {
                return temp;
            }
            else
            {
                return  db.rawQuery("select * from " + DB_NAME + " where " + ARTICLE_COLUMN_NAME + " is not null order by random() limit 1", null);
            }
        }
    }

    boolean isArticle(String article) {
        String articles = this.mContext.getString(R.string.articles_de);
        return articles.contains(article.toLowerCase());
    }

    boolean isPrefix(String word) {
        String prefix = this.mContext.getString(R.string.help_words_de);
        return prefix.contains(word.toLowerCase());
    }

    public int checkEmptyString(String str)
    {
        if ("".equals(str))
        {
            return EXIT_CODE_EMPTY_INPUT;
        }
        else
            return EXIT_CODE_OK;
    }

    private String cutAwayFirstWord(String input)
    {
        return input.split(" ", 2)[1];
    }



    public int writeToDB(String word, String translation) {
        try {
            word=word.toLowerCase();
            translation = translation.toLowerCase();
            List<String> wordsList = Arrays.asList(word.split(" "));
            ContentValues cv = new ContentValues();
            switch (wordsList.size()) {
                case 0:
                    return EXIT_CODE_EMPTY_INPUT;
                case 1:
                    if (word.isEmpty()) {
                        return EXIT_CODE_EMPTY_INPUT;
                    }
                    cv.put(WORD_COLUMN_NAME, word);
                    cv.put(ARTICLE_COLUMN_NAME, (String)null);
                    cv.put(PREFIX_COLUMN_NAME, (String)null);
                    break;
                default:
                    if (isArticle(wordsList.get(0))) {
                        cv.put(WORD_COLUMN_NAME, cutAwayFirstWord(word));
                        cv.put(ARTICLE_COLUMN_NAME, wordsList.get(0));
                        cv.put(PREFIX_COLUMN_NAME, (String)null);
                    }
                    else
                    if (isPrefix(wordsList.get(0)))
                    {
                        cv.put(WORD_COLUMN_NAME, cutAwayFirstWord(word));
                        cv.put(ARTICLE_COLUMN_NAME, (String)null);
                        cv.put(PREFIX_COLUMN_NAME, wordsList.get(0));
                    }
                    else
                    {
                        cv.put(WORD_COLUMN_NAME, word);
                        cv.put(ARTICLE_COLUMN_NAME, (String)null);
                        cv.put(PREFIX_COLUMN_NAME, (String)null);
                    }
                    break;
            }
            long key = -1;
            boolean updatedFlag = false;
            db = this.getWritableDatabase();
            Cursor c = db.query(DB_NAME, new String[]{ID_COLUMN_NAME,
                    ARTICLE_COLUMN_NAME, WORD_COLUMN_NAME,
                    TRANSLATION_COLUMN_NAME}, WORD_COLUMN_NAME + " like "
                    + "'" + cv.getAsString(WORD_COLUMN_NAME) + "'", null, null,
                    null, null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
                    int translationColIndex = c
                            .getColumnIndex(TRANSLATION_COLUMN_NAME);
                    int idColIndex = c.getColumnIndex(ID_COLUMN_NAME);
                    String tempWord = (c.getString(wordColIndex));
                    String trans = (c.getString(translationColIndex));
                    List<String> transInDBList = Arrays.asList(trans
                            .split(", "));
                    List<String> inputTransList = Arrays.asList(translation
                            .split(", "));
                    String tempTranslation = trans;
                    boolean anyNewValue = false;
                    for (int i = 0; i < inputTransList.size(); ++i) {
                        if (transInDBList.contains(inputTransList.get(i))) {
                            Log.d(LOG_TAG, "translation already in table... "
                                    + inputTransList.get(i));
                        } else {
                            tempTranslation += (", " + inputTransList.get(i));
                            anyNewValue = true;
                        }
                    }
                    translation = tempTranslation;
                    if (!anyNewValue) {
                        c.close();
                        return EXIT_CODE_WORD_ALREADY_IN_DB;
                    }
                    key = c.getLong(idColIndex);
                    updatedFlag = true;
                    Log.d(LOG_TAG, "word already in table = " + tempWord
                            + " translations = " + translation);

                }
            }
            cv.put(TRANSLATION_COLUMN_NAME, translation);
            cv.put(WEIGHT_COLUMN_NAME,WEIGHT_NEW);
            if (!updatedFlag) {
                long rowID = db.insert(DB_NAME, null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID + " rows total = "
                        + maxId);
                c.close();
                return EXIT_CODE_OK;
            } else {
                long rowID = db.update(DB_NAME, cv,
                        String.format("%s = ?", ID_COLUMN_NAME),
                        new String[]{"" + key});
                Log.d(LOG_TAG, "row updated, ID = " + rowID + " rows total = "
                        + maxId);
                c.close();
                return EXIT_CODE_WORD_UPDATED;
            }
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "got exception " + e.toString());
            return EXIT_CODE_EMPTY_INPUT;
        }

    }

    private boolean wordInArray(String word,  List<String> array)
    {
        for (String a:array)
        {
            if (word.toLowerCase().equals(a.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean updateWordWeight(String word, int newWeight)
    {
        try{
            db = this.getWritableDatabase();
            Cursor c = db.rawQuery("UPDATE " + DB_NAME + " SET " + WEIGHT_COLUMN_NAME + "=" + newWeight + " WHERE " + WORD_COLUMN_NAME + "='" + word + "'", null);
            c.moveToFirst();
            c.close();
            Log.d(LOG_TAG, "word " + word + " updated weight to " + newWeight);
            return true;
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"error in update weight in DbHelper class");
            return false;
        }
    }

    public ArticleWordIdStruct getRandomWord(ArrayList<String> usedWords, boolean noun, int weight) {
        maxId = getDBSize(noun);
        if (usedWords.size()>=maxId)
        {
            return null;
        }
        String word = null;
        String prefix = null;
        String article = null;
        Cursor c;
        int id = 0;
        do {
            if (usedWords.size()>=getDBWeightSize(noun,weight))
            {
                weight = DBHelper.WEIGHT_NEW;
            }
            c = getRandRow(noun, weight);
            if (c.moveToFirst()) {
                int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
                int articleColIndex = c.getColumnIndex(ARTICLE_COLUMN_NAME);
                int idColIndex = c.getColumnIndex(ID_COLUMN_NAME);
                int prefixColIndex = c.getColumnIndex(PREFIX_COLUMN_NAME);
                word = (c.getString(wordColIndex));
                article = (c.getString(articleColIndex));
                id = (c.getInt(idColIndex));
                prefix = (c.getString(prefixColIndex));
                Log.d(LOG_TAG, "randWord = " + article + " " + word);
                c.close();
            } else {
                Log.d(LOG_TAG, "0 rows");
                return null;
            }
            Log.d(LOG_TAG, "word " + word + " is in array " + usedWords.toString() + " = " + wordInArray(word,usedWords));
        }
        while (wordInArray(word,usedWords));
        if (noun)
        {
            //TODO only in German
            word=capitalize(word);
        }
        ArticleWordIdStruct result = new ArticleWordIdStruct(article, prefix, word, id);
        return result;
    }

    private String capitalize(String str)
    {
        if (str.length()>0)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        else
            return null;
    }

    public boolean exportDB()
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            sd = new File(sd, "LearnIt");
            sd.mkdirs();
            Log.d(LOG_TAG,"searching file in " + sd.getPath());
            if (sd.canWrite()) {
                String backupDBPath = "DB_Backup.db";
                File currentDB = mContext.getDatabasePath(DBHelper.DB_NAME);
                Log.d(LOG_TAG, "current db path = "+currentDB.getPath());
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                else
                {
                    Log.d(LOG_TAG,"db not exist");
                }

                Log.d(LOG_TAG,"db exported to " + backupDB.getPath());
                Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.db_exported),backupDB.getPath()), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            return true;
        } catch (Exception e) {
            Log.d(LOG_TAG, "export failed - " + e.getMessage());
            return false;
        }
    }

    public boolean importDB()
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            sd = new File(sd, "LearnIt");
            if (sd.canRead()) {
                String backupDBPath = "DB_Backup.db";
                File currentDB = mContext.getDatabasePath(DBHelper.DB_NAME);
                Log.d(LOG_TAG, "current db path = "+currentDB.getPath());
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel dst = new FileInputStream(currentDB).getChannel();
                    FileChannel src = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                else
                {
                    Log.d(LOG_TAG,"db not exist");
                }

                Log.d(LOG_TAG,"db imported from " + backupDB.getPath());
                Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.db_exported),backupDB.getPath()), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            return true;
        } catch (Exception e) {
            Log.d(LOG_TAG, "import failed - " + e.getMessage());
            return false;
        }
    }




    public String getTranslation(String word) {
        word=word.toLowerCase();
        db = this.getReadableDatabase();
        Cursor c = db.query(DB_NAME,
                new String[]{TRANSLATION_COLUMN_NAME, WORD_COLUMN_NAME},
                WORD_COLUMN_NAME + " like " + "'%" + word + "%'", null, null,
                null, null);
        if (c.moveToFirst()) {
            int translationColIndex = c.getColumnIndex(TRANSLATION_COLUMN_NAME);
            String trans = c.getString(translationColIndex);
            if (trans!=null)
            {
                c.close();
                return trans;
            }
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return null;
    }

    public int getId(String word) {
        word=word.toLowerCase();
        db = this.getReadableDatabase();
        Cursor c = db.query(DB_NAME,
                new String[]{ID_COLUMN_NAME, WORD_COLUMN_NAME},
                WORD_COLUMN_NAME + " like " + "'%" + word + "%'", null, null,
                null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(ID_COLUMN_NAME);
            int id = c.getInt(idColIndex);
            if (id!=0)
            {
                c.close();
                return id;
            }
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return 0;
    }

    public ArrayList<String> getWords(String word) {
        word=word.toLowerCase();
        db = this.getWritableDatabase();
        ArrayList<String> listItems = new ArrayList<String>();
        Cursor c = db.query(DB_NAME,
                new String[]{ID_COLUMN_NAME, ARTICLE_COLUMN_NAME,
                        WORD_COLUMN_NAME, TRANSLATION_COLUMN_NAME,
                        PREFIX_COLUMN_NAME, WEIGHT_COLUMN_NAME},
                WORD_COLUMN_NAME + " like " + "'%" + word + "%'", null, null,
                null, WORD_COLUMN_NAME);
        String tempWord;
        if (c.moveToFirst()) {
            int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
            int articleColIndex = c.getColumnIndex(ARTICLE_COLUMN_NAME);
            int prefixColIndex = c.getColumnIndex(PREFIX_COLUMN_NAME);
            do {
                tempWord = c.getString(wordColIndex);
                if (null!=(c.getString(articleColIndex)))
                {
                    tempWord = capitalize(tempWord);
                    tempWord = String.format("%s %s", c.getString(articleColIndex),tempWord);
                }
                if (null!=(c.getString(prefixColIndex)))
                {
                    tempWord = String.format("%s %s", c.getString(prefixColIndex),tempWord);
                }
                listItems.add(tempWord);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return listItems;
    }

    public ArrayList<String> getAllWords() {
        db = this.getReadableDatabase();
        ArrayList<String> listItems = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " ORDER BY "+ WORD_COLUMN_NAME, null);
        String tempWord;
        if (c.moveToFirst()) {
            int wordColIndex = c.getColumnIndex(WORD_COLUMN_NAME);
            int articleColIndex = c.getColumnIndex(ARTICLE_COLUMN_NAME);
            int prefixColIndex = c.getColumnIndex(PREFIX_COLUMN_NAME);
            do {
                tempWord = c.getString(wordColIndex);
                if (null!=(c.getString(articleColIndex)))
                {
                    //TODO capitalize only in German
                    tempWord = capitalize(tempWord);
                    tempWord = String.format("%s %s", c.getString(articleColIndex),tempWord);
                }
                if (null!=(c.getString(prefixColIndex)))
                {
                    tempWord = String.format("%s %s", c.getString(prefixColIndex),tempWord);
                }
                listItems.add(tempWord);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return listItems;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
