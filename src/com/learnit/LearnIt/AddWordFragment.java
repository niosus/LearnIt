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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddWordFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    protected String TAG = "";
    private EditText editWord;
    private EditText editTranslation;
    private DBHelper dbHelper;

    private ImageButton btn_clear_word;
    private ImageButton btn_clear_trans;

    MenuItem saveItem;

    private final int SUCCESS=0;
    private final int WORD_IS_NULL=-1;
    private final int WORD_IS_EMPTY=-2;
    View v;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dbHelper = new DBHelper(this.getActivity());

    }

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
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
            Log.d(LOG_TAG,"testing yahoo!!!");
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

        editWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString()!=null && editable.toString()!="")
                {
                    btn_clear_word.setVisibility(View.VISIBLE);
                }
                if (editable.length()==0)
                {
                    saveItem.setVisible(false);
                    btn_clear_word.setVisibility(View.INVISIBLE);
                }
            }
        });
        editTranslation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString()!=null && editable.toString()!="")
                {
                    btn_clear_trans.setVisibility(View.VISIBLE);
                    if (editable.length()>0)
                    {
                        saveItem.setVisible(true);
                    }
                }
                if (editable.length()==0)
                {
                    saveItem.setVisible(false);
                    btn_clear_trans.setVisibility(View.INVISIBLE);
                }

            }
        });
        return v;
    }

    private void cleanAddWordFields()
    {
        editTranslation.setText("");
        editWord.setText("");
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
                break;
            case DBHelper.EXIT_CODE_WORD_UPDATED:
                cleanAddWordFields();
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_updated");
                break;
            case DBHelper.EXIT_CODE_EMPTY_INPUT:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_EMPTY);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_empty");
                break;
            case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
                cleanAddWordFields();
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_EXISTS);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_exists");
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
        exitCode = dbHelper.writeToDB(word, translation);
        Log.d(LOG_TAG, "got right here exit code = " + exitCode);
        showMessage(exitCode);
    }

    private class MyBtnOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btn_add_trans_clear:
                    editTranslation.setText("");
                    v.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_add_word_clear:
                    editWord.setText("");
                    v.setVisibility(View.INVISIBLE);
                    break;
            }
        }

    }
}