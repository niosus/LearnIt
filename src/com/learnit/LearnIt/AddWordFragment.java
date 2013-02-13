/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.learnit.LearnIt.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddWordFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    private EditText editWord;
    private EditText editTranslation;
    private DBHelper dbHelper;
    GetDictTask task;
    StarDict dict;
    String selectedLanguageFrom;
    String selectedLanguageTo;
    Utils utils;

    private ImageButton btn_clear_word;
    private ImageButton btn_clear_trans;

    MenuItem saveItem;

    private final int ASYNC_TASK_LOAD_DICTIONARY = 2;
    private final int ASYNC_TASK_FIND_WORD = 1;
    View v;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        selectedLanguageFrom = sp.getString(getString(R.string.key_language_from),"NONE");
        selectedLanguageTo = sp.getString(getString(R.string.key_language_to),"NONE");
        dbHelper = new DBHelper(this.getActivity());

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        utils = new Utils();
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
        String currentLanguage;
        if (allLanguages.contains(selectedLanguageTo))
        {
            currentLanguage = selectedLanguageTo;
        }
        else
        {
            currentLanguage = Locale.getDefault().getLanguage();
        }
        Log.d(LOG_TAG,"possible languages = " + allLanguages);
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

    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String newSelectedLanguageFrom = sp.getString(getString(R.string.key_language_from),"NONE");
        String newSelectedLanguageTo = sp.getString(getString(R.string.key_language_to),"NONE");
        if (null==dict
                || !selectedLanguageFrom.equals(newSelectedLanguageFrom)
                || !selectedLanguageTo.equals(newSelectedLanguageTo))
        {
            dict=null;
            task = new GetDictTask();
            task.execute(ASYNC_TASK_LOAD_DICTIONARY);
        }
        if (null!=saveItem)
        {
            if (null!=editTranslation && null!=editWord)
            {
                if (editWord.length()>0 && editTranslation.length()>0)
                {
                    saveItem.setVisible(true);
                }
                else
                {
                    saveItem.setVisible(false);
                }
            }
            else
            {
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
            addWordToDB(editWord.getText().toString(),editTranslation.getText().toString());
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
        editWord = (EditText) v.findViewById(R.id.edv_add_word);
        editTranslation = (EditText) v.findViewById(R.id.edv_add_translation);
        editTranslation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    task = new GetDictTask();
                    task.execute(ASYNC_TASK_FIND_WORD);
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
                if (editable.toString()!=null && !editable.toString().equals(""))
                {
                    btn_clear_word.setVisibility(View.VISIBLE);
                }
                if (editable.length()==0)
                {
                    if (null!=saveItem)
                    {
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
                if (editable.toString()!=null && !editable.toString().equals(""))
                {
                    btn_clear_trans.setVisibility(View.VISIBLE);
                    if (editable.length()>0)
                    {
                        saveItem.setVisible(true);
                    }
                }
                if (editable.length()==0)
                {
                    if (null!=saveItem)
                    {
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
                String queryWord = ((TextView) view).getText().toString();
                Log.d(LOG_TAG, queryWord);
                if (editTranslation.getText().toString()==null||editTranslation.getText().toString().equals(""))
                {
                    editTranslation.setText(queryWord);
                }
                else
                {
                    editTranslation.setText(queryWord + ", " + editTranslation.getText().toString());
                }
                editTranslation.setSelection(queryWord.length());
            }
        });
        return v;
    }

    private void cleanAddWordFields()
    {
        editTranslation.setText("");
        editWord.setText("");
    }


    private  ArrayList<String> parseDictOutput(String str) {
        Log.d(LOG_TAG,"input = " + str);
        ArrayList<String> tagValues = new ArrayList<String>();
        if (str.contains("<dtrn>"))
        {

            String deleteCo = "(<co>(.+?)</co>)|(<abr>(.+?)</abr>)|(<c>(.+?)</c>)|(<i>(.+?)</i>)|(<nu/>(.+?)<nu/>)";
            String selectDtrn = "<dtrn>(.+?)</dtrn>";
//            String selectDtrn = ".*";
            Pattern p = Pattern.compile(deleteCo);
            Matcher matcher = p.matcher(str);
            while (matcher.find()) {
                str = matcher.replaceAll("");
                matcher = p.matcher(str);
            }
            p = Pattern.compile(selectDtrn);
            matcher = p.matcher(str);
            while (matcher.find()) {
                String[] temp = matcher.group(1).split("\\s*(,|;)\\s*");
                for (String s:temp)
                {
                    tagValues.add(s);
                }
            }
            return tagValues;
        }
        else
        {
            String[] temp = str.split("\\s*(\\n|,)\\s*");
            for (String s:temp)
            {
                if (!s.equals(temp[0]))
                {
                    tagValues.add(s);
                }
            }
            return tagValues;
        }
    }

    private void showMessage(int exitCode)
    {
        MyDialogFragment frag;
        Bundle args;
        switch (exitCode) {
            case DBHelper.EXIT_CODE_OK:
                cleanAddWordFields();
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_ADDED);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_added");
                updateList(null);
                editWord.setFocusableInTouchMode(true);
                editWord.requestFocus();
                break;
            case DBHelper.EXIT_CODE_WORD_UPDATED:
                cleanAddWordFields();
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_updated");
                updateList(null);
                editWord.setFocusableInTouchMode(true);
                editWord.requestFocus();
                break;
            case DBHelper.EXIT_CODE_EMPTY_INPUT:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_EMPTY);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_empty");
                updateList(null);
                editWord.setFocusableInTouchMode(true);
                editWord.requestFocus();
                break;
            case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
                cleanAddWordFields();
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_EXISTS);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_exists");
                updateList(null);
                editWord.setFocusableInTouchMode(true);
                editWord.requestFocus();
                break;
            case DBHelper.EXIT_CODE_WRONG_ARTICLE:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_ARTICLE);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "wrong_article");
                break;
            case DBHelper.EXIT_CODE_WRONG_FORMAT:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_FORMAT);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "wrong_format");
                break;
        }
    }

    private void addWordToDB(String word, String translation)
    {
        int exitCode;
        Log.d(LOG_TAG,"word = " + word + " trans = " + translation);
        exitCode = dbHelper.writeToDB(word, translation);
        Log.d(LOG_TAG, "got right here exit code = " + exitCode);
        showMessage(exitCode);
    }

    private void updateList(ArrayList<String> items)
    {
        ArrayAdapter<String> adapter;
        if (null!=items)
        {
        adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, items);
        ((ListView) this.getView().findViewById(R.id.list_of_add_words))
                .setAdapter(adapter);
        }
        else
        {
        ((ListView) this.getView().findViewById(R.id.list_of_add_words))
                .setAdapter(null);
        }

    }

    private String getRealWord(String word)
    {
        return utils.stripFromArticle(this.getActivity(),word);
    }

    private class MyBtnOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btn_add_trans_clear:
                    editTranslation.setText("");
                    editTranslation.setFocusableInTouchMode(true);
                    editTranslation.requestFocus();
                    v.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_add_word_clear:
                    editWord.setText("");
                    editWord.setFocusableInTouchMode(true);
                    editWord.requestFocus();
                    updateList(null);
                    v.setVisibility(View.INVISIBLE);
                    break;
            }
        }

    }

    class GetDictTask extends AsyncTask<Integer, Void, ArrayList<String> > {
        private boolean createDict=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null==dict)
            {
                createDict=true;
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_loading_dict), Toast.LENGTH_SHORT).show();
            }
            else
            {
                createDict=false;
            }
        }


        @Override
        protected synchronized ArrayList<String> doInBackground(Integer... action) {
            try {
                if (action[0]==ASYNC_TASK_FIND_WORD)
                    {
                    String tempWord = editWord.getText().toString();
                    Log.d(LOG_TAG,"temp word is " + tempWord);
                    if (null!=tempWord)
                    {
                        String newWord = getRealWord(tempWord);
                        return parseDictOutput(dict.lookupWord(newWord));
                    }
                    return null;
                }
                else if (action[0]==ASYNC_TASK_LOAD_DICTIONARY)
                {
                    try {
                        if (null==dict)
                            getDict();
                        return null;
                    }
                    catch (OutOfMemoryError e)
                    {
                        dict=null;
                        Log.d(LOG_TAG,"ERROR in asyncronical get dict");
                    }
                }
            }
            catch (Exception e)
            {
//                Log.e("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> items) {
            super.onPostExecute(items);
            if (null==items)
            {

                if (null==dict)
                {
                    if (selectedLanguageTo.equals("auto"))
                    {
                        selectedLanguageTo=Locale.getDefault().getLanguage();
                    }
                    Toast.makeText(getActivity().getApplicationContext(), String.format(getString(R.string.toast_no_dict), selectedLanguageFrom+"-"+selectedLanguageTo), Toast.LENGTH_LONG).show();
                }
                else if (createDict)
                {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_loaded_dict), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_no_word), Toast.LENGTH_LONG).show();
                }
            }
            updateList(items);
        }
    }
}