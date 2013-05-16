/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.stardict.StarDict;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictToSQL extends FragmentActivity {
    protected static final String LOG_TAG = "my_logs";
    private static final int ACTION_NONE = 1;
    private static final int ACTION_LOAD_DICT = 1;
    public static boolean home_button_active = true;
    ResultFragment resultFragment;
    MyTask mt;

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultFragment = new ResultFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, resultFragment)
                .commit();
        mt = (MyTask) getLastCustomNonConfigurationInstance();
        if (mt == null) {
            Log.d(LOG_TAG,"creating new task");
            mt = new MyTask();
            mt.link(this);
            mt.init(this,ACTION_LOAD_DICT);
            mt.execute();
        }
        else
        {
            mt.link(this);
        }
    }

    public Object onRetainCustomNonConfigurationInstance() {
        mt.unLink();
        return mt;
    }

    public static class MyTask extends AsyncTask<Void, Integer, List<String>> {
        int action = ACTION_NONE;
        DictToSQL activity;
        ProgressDialogFragment progressDialog;
        DBHelper dbHelper;
        Context context;
        String selectedLanguageFrom;
        String selectedLanguageTo;
        StarDict dict;

        // получаем ссылку на MainActivity
        void link(DictToSQL act) {
            this.activity = act;
            this.progressDialog = new ProgressDialogFragment();
        }

        // обнуляем ссылку
        void unLink() {
            activity = null;
            progressDialog=null;
            Log.d(LOG_TAG,"unlinked everyone");
        }

        public void init(Context context, int action)
        {
            this.context=context;
            this.action=action;
        }

        private void getDict() {
            Log.d(LOG_TAG,"get dict");
            File sd = Environment.getExternalStorageDirectory();
            Pair<String,String> pair = Utils.getCurrentLanguages(context);
            selectedLanguageFrom = pair.first;
            selectedLanguageTo = pair.second;

            dict = null;
            sd = new File(sd, "LearnIt");
            sd = new File(sd, selectedLanguageFrom + "-" + selectedLanguageTo);
            sd = new File(sd, "dict.ifo");
            dict = new StarDict(sd.getPath());
            if (!dict.boolAvailable) {
                dict = null;
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG,"on pre execute task");
            home_button_active = false;
            progressDialog.show(activity.getSupportFragmentManager(), "MyProgressDialog");
            dbHelper = new DBHelper(context, DBHelper.DB_DICT_FROM);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DBHelper.DB_DICT_FROM, null, null);
        }

        private Runnable changeMessagePercentage = new Runnable() {
            @Override
            public void run() {
                progressDialog.setText(activity.getString(R.string.dict_sql_progress_found));
                progressDialog.setIndeterminate(false);
            }
        };

        private Runnable changeMessageSearching = new Runnable() {
            @Override
            public void run() {
                progressDialog.setText(activity.getString(R.string.dict_sql_progress_searching));
                progressDialog.setIndeterminate(true);
            }
        };

        @Override
        protected List<String> doInBackground(Void... word) {
            try {
                activity.runOnUiThread(changeMessageSearching);
                getDict();
                if (null == dict) {
                    List<String> list = new ArrayList<String>();
                    list.add(activity.getString(R.string.dict_sql_no_dict));
                    Resources res = activity.getResources();
                    String[] language_codes = res.getStringArray(R.array.values_languages_from);
                    String[] languages = res.getStringArray(R.array.entries_languages_from);
                    String langFromFull = languages[Arrays.binarySearch(language_codes, selectedLanguageFrom)];
                    String langToFull = languages[Arrays.binarySearch(language_codes, selectedLanguageTo)];
                    list.add(langFromFull + "-" + langToFull);
                    list.add("");
                    list.add("");
                    return list;
                }
                activity.runOnUiThread(changeMessagePercentage);
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

                List<String> list = new ArrayList<String>();
                Resources res = activity.getResources();
                String[] language_codes = res.getStringArray(R.array.values_languages_from);
                String[] languages = res.getStringArray(R.array.entries_languages_from);
                String langFromFull = languages[Arrays.binarySearch(language_codes, selectedLanguageFrom.toLowerCase())];
                String langToFull = languages[Arrays.binarySearch(language_codes, selectedLanguageTo.toLowerCase())];
                list.add(activity.getString(R.string.dict_sql_title));
                list.add(dict.getDictName());
                list.add(String.format(activity.getString(R.string.dict_sql_version), langFromFull, langToFull, dict.getDictVersion()));
                list.add(activity.getString(R.string.dict_sql_success));
                return list;
            } catch (Exception e) {
                Log.e(LOG_TAG, "error" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(LOG_TAG,"cancelled");
            if (dbHelper!=null)
            {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog = (ProgressDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("MyProgressDialog");
            progressDialog.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(final List<String> item) {
            super.onPostExecute(item);
            home_button_active = true;
            activity.resultFragment.setTexts(item);
            progressDialog = (ProgressDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("MyProgressDialog");
            progressDialog.dismiss();
            new CountDownTimer(10000, 100) {
                List<String> items = item;

                public void onTick(long millisUntilFinished) {
                    activity.resultFragment.setTimerText(String.format(activity.getString(R.string.dict_sql_closing_window), millisUntilFinished / 1000));
                    activity.resultFragment.setTexts(items);
                }

                public void onFinish() {
                    activity.resultFragment.finishActivity();
                }
            }.start();
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {
        ProgressDialog dialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgressNumberFormat("");
            dialog.setMessage(getString(R.string.dict_sql_progress_found));
//            dialog.setCancelable(false);
            Log.d(LOG_TAG,"progress progressDialog oncreate");
            return dialog;
        }

        public void setProgress(int i) {
            try
            {
                dialog.setProgress(i);
            }
            catch (Exception e)
            {
//                Log.d(LOG_TAG,"trying to update progress that belongs to old activity");
            }
        }

        public void setText(String text) {
            try
            {
                dialog.setMessage(text);
            }
            catch (Exception e)
            {
//                Log.d(LOG_TAG,"trying to update progress that belongs to old activity");
            }
        }

        public void setIndeterminate(boolean bool) {
            try
            {
                dialog.setIndeterminate(bool);
            }
            catch (Exception e)
            {
//                Log.d(LOG_TAG,"trying to update progress that belongs to old activity");
            }
        }
    }


    public static class ResultFragment extends Fragment {
        TextView tvTitle, tvDictName, tvDictInfo, tvLoaded, tvCountdown;

        public void setTexts(List<String> item)
        {
            tvTitle.setText(item.get(0));
            tvDictName.setText(item.get(1));
            tvDictInfo.setText(item.get(2));
            tvLoaded.setText(item.get(3));
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("Title", tvTitle.getText().toString());
            outState.putString("DictName", tvDictName.getText().toString());
            outState.putString("DictInfo", tvDictInfo.getText().toString());
            outState.putString("Loaded", tvLoaded.getText().toString());
        }

        public void setTimerText(String text)
        {
            tvCountdown.setText(text);
        }

        public ResultFragment()
        {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dict_to_sql, container, false);
            tvTitle = (TextView) v.findViewById(R.id.text_dict_to_sql_title);
            tvDictName = (TextView) v.findViewById(R.id.text_dictionary_name);
            tvDictInfo = (TextView) v.findViewById(R.id.text_dictionary_info);
            tvLoaded = (TextView) v.findViewById(R.id.text_loaded);
            tvCountdown = (TextView) v.findViewById(R.id.text_countdown);
            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        protected void finishActivity() {
            getActivity().finish();
        }
    }
}