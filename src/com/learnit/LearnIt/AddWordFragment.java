package com.learnit.LearnIt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddWordFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    protected String TAG = "";
    private EditText editWord;
    private EditText editTranslation;
    private DBHelper dbHelper;

    private final int SUCCESS=0;
    private final int WORD_IS_NULL=-1;
    private final int WORD_IS_EMPTY=-2;
    View v;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dbHelper = new DBHelper(this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.add_word_fragment, container, false);
        Button btnAddWord = (Button) v.findViewById(R.id.btn_add_word);
        final MyBtnOnClickListener myBtnOnClickListener = new MyBtnOnClickListener();
        btnAddWord.setOnClickListener(myBtnOnClickListener);
        editWord = (EditText) v.findViewById(R.id.edv_add_word);
        editTranslation = (EditText) v.findViewById(R.id.edv_add_translation);
        return v;
    }

    private void cleanAddWordFields()
    {
        editTranslation.setText("");
        editWord.setText("");
    }

    private void addWordToDB(String word, String translation)
    {
        int exitCode;
        exitCode = dbHelper.writeToDB(word, translation);
        Log.d(LOG_TAG, "got right here exit code = " + exitCode);
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

    private class MyBtnOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
//            fragmentEventListener.addWordToDB(word, translation);
              addWordToDB(editWord.getText().toString(),editTranslation.getText().toString());
        }

    }
}