package com.learnit.LearnIt;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
                ImageButton btnClearWord = (ImageButton) v.findViewById(R.id.btn_add_word_clear);
                ImageButton btnClearTrans = (ImageButton) v.findViewById(R.id.btn_add_trans_clear);
                Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
                MyBtnTouchListener myBtnTouchListener = new MyBtnTouchListener();
                btnClearTrans.setOnClickListener(myBtnTouchListener);
                btnClearWord.setOnClickListener(myBtnTouchListener);
                btnCancel.setOnClickListener(myBtnTouchListener);
                btnOk.setOnClickListener(myBtnTouchListener);
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
                    break;
                case R.id.btn_add_word_clear:
                    edtWord.setText("");
                    break;
                case R.id.btn_ok:
                    Log.d(LOG_TAG,"update word = " + edtWord.getText().toString() + " trans = " + edtTrans.getText().toString());
                    dbHelper.deleteWord(oldWord);
                    dbHelper.writeToDB(edtWord.getText().toString(), edtTrans.getText().toString());
                    dismiss();
                    break;
                case R.id.btn_cancel:
                    dismiss();
            }
        }
    }
}