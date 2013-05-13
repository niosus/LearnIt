package com.learnit.LearnIt.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.learnit.LearnIt.fragments.MyDialogFragment;
import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

public class EditWord extends FragmentActivity {
    public static final String WORD_TAG = "word";
    public final String LOG_TAG = "my_logs";
    EditText edtWord;
    EditText edtTrans;
    String oldWord;
    String oldStrippedWord;
    Utils utils;

    private ImageButton btnClearWord;
    private ImageButton btnClearTrans;

    DBHelper dbHelper;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_cancel:
                finishActivity();
                return true;
            case R.id.edit_menu_done:
                Log.d(LOG_TAG, "update word = " + edtWord.getText().toString() + " trans = " + edtTrans.getText().toString());
                if (dbHelper.checkEmptyString(edtWord.getText().toString()) == DBHelper.EXIT_CODE_EMPTY_INPUT
                        || dbHelper.checkEmptyString(edtTrans.getText().toString()) == DBHelper.EXIT_CODE_EMPTY_INPUT) {
                    showMessage(DBHelper.EXIT_CODE_EMPTY_INPUT);
                } else {
                    dbHelper.deleteWord(oldStrippedWord);
                    int exitCode = dbHelper.writeToDB(edtWord.getText().toString(), edtTrans.getText().toString());
                    showMessage(exitCode);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
        utils = new Utils();
        oldWord = getIntent().getStringExtra(WORD_TAG);
        oldStrippedWord = StringUtils.stripFromArticle(this, oldWord);
        String translation = dbHelper.getTranslation(oldStrippedWord);
        Log.d(LOG_TAG, "got word to edit = " + oldStrippedWord + ", trans = " + translation);

        setContentView(R.layout.edit_word);

        edtWord = (EditText) findViewById(R.id.edtWord);
        edtTrans = (EditText) findViewById(R.id.edtTrans);
        edtWord.setText(oldWord);
        edtTrans.setText(translation);

        btnClearWord = (ImageButton) findViewById(R.id.btn_add_word_clear);
        btnClearTrans = (ImageButton) findViewById(R.id.btn_add_trans_clear);
        MyBtnTouchListener myBtnTouchListener = new MyBtnTouchListener();
        btnClearTrans.setOnClickListener(myBtnTouchListener);
        btnClearWord.setOnClickListener(myBtnTouchListener);

        edtWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && !editable.toString().equals("")) {
                    btnClearWord.setVisibility(View.VISIBLE);
                }
                if (editable.length() == 0) {
                    btnClearWord.setVisibility(View.INVISIBLE);
                }
            }
        });
        edtTrans.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && !editable.toString().equals("")) {
                    btnClearTrans.setVisibility(View.VISIBLE);
                }
                if (editable.length() == 0) {
                    btnClearTrans.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void finishActivity() {
        this.finish();
    }

    private class MyBtnTouchListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_trans_clear:
                    edtTrans.setText("");
                    v.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_add_word_clear:
                    edtWord.setText("");
                    v.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    private void showMessage(int exitCode) {
        MyDialogFragment frag;
        Bundle args;
        switch (exitCode) {
            case DBHelper.EXIT_CODE_OK:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "word_updated");
                this.finish();
                break;
            case DBHelper.EXIT_CODE_WORD_UPDATED:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "word_updated");
                this.finish();
                break;
            case DBHelper.EXIT_CODE_EMPTY_INPUT:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_EMPTY);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "word_empty");
                break;
            case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_EXISTS);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "word_exists");
                break;
            case DBHelper.EXIT_CODE_WRONG_ARTICLE:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_ARTICLE);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "wrong_article");
                break;
            case DBHelper.EXIT_CODE_WRONG_FORMAT:
                frag = new MyDialogFragment();
                args = new Bundle();
                args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_FORMAT);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "wrong_format");
                break;
        }
    }

}
