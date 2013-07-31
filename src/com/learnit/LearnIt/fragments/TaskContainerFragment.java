package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.activities.StarDictToSQL;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.stardict.StarDict;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;

public class TaskContainerFragment extends Fragment {
    final String LOG_TAG = "my_logs";
    private static GetDictTask _task;
    protected Context _context;
    private OnTaskActionListener mCallback;

    // Container Activity must implement this interface
    public interface OnTaskActionListener {
        public void onStartLoading();
        public void onStartSearching();
        public void noDictFound();
        public void onProgressUpdate(int progress);
        public void onDictLoaded(String name);
    }

    private void updateTask(String langFrom, String langTo)
    {
        if (_task == null)
        {
            _task = new GetDictTask(langFrom, langTo);
        }
    }

    public void executeTask(Context context)
    {
        _context = context;
        Pair<String,String> pair = Utils.getCurrentLanguages(_context);
        updateTask(pair.first, pair.second);
        _task.execute();
        Log.e(LOG_TAG, "task was executed");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(LOG_TAG, "THIS IS CALLED");
        try {
            mCallback = (StarDictToSQL) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskActionListener");
        }
    }

    public class GetDictTask extends AsyncTask<Void, Integer, String> {
        private String _langFrom, _langTo;

        private GetDictTask(String langFrom, String langTo)
        {
            _langFrom = langFrom;
            _langTo = langTo;
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
            if (mCallback == null)
            {
                Log.e(LOG_TAG, "mCallback is null");
            }
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

}
