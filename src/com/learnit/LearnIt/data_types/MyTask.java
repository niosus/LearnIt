package com.learnit.LearnIt.data_types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Pair;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.stardict.StarDict;

import java.io.File;

public class MyTask extends AsyncTask<Void, Integer, String> {
    private static MyTask _instance;
    private Context _context;
    private String _langFrom, _langTo;

    OnTaskActionListener mCallback;

    // Container Activity must implement this interface
    public interface OnTaskActionListener {
        public void onStartLoading();
        public void onStartSearching();
        public void noDictFound();
        public void onProgressUpdate(int progress);
        public void onDictLoaded(String name);
    }

    private MyTask(Context context, String langFrom, String langTo)
    {
        _context = context;
        _langFrom = langFrom;
        _langTo = langTo;
    }

    public static MyTask instance(Context context, String langFrom, String langTo)
    {
        if (_instance==null)
        {
            _instance = new MyTask(context, langFrom, langTo);
            return _instance;
        }
        if (!langFrom.equals(_instance._langFrom)
                || !langTo.equals(_instance._langTo))
        {
            //this means the task is actually performing something else
            return null;
        }
        if (!context.equals(_instance._context))
        {
            _instance._context = context;
        }
        return _instance;
    }

    private StarDict getDict(String langFrom, String langTo) {
        File sd = Environment.getExternalStorageDirectory();
        sd = new File(sd, "LearnIt");
        sd = new File(sd, langFrom + "-" + langTo);
        sd = new File(sd, "dict.ifo");
        StarDict dict = new StarDict(sd.getPath());
        if (!dict.boolAvailable) {
            dict = null;
        }
        return dict;
    }

    private void updateDatabaseFromDict(StarDict dict)
    {
        DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_DICT_FROM);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numOfWords = dict.getTotalWords();
        String sql = "INSERT INTO " + DBHelper.DB_DICT_FROM + " (" + dbHelper.DICT_OFFSET_COLUMN_NAME + ", " + dbHelper.DICT_CHUNK_SIZE_COLUMN_NAME + ", " + dbHelper.WORD_COLUMN_NAME + ")  VALUES (?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        db.beginTransaction();
        for (int i = 0; i < numOfWords; ++i) {
            Pair<Long, Long> position = dict.findWordMemoryOffsets(i);
            String wordTemp = dict.getWordByIndex(i);
            stmt.bindLong(1, position.first);
            stmt.bindLong(2, position.second);
            stmt.bindString(3, wordTemp);
            stmt.execute();
            stmt.clearBindings();
            float ratio = (float) i / numOfWords;
            int percent = (int) (ratio * 100);
            publishProgress(percent);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DBHelper dbHelper = new DBHelper(_context, DBHelper.DB_DICT_FROM);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.DB_DICT_FROM, null, null);
        mCallback.onStartSearching();
    }

    @Override
    protected void onPostExecute(String dictName) {
        super.onPostExecute(dictName);
        mCallback.onDictLoaded(dictName);
    }

    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mCallback.onProgressUpdate(progress[0]);
    }

    @Override
    protected String doInBackground(Void... unused) {
        StarDict dict = getDict(_langFrom, _langTo);
        if (null == dict) {
            mCallback.noDictFound();
            return null;
        }
        mCallback.onStartLoading();
        updateDatabaseFromDict(dict);
        return dict.getDictName();
    }
}
