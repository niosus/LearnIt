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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MyCustomEditDialog extends DialogFragment {
    public static final String ID_TAG = "id";
    public static final String WORD_TAG = "word";
    public static final String TRANSLATION_TAG = "translation";
    public final String LOG_TAG = "my_logs";
    public static final int DIALOG_EDIT_WORD = 9;
    EditText edtWord;
    EditText edtTrans;
    String oldWord;
    int currentId=-1;

    private ImageButton btnClearWord;
    private ImageButton btnClearTrans;

    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getActivity());
        setStyle(STYLE_NO_TITLE,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState)
    {
        Log.d(LOG_TAG,"onCreateDialogView");
        try{
            int id = getArguments().getInt(ID_TAG);
            oldWord= getArguments().getString(WORD_TAG);
            String translation = dbHelper.getTranslation(oldWord);
            currentId = dbHelper.getId(oldWord);
            Log.d(LOG_TAG,"got edit word - " + oldWord + " trans - " + translation + " id = " + id);
            if (id==DIALOG_EDIT_WORD)
            {
                View v = inflater.inflate(R.layout.edit_word_dialog, container, false);
                edtWord = (EditText) v.findViewById(R.id.edtWord);
                edtWord.setText(oldWord);

                edtTrans = (EditText) v.findViewById(R.id.edtTrans);
                edtTrans.setText(translation);
                btnClearWord = (ImageButton) v.findViewById(R.id.btn_add_word_clear);
                btnClearTrans = (ImageButton) v.findViewById(R.id.btn_add_trans_clear);
                Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
                MyBtnTouchListener myBtnTouchListener = new MyBtnTouchListener();
                btnClearTrans.setOnClickListener(myBtnTouchListener);
                btnClearWord.setOnClickListener(myBtnTouchListener);
                btnCancel.setOnClickListener(myBtnTouchListener);
                btnOk.setOnClickListener(myBtnTouchListener);

                edtWord.addTextChangedListener(new TextWatcher() {
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
                            btnClearWord.setVisibility(View.VISIBLE);
                        }
                        if (editable.length()==0)
                        {
                            btnClearWord.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                edtTrans.addTextChangedListener(new TextWatcher() {
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
                            btnClearTrans.setVisibility(View.VISIBLE);
                        }
                        if (editable.length()==0)
                        {
                            btnClearTrans.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                return v;
            }
        }
        catch (Exception e)
        {
            Log.d(LOG_TAG, "exception " + e.getMessage());
            return super.onCreateView(inflater,container,savedInstanceState);
        }
        return null;
    }

    private class MyBtnTouchListener implements View.OnClickListener
    {
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_add_trans_clear:
                    edtTrans.setText("");
                    v.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_add_word_clear:
                    edtWord.setText("");
                    v.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_ok:
                    Log.d(LOG_TAG,"update word = " + edtWord.getText().toString() + " trans = " + edtTrans.getText().toString());
                    if (dbHelper.checkEmptyString(edtWord.getText().toString())==DBHelper.EXIT_CODE_EMPTY_INPUT
                        || dbHelper.checkEmptyString(edtTrans.getText().toString())==DBHelper.EXIT_CODE_EMPTY_INPUT)
                    {
                        showMessage(DBHelper.EXIT_CODE_EMPTY_INPUT);
                    }
                    else
                    {
                        dbHelper.deleteWord(oldWord);
                        int exitCode = dbHelper.writeToDB(edtWord.getText().toString(), edtTrans.getText().toString());
                        showMessage(exitCode);
                    }
                    break;
                case R.id.btn_cancel:
                    dismiss();
            }
        }
    }

    private void showMessage(int exitCode)
    {
        MyDialogFragment frag;
        Bundle args;
        switch (exitCode) {
            case DBHelper.EXIT_CODE_OK:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_added");
                dismiss();
                break;
            case DBHelper.EXIT_CODE_WORD_UPDATED:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_updated");
                dismiss();
                break;
            case DBHelper.EXIT_CODE_EMPTY_INPUT:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_EMPTY);
                frag.setArguments(args);
                frag.show(getFragmentManager(), "word_empty");
                break;
            case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
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

    public void showDialog(String queryWord, String translation, int dialogType)
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