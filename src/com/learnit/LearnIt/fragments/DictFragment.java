/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.activities.EditWord;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    private DBHelper dbHelper;
    private EditText edtWord;
    private ImageButton btnClear;
    Utils utils;

    ActionMode mActionMode = null;


    View v;

    public DictFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.updateCurrentDBName(this.getActivity());
        dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
    }

    public void getWordsByPattern(String word) {
        SimpleAdapter adapter;
        List<Map<String, String>> strings = new ArrayList<Map<String, String>>();
        if (word != null && !word.isEmpty()) {
            Log.d(LOG_TAG, String.format("search word by mask-%s", word));
            strings = dbHelper.getWords(word);
        }
        adapter = new SimpleAdapter(this.getActivity(), strings,
                android.R.layout.simple_list_item_2,
                new String[]{"word", "translation"},
                new int[]{android.R.id.text1, android.R.id.text2});
        ((ListView) this.getView().findViewById(R.id.list_of_words))
                .setAdapter(adapter);
    }


    public void showDialog(String queryWord, String translation, int dialogType) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(MyDialogFragment.ID_TAG, dialogType);
        args.putString(MyDialogFragment.WORD_TAG, queryWord);
        args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
        frag.setArguments(args);
        frag.show(getFragmentManager(), "show_word_fragment_dialog");
    }

    boolean isArticle(String article) {
        String articles = getString(R.string.articles_de);
        return articles.contains(article.toLowerCase());
    }

    boolean isPrefix(String word) {
        String prefix = this.getString(R.string.help_words_de);
        return prefix.contains(word.toLowerCase());
    }


    private String stripFromArticle(String str) {
        String[] tempArray = str.split("\\s");
        Log.d(LOG_TAG, "str = " + str + ", array length = " + tempArray.length);
        if (tempArray.length == 1) {
            return str;
        } else if (tempArray.length > 1) {
            if (isArticle(tempArray[0]) || isPrefix(tempArray[0])) {
                return StringUtils.cutAwayFirstWord(str);
            }
            return str;
        } else return null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dict_fragment, container, false);

        edtWord = (EditText) v.findViewById(R.id.edv_search_word);
        edtWord.clearFocus();
        edtWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getWordsByPattern(s.toString());
                if (!s.toString().equals("") && s.toString() != null) {
                    btnClear.setVisibility(View.VISIBLE);
                }
                if (s.length() == 0) {
                    btnClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });
        btnClear = (ImageButton) v.findViewById(R.id.btn_search_clear);
        MyBtnTouchListener touchListener = new MyBtnTouchListener();
        btnClear.setOnClickListener(touchListener);
        btnClear.setVisibility(View.INVISIBLE);
        final ListView listView = (ListView) v.findViewById(R.id.list_of_words);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                if (mActionMode != null) {
                    return false;
                }
                ListActionMode mActionModeCallback = new ListActionMode(view);
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                HashMap<String, String> maplist = (HashMap<String, String>) parent.getAdapter().getItem(position);
                String queryWord = maplist.get("word");
                Log.d(LOG_TAG, queryWord);
                String translation = maplist.get("translation");
                showDialog(queryWord, translation, MyDialogFragment.DIALOG_SHOW_WORD);
            }
        });

        return v;
    }

    private class MyBtnTouchListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_search_clear:
                    edtWord.setText("");
                    btnClear.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    }

    void startEditWordActivity(String word) {
        Intent intent = new Intent(this.getActivity(), EditWord.class);
        intent.putExtra("word", word);
        startActivity(intent);
        Log.d(LOG_TAG, "start info activity called");
    }


    private class ListActionMode implements ActionMode.Callback {
        private View v;

        public ListActionMode(View view) {
            v = view;
        }

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            TextView tv = (TextView) v.findViewById(android.R.id.text1);
            String queryWord = tv.getText().toString();
            Log.d(LOG_TAG, "item selected = " + queryWord);
            switch (item.getItemId()) {
                case R.id.context_menu_edit:
                    startEditWordActivity(queryWord);
                    edtWord.setText("");
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.context_menu_delete:
                    if (dbHelper.deleteWord(stripFromArticle(queryWord))) {
                        showDialog(queryWord, null, MyDialogFragment.DIALOG_WORD_DELETED);
                        edtWord.setText("");
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    }
}