/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.*;

public class DBHelper extends SQLiteOpenHelper{
    final static int DB_VERSION = 1;
    public static final String LOG_TAG = "my_logs";
    public static String DB_WORDS = "myDB"; //gets changed when the languages are updated
    final static String DB_DICT_FROM = "dictFROM";
    final String WORD_COLUMN_NAME = "word";
    final String ID_COLUMN_NAME = "id";
    final String ARTICLE_COLUMN_NAME = "article";
    final String WEIGHT_COLUMN_NAME = "weight";
    final String PREFIX_COLUMN_NAME = "prefix";
    final String TRANSLATION_COLUMN_NAME = "translation";


    final String DICT_OFFSET_COLUMN_NAME = "start_offset";
    final String DICT_CHUNK_SIZE_COLUMN_NAME = "end_offset";

    String currentDBName;

    public static final int EXIT_CODE_EMPTY_INPUT = -10;
    public static final int EXIT_CODE_WORD_ALREADY_IN_DB = -11;
    public static final int EXIT_CODE_WORD_UPDATED = 1;
    public static final int EXIT_CODE_OK = 0;
    public static final int EXIT_CODE_WRONG_ARTICLE = -12;
    public static final int EXIT_CODE_WRONG_FORMAT = -13;

    public static final int WEIGHT_NEW=100;

    public static final int WEIGHT_ONE_WRONG=100;
    public static final int WEIGHT_TWO_WRONG=1000;
    public static final int WEIGHT_THREE_WRONG=10000;

    public static final int WEIGHT_CORRECT_BUTTON=10;
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

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
        currentDBName=dbName;
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database "+currentDBName+"---");
        switch (currentDBName.toCharArray()[0])
        {
            case 'm':
                db.execSQL("CREATE TABLE " + currentDBName + " ("
                        + ID_COLUMN_NAME + " integer primary key autoincrement,"
                        + ARTICLE_COLUMN_NAME + " text,"
                        + WORD_COLUMN_NAME + " text,"
                        + TRANSLATION_COLUMN_NAME + " text,"
                        + WEIGHT_COLUMN_NAME + " integer,"
                        + PREFIX_COLUMN_NAME + " text"+ ");");
                break;
            case 'd':
                db.execSQL("CREATE TABLE " + currentDBName + " ("
                        + ID_COLUMN_NAME + " integer primary key autoincrement,"
                        + DICT_OFFSET_COLUMN_NAME + " long,"
                        + DICT_CHUNK_SIZE_COLUMN_NAME + " long,"
                        + WORD_COLUMN_NAME + " text"+ ");");
                break;
        }

    }

    public boolean deleteWord(String word)
    {
        word = word.toLowerCase();
        Log.d(LOG_TAG,"delete word = " + word);
        db = this.getWritableDatabase();
        int id = this.getId(word);
        db.delete(currentDBName,WORD_COLUMN_NAME + "='" + word + "'", null);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id + MyAlarmService.idModificator);
        return true;
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
            Cursor c = db.query(currentDBName, new String[]{ID_COLUMN_NAME,
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
                long rowID = db.insert(currentDBName, null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID + " rows total = "
                        + maxId);
                c.close();
                return EXIT_CODE_OK;
            } else {
                long rowID = db.update(currentDBName, cv,
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

    public boolean updateWordWeight(String word, int newWeight)
    {
        try{
            db = this.getWritableDatabase();
            Cursor c = db.rawQuery("UPDATE " + currentDBName + " SET " + WEIGHT_COLUMN_NAME + "=" + newWeight + " WHERE " + WORD_COLUMN_NAME + "='" + word + "'", null);
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

    public ArrayList<ArticleWordIdStruct> getRandomWords(int numOfWords, String ommitWord, boolean noun) {
        db = this.getReadableDatabase();
        ArrayList<ArticleWordIdStruct> structArray = new ArrayList<ArticleWordIdStruct>();
        Cursor c;
        if (!noun)
        {
            c=db.rawQuery("select * from "+currentDBName+" where "+WORD_COLUMN_NAME+"!='"+ommitWord+"' order by "+WEIGHT_COLUMN_NAME+"*random() desc limit "+numOfWords,null);
        }
        else
        {
            c=db.rawQuery("select * from "+currentDBName+" where "+ARTICLE_COLUMN_NAME+" is not null and " +WORD_COLUMN_NAME+"!='"+ommitWord+"' order by "+WEIGHT_COLUMN_NAME+"*random() desc limit "+numOfWords,null);
        }
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
                structArray.add(new ArticleWordIdStruct(article,prefix,word,translation,id));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return structArray;
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
                File currentDB = mContext.getDatabasePath(currentDBName);
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
            String backupDBPath = "DB_Backup.db";
            File dbfile = new File(sd, backupDBPath);
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            Log.d(LOG_TAG,"Its open? "  + db.isOpen());
            Cursor c_name = db.rawQuery("SELECT name FROM sqlite_sequence", null);
            String name=null;
            if (c_name.moveToFirst()) {
                int name_index = c_name.getColumnIndex("name");
                name = c_name.getString(name_index);
                Log.d(LOG_TAG,name);
            }
            c_name.close();
            Cursor c = db.rawQuery("select * from "+ name, null);
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
                            WORD_COLUMN_NAME + " like " + "'" + word + "'", null, null,
                            null, null).getCount()==0)
                    {
                        db_local.insert(currentDBName, null, cv);
                    }
                } while(c.moveToNext());
            }
            Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.db_imported),dbfile.getPath()), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return true;

        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(mContext, String.format(mContext.getString(R.string.db_import_error)), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            e.printStackTrace();
            return false;
        }
    }




    public String getTranslation(String word) {
        word=word.toLowerCase();
        db = this.getReadableDatabase();
        Cursor c = db.query(currentDBName,
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
        Cursor c = db.query(currentDBName,
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

    public List<Map<String, String>> getWords(String word) {
        word=word.toLowerCase();
        db = this.getReadableDatabase();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Cursor c = db.query(currentDBName,
                new String[]{ID_COLUMN_NAME, ARTICLE_COLUMN_NAME,
                        WORD_COLUMN_NAME, TRANSLATION_COLUMN_NAME,
                        PREFIX_COLUMN_NAME, WEIGHT_COLUMN_NAME},
                WORD_COLUMN_NAME + " like '%" + word + "%' or " + TRANSLATION_COLUMN_NAME + " like '%"+word+"%'", null, null,
                null, WORD_COLUMN_NAME);
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
                if (null!=(c.getString(articleColIndex)))
                {
                    tempWord = capitalize(tempWord);
                    tempWord = String.format("%s %s", c.getString(articleColIndex),tempWord);
                }
                if (null!=(c.getString(prefixColIndex)))
                {
                    tempWord = String.format("%s %s", c.getString(prefixColIndex),tempWord);
                }
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("word", tempWord);
                datum.put("translation",tempTrans);
                data.add(datum);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return data;
    }

    public List<String> getHelpWords(String word) {
        if (currentDBName!=DB_DICT_FROM)
        {
            return null;
        }
        word=word.toLowerCase();
        db = this.getReadableDatabase();
        List<String> data = new ArrayList<String>();
        Cursor c = db.query(currentDBName,
                new String[]{WORD_COLUMN_NAME, DICT_OFFSET_COLUMN_NAME,
                        DICT_CHUNK_SIZE_COLUMN_NAME},
                WORD_COLUMN_NAME + " like '" + word + "%' limit 20", null, null,
                null, null);
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
        return data;
    }

    public Pair<Long,Long> getDictOffsetAndSize(String word) {
        if (currentDBName!=DB_DICT_FROM)
        {
            return null;
        }
        word=word.toLowerCase();
        db = this.getReadableDatabase();
        Cursor c = db.query(currentDBName,
                new String[]{WORD_COLUMN_NAME, DICT_OFFSET_COLUMN_NAME,
                        DICT_CHUNK_SIZE_COLUMN_NAME},
                WORD_COLUMN_NAME + " like '" + word + "'", null, null,
                null, null);
        Long offset, size;
        Pair<Long,Long> pair=null;
        if (c.moveToFirst()) {
            int offsetColIndex = c.getColumnIndex(DICT_OFFSET_COLUMN_NAME);
            int sizeColIndex = c.getColumnIndex(DICT_CHUNK_SIZE_COLUMN_NAME);
            do {
                offset = c.getLong(offsetColIndex);
                size = c.getLong(sizeColIndex);
                pair = new Pair<Long, Long>(offset,size);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        return pair;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
