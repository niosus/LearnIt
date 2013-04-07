/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.learnit.LearnIt.stardict.StarDict;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DictToSQL extends FragmentActivity {
    protected static final String LOG_TAG = "my_logs";
    public static boolean home_button_active = true;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragTemp = new ResultFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragTemp)
                .commit();
    }

    public class ResultFragment extends Fragment {
        DBHelper dbHelper;
        Utils utils;
        StarDict dict;
        String selectedLanguageFrom;
        String selectedLanguageTo;
        String currentLanguage;
        TextView tv_title, tv_dict_name, tv_dict_info, tv_loaded;
        private final String LOG_TAG = "my_logs";
        MyTask mt;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            utils = new Utils();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dict_to_sql, container, false);
            tv_title = (TextView) v.findViewById(R.id.text_dict_to_sql_title);
            tv_dict_name = (TextView) v.findViewById(R.id.text_dictionary_name);
            tv_dict_info = (TextView) v.findViewById(R.id.text_dictionary_info);
            tv_loaded = (TextView) v.findViewById(R.id.text_loaded);
            return v;
        }

        @Override
        public void onResume()
        {
            super.onResume();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            selectedLanguageFrom = sp.getString(getString(R.string.key_language_from),"NONE");
            selectedLanguageTo = sp.getString(getString(R.string.key_language_to),"NONE");
            mt = new MyTask();
            mt.action=1;
            mt.execute();
        }

        private void getDict()
        {
            File sd = Environment.getExternalStorageDirectory();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            selectedLanguageFrom = sp.getString(getString(R.string.key_language_from),"NONE");
            selectedLanguageTo = sp.getString(getString(R.string.key_language_to),"NONE");
            Resources res = getResources();
            String[] languages = res.getStringArray(R.array.values_languages_from);
            String allLanguages = Arrays.toString(languages);
            if (allLanguages.contains(selectedLanguageTo))
            {
                currentLanguage = selectedLanguageTo;
            }
            else
            {
                currentLanguage = Locale.getDefault().getLanguage();
            }
            Log.d(LOG_TAG,"possible languages = " + allLanguages + "\n" + currentLanguage);
            if (allLanguages.contains(selectedLanguageFrom))
            {
                dict=null;
                sd = new File(sd, "LearnIt");
                sd = new File(sd, selectedLanguageFrom +"-"+currentLanguage);
                sd = new File(sd, "dict.ifo");
                dict = new StarDict(sd.getPath());
                if (!dict.boolAvailable)
                {
                    dict=null;
                }
            }
        }

        class ProgressDialogFragment extends DialogFragment{
            ProgressDialog dialog;
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                dialog = new ProgressDialog(getActivity());
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setProgressNumberFormat("");
                dialog.setMessage(getString(R.string.dict_sql_progress_searching));
                dialog.setIndeterminate(true);
                return dialog;
            }
            public void setProgress(int i)
            {
                dialog.setProgress(i);
            }
            public void setText(String text)
            {
                dialog.setMessage(text);
            }
            public void setMax(int max)
            {
                dialog.setMax(max);
            }
            public void setIndeterminate(boolean bool)
            {
                dialog.setIndeterminate(bool);
            }
        }

        class MyTask extends AsyncTask<Void, Integer, List<String>> {
            public int action=0;
            SQLiteDatabase db;
            ProgressDialogFragment dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                home_button_active=false;
                dialog = new ProgressDialogFragment();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "MyDialog");
                dbHelper = new DBHelper(getActivity(), DBHelper.DB_DICT_FROM);
                db = dbHelper.getWritableDatabase();
                db.delete(DBHelper.DB_DICT_FROM, null, null);
            }

            private Runnable changeMessage = new Runnable() {
                @Override
                public void run() {
                    dialog.setText(getString(R.string.dict_sql_progress_found));
                    dialog.setIndeterminate(false);
                }
            };

            @Override
            protected List<String> doInBackground(Void... word) {
                try {
                    getDict();
                    if (null==dict)
                    {
                        List<String> list = new ArrayList<String>();
                        list.add(getString(R.string.dict_sql_no_dict));
                        Resources res = getResources();
                        String[] language_codes = res.getStringArray(R.array.values_languages_from);
                        String[] languages = res.getStringArray(R.array.entries_languages_from);
                        String langFromFull =  languages[Arrays.binarySearch(language_codes,selectedLanguageFrom.toLowerCase())];
                        String langToFull =  languages[Arrays.binarySearch(language_codes,currentLanguage.toLowerCase())];
                        list.add(langFromFull+"-"+langToFull);
                        list.add("");
                        list.add("");
                        return list;
                    }
                    runOnUiThread(changeMessage);
                    int numOfWords = dict.getTotalWords();
                    String sql = "INSERT INTO "+DBHelper.DB_DICT_FROM+" ("+dbHelper.DICT_OFFSET_COLUMN_NAME +", "+dbHelper.DICT_CHUNK_SIZE_COLUMN_NAME +", "+dbHelper.WORD_COLUMN_NAME+")  VALUES (?, ?, ?)";
                    SQLiteStatement stmt = db.compileStatement(sql);
                    db.beginTransaction();
                    for (int i=0; i<numOfWords; ++i)
                    {
                        Pair<Long, Long> position = dict.findWordMemoryOffsets(i);
                        String wordTemp = dict.getWordByIndex(i);
                        stmt.bindLong(1, position.first);
                        stmt.bindLong(2, position.second);
                        stmt.bindString(3, wordTemp);
                        stmt.execute();
                        stmt.clearBindings();
                        float ratio = (float)i / numOfWords;
                        int percent = (int)(ratio*100);
                        publishProgress(percent);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();

                    List<String> list = new ArrayList<String>();
                    Resources res = getResources();
                    String[] language_codes = res.getStringArray(R.array.values_languages_from);
                    String[] languages = res.getStringArray(R.array.entries_languages_from);
                    String langFromFull =  languages[Arrays.binarySearch(language_codes,selectedLanguageFrom.toLowerCase())];
                    String langToFull =  languages[Arrays.binarySearch(language_codes,currentLanguage.toLowerCase())];
                    list.add(getString(R.string.dict_sql_title));
                    list.add(dict.getDictName());
                    list.add(String.format(getString(R.string.dict_sql_version), langFromFull, langToFull, dict.getDictVersion()));
                    list.add(getString(R.string.dict_sql_success));
                    return list;
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "error" + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                dialog.setProgress(values[0]);
            }


            @Override
            protected void onPostExecute(List<String> item) {
                super.onPostExecute(item);
                home_button_active=true;
                tv_title.setText(item.get(0));
                tv_dict_name.setText(item.get(1));
                tv_dict_info.setText(item.get(2));
                tv_loaded.setText(item.get(3));
                DBHelper.DB_WORDS="myDB"+selectedLanguageFrom+currentLanguage;
                dialog.dismiss();
            }
        }
    }
}