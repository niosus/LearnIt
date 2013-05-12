/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.stardict.DictFile;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddWordFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    private EditText editWord;
    private EditText editTranslation;
    private DBHelper dbHelper;
    private DBHelper dbHelperDict;
    GetDictTask task;
    String selectedLanguageFrom;
    String selectedLanguageTo;
    String currentWord;
    Utils utils;
    boolean wordFocused = false;
    boolean transFocused = false;

    private ImageButton btn_clear_word;
    private ImageButton btn_clear_trans;
    TextView tv_help;

    MenuItem saveItem;

    private final int ASYNC_TASK_FIND_TRANSLATION = 2;
    private final int ASYNC_TASK_FIND_WORD = 1;
    View v;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        wordFocused = true;
        transFocused = false;

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        utils = new Utils();
    }

    public void onResume() {
        super.onResume();
        wordFocused = true;
        transFocused = false;
        editWord.requestFocus();
        Pair<String, String> langPair = Utils.getCurrentLanguages(this.getActivity());
        selectedLanguageFrom = langPair.first;
        selectedLanguageTo = langPair.second;
        Log.d(LOG_TAG, "onResume Add words fragment: from - " + selectedLanguageFrom + " to " + selectedLanguageTo);
        dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
        dbHelperDict = new DBHelper(this.getActivity(), DBHelper.DB_DICT_FROM);
        if (null != saveItem) {
            if (null != editTranslation && null != editWord) {
                if (editWord.length() > 0 && editTranslation.length() > 0) {
                    saveItem.setVisible(true);
                } else {
                    saveItem.setVisible(false);
                }
            } else {
                saveItem.setVisible(false);
            }
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_add_words, menu);
        saveItem = menu.findItem(R.id.save_item);
        saveItem.setVisible(false);
        saveItem.setOnMenuItemClickListener(myOnMenuClickListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    MenuItem.OnMenuItemClickListener myOnMenuClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            addWordToDB(editWord.getText().toString(), editTranslation.getText().toString());
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.add_word_fragment, container, false);
        btn_clear_trans = (ImageButton) v.findViewById(R.id.btn_add_trans_clear);
        btn_clear_word = (ImageButton) v.findViewById(R.id.btn_add_word_clear);
        final MyBtnOnClickListener myBtnOnClickListener = new MyBtnOnClickListener();
        btn_clear_trans.setOnClickListener(myBtnOnClickListener);
        btn_clear_word.setOnClickListener(myBtnOnClickListener);
        btn_clear_trans.setVisibility(View.INVISIBLE);
        btn_clear_word.setVisibility(View.INVISIBLE);
        tv_help = (TextView) v.findViewById(R.id.text_view_help);
        tv_help.setVisibility(View.INVISIBLE);
        editWord = (EditText) v.findViewById(R.id.edv_add_word);
        editWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (editWord.getText().toString().length() > 0) {
                        task = new GetDictTask();
                        task.execute(ASYNC_TASK_FIND_WORD);
                    }
                    wordFocused = true;
                    transFocused = false;
                }
            }
        });
        editTranslation = (EditText) v.findViewById(R.id.edv_add_translation);
        editTranslation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (editWord.getText().toString().length() > 0) {
                        task = new GetDictTask();
                        task.execute(ASYNC_TASK_FIND_TRANSLATION);
                    }
                    wordFocused = false;
                    transFocused = true;
                }
            }
        });
        task = new GetDictTask();

        editWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && !editable.toString().equals("") && !editable.toString().equals(currentWord)) {
                    btn_clear_word.setVisibility(View.VISIBLE);
                    task = new GetDictTask();
                    task.execute(ASYNC_TASK_FIND_WORD);
                }
                if (editable.length() == 0) {
                    if (null != saveItem) {
                        saveItem.setVisible(false);
                    }
                    updateList(null);
                    btn_clear_word.setVisibility(View.INVISIBLE);
                }
            }
        });
        editTranslation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && !editable.toString().equals("")) {
                    btn_clear_trans.setVisibility(View.VISIBLE);
                    if (editable.length() > 0) {
                        saveItem.setVisible(true);
                    }
                }
                if (editable.length() == 0) {
                    if (null != saveItem) {
                        saveItem.setVisible(false);
                    }
                    btn_clear_trans.setVisibility(View.INVISIBLE);
                }
            }
        });
        final ListView listView = (ListView) v.findViewById(R.id.list_of_add_words);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (transFocused) {
                    String queryWord = ((TextView) view).getText().toString();
                    Log.d(LOG_TAG, queryWord);
                    if (editTranslation.getText().toString() == null || editTranslation.getText().toString().equals("")) {
                        editTranslation.setText(queryWord);
                    } else {
                        editTranslation.setText(queryWord + ", " + editTranslation.getText().toString());
                    }
                    editTranslation.setSelection(queryWord.length());
                    editWord.setText(currentWord);
                } else if (wordFocused) {
                    String queryWord = ((TextView) view).getText().toString();
                    Log.d(LOG_TAG, queryWord);
                    editWord.setText(queryWord);
                    editWord.setSelection(queryWord.length());
                    transFocused = true;
                    wordFocused = false;
                    editTranslation.requestFocus();
                }
            }
        });
        return v;
    }

    private void cleanAddWordFields() {
        editTranslation.setText("");
        editWord.setText("");
    }

    private void addArticleToCurrentWord(String article) {
        if (null != article) {
            Log.d(LOG_TAG, article + " " + editWord.getText().toString());
            currentWord = article + " " + getRealWord(editWord.getText().toString());
        } else {
            currentWord = editWord.getText().toString();
        }
    }


    private ArrayList<String> parseDictOutput(String str) {
        Log.d(LOG_TAG, "input = " + str);
        ArrayList<String> tagValues = StringUtils.getHelpWordsFromDictOutput(str);
        String article = StringUtils.getArticleFromDictOutput(str, selectedLanguageFrom);
        addArticleToCurrentWord(article);
        return tagValues;
    }

    public void updateFields(int exitCode) {
        switch (exitCode) {
            case DBHelper.EXIT_CODE_OK:
            case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
            case DBHelper.EXIT_CODE_WORD_UPDATED:
                updateList(null);
                cleanAddWordFields();
                editWord.setFocusableInTouchMode(true);
                editWord.requestFocus();
                break;
        }
    }

    public void showMessage(int exitCode) {
        MyDialogFragment frag = new MyDialogFragment();
        frag.showMessage(exitCode, getFragmentManager());
        updateFields(exitCode);
    }

    private void addWordToDB(String word, String translation) {
        int exitCode;
        Log.d(LOG_TAG, "word = " + word + " trans = " + translation);
        exitCode = dbHelper.writeToDB(word, translation);
        Log.d(LOG_TAG, "add word to DB exit code = " + exitCode);
        showMessage(exitCode);
    }

    private void updateList(List<String> items) {
        ArrayAdapter<String> adapter;
        if (null != items) {
            if (wordFocused)
                tv_help.setText(getString(R.string.add_words_frag_help_text_words));
            else
                tv_help.setText(getString(R.string.add_words_frag_help_text_trans));
            tv_help.setVisibility(View.VISIBLE);
            adapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_list_item_1, items);
            ((ListView) this.getView().findViewById(R.id.list_of_add_words))
                    .setAdapter(adapter);
        } else {
            tv_help.setVisibility(View.INVISIBLE);
            ((ListView) this.getView().findViewById(R.id.list_of_add_words))
                    .setAdapter(null);
        }

    }

    private String getRealWord(String word) {
        return StringUtils.stripFromArticle(this.getActivity(), word);
    }

    private class MyBtnOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_trans_clear:
                    editTranslation.setText("");
                    editTranslation.setFocusableInTouchMode(true);
                    editTranslation.requestFocus();
                    transFocused = true;
                    wordFocused = false;
                    v.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_add_word_clear:
                    editWord.setText("");
                    editWord.setFocusableInTouchMode(true);
                    editWord.requestFocus();
                    updateList(null);
                    transFocused = false;
                    wordFocused = true;
                    v.setVisibility(View.INVISIBLE);
                    break;
            }
        }

    }

    class GetDictTask extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected List<String> doInBackground(Integer... action) {
            try {
                if (action[0] == ASYNC_TASK_FIND_TRANSLATION) {
                    String tempWord = editWord.getText().toString();
                    Log.d(LOG_TAG, "temp word is " + tempWord);
                    Log.d(LOG_TAG, "language from - " + selectedLanguageFrom + " langiage to - " + selectedLanguageTo);
                    File sd = Environment.getExternalStorageDirectory();
                    sd = new File(sd, "LearnIt");
                    sd = new File(sd, selectedLanguageFrom + "-" + selectedLanguageTo);
                    sd = new File(sd, "dict.dict");
                    DictFile dictFile = new DictFile(sd.getPath());
                    if (null != tempWord) {
                        String newWord = getRealWord(tempWord);
                        Pair<Long, Long> pair = dbHelperDict.getDictOffsetAndSize(newWord);
                        return parseDictOutput(dictFile.getWordData(pair.first, pair.second));
                    }
                    return null;
                } else {
                    String newWord = getRealWord(editWord.getText().toString());
                    return dbHelperDict.getHelpWords(newWord);
                }

            } catch (Exception e) {
                Log.d(LOG_TAG, "exception on doInBackground AddWordsFragment");
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> items) {
            super.onPostExecute(items);
            try {
                if (null == items) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_no_word), Toast.LENGTH_LONG).show();
                }
                updateList(items);
            } catch (Exception ex) {
                Log.d(LOG_TAG, "exception onPostExecute AddWordsFragment");
            }
        }
    }
}