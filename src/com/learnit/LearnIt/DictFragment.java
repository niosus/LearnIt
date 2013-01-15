/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

public class DictFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    private DBHelper dbHelper;
    private EditText edtWord;
    private ImageButton btnClear;

    ActionMode mActionMode=null;


    View v;

    public DictFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dbHelper = new DBHelper(this.getActivity());
    }

    public void getWordsByPattern(String word) {
        ArrayAdapter<String> adapter;
        ArrayList<String> strings = new ArrayList<String>();
        if (word != null && !word.isEmpty()) {
            Log.d(LOG_TAG, String.format("search word by mask-%s", word));
            strings=dbHelper.getWords(word);
        }
        adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, strings);
        ((ListView) this.getView().findViewById(R.id.list_of_words))
                .setAdapter(adapter);
    }

    public String getTranslation(String word) {
        if (word != null && !word.isEmpty()) {
            Log.d(LOG_TAG, String.format("search word-%s", word));
            return dbHelper.getTranslation(word);
        }
        return null;
    }



    public void showDialog(String queryWord, String translation, int dialogType)
    {
        if (dialogType == MyDialogFragment.DIALOG_EDIT_WORD)
        {
            MyCustomEditDialog frag = new MyCustomEditDialog();
            Bundle args = new Bundle();
            args.putInt(MyDialogFragment.ID_TAG, dialogType);
            args.putString(MyDialogFragment.WORD_TAG, queryWord);
            args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "show_edit_word_fragment_dialog");
        }
        else
        {
            MyDialogFragment frag = new MyDialogFragment();
            Bundle args = new Bundle();
            args.putInt(MyDialogFragment.ID_TAG, dialogType);
            args.putString(MyDialogFragment.WORD_TAG, queryWord);
            args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "show_word_fragment_dialog");
        }
    }

    private String stripWordFromArticle(String str)
    {
        String[] tempArray = str.split(" ");
        Log.d(LOG_TAG, "str = " + str + ", array length = " + tempArray.length);
        switch (tempArray.length)
        {
            case 1:
                return str;
            case 2:
                return tempArray[1];
            default:
                return null;
        }
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
                if (s.toString()!="" && s.toString()!=null)
                {
                    btnClear.setVisibility(View.VISIBLE);
                }
                if (s.length()==0)
                {
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
                String queryWord = ((TextView) view).getText().toString();
                String tempStrippedWord = stripWordFromArticle(queryWord);
                Log.d(LOG_TAG, queryWord);
                edtWord.setText(tempStrippedWord);
                String translation = getTranslation(tempStrippedWord);
                showDialog(queryWord,translation, MyDialogFragment.DIALOG_SHOW_WORD);
            }
        });

        return v;
    }

    private class MyBtnTouchListener implements View.OnClickListener
    {
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_search_clear:
                    edtWord.setText("");
                    btnClear.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    }


    private class ListActionMode implements ActionMode.Callback
    {
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
            String queryWord = ((TextView)v).getText().toString();
            Log.d(LOG_TAG,"item selected = " + queryWord);
            switch (item.getItemId()) {
                case R.id.context_menu_edit:
                    showDialog(queryWord,null,MyDialogFragment.DIALOG_EDIT_WORD);
                    edtWord.setText("");
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.context_menu_delete:
                    if (dbHelper.deleteWord(stripWordFromArticle(queryWord)))
                    {
                        showDialog(queryWord,null,MyDialogFragment.DIALOG_WORD_DELETED);
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