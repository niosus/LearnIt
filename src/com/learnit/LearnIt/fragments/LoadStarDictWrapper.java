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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.LearnIt.activities.LoadStarDictActivity;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.stardict.StarDict;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;

public class LoadStarDictWrapper extends Fragment {
    final String LOG_TAG = "my_logs";
    public static String TAG = "task_fragment";
    public boolean DONE = false;
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _context = activity;
        try {
            mCallback = (LoadStarDictActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskActionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Pair<String,String> pair = Utils.getCurrentLanguages(_context);
        updateTask(pair.first, pair.second);
        _task.execute();
        Log.e(LOG_TAG, "task was executed");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    private void updateTask(String langFrom, String langTo)
    {
        if (_task == null || DONE)
        {
            _task = new GetDictTask(langFrom, langTo);
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
            DONE = true;
            _task = null;
            if (dictName == null)
            {
                mCallback.noDictFound();
                return;
            }
            mCallback.onDictLoaded(dictName);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mCallback.onProgressUpdate(progress[0]);
        }

        @Override
        protected String doInBackground(Void... unused) {
            ((Activity)_context).runOnUiThread(onStartSearching);
            StarDict dict = getDict(_langFrom, _langTo);
            if (null == dict) {
                return null;
            }
            ((Activity)_context).runOnUiThread(onStartLoading);
            updateDatabaseFromDict(dict);
            return dict.getDictName();
        }

        private Runnable onStartLoading = new Runnable() {
            @Override
            public void run() {
                mCallback.onStartLoading();
            }
        };

        private Runnable onStartSearching = new Runnable() {
            @Override
            public void run() {
                mCallback.onStartSearching();
            }
        };
    }

}
