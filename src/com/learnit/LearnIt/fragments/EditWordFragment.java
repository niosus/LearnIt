/*
 * Copyright (C) 2014  Igor Bogoslavskyi
 * This file is part of LearnIt.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.learnit.LearnIt.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;
import com.learnit.LearnIt.data_types.FactoryDbHelper;
import com.learnit.LearnIt.utils.StringUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EditWordFragment extends DialogFragment {
    public static final String WORD_TAG = "word";
    public final String LOG_TAG = "my_logs";
    EditText edtWord;
    EditText edtTrans;
    String oldWord;
    String oldStrippedWord;

    private ImageButton btnClearWord;
    private ImageButton btnClearTrans;

    DBHelper dbHelper;

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_cancel:
                return true;
            case R.id.edit_menu_done:
                Log.d(LOG_TAG, "update word = " + edtWord.getText().toString() + " trans = " + edtTrans.getText().toString());
                if (StringUtils.isStringEmpty(edtWord.getText().toString())
                        || StringUtils.isStringEmpty(edtTrans.getText().toString())) {
                    Crouton.makeText(this.getActivity(), getString(R.string.crouton_empty_input), Style.ALERT).show();
                } else {
                    dbHelper.deleteWord(oldStrippedWord);
                    int exitCode = dbHelper.writeToDB(edtWord.getText().toString(), edtTrans.getText().toString());
	                if (exitCode == DBHelper.EXIT_CODE_OK) {
		                Crouton.makeText(this.getActivity(), getString(R.string.crouton_word_saved, edtWord.getText().toString()), Style.CONFIRM).show();
	                } else if (exitCode == DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB) {
                        Crouton.makeText(this.getActivity(), getString(R.string.crouton_word_already_present, edtWord.getText().toString()), Style.ALERT).show();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return new AlertDialog.Builder(getActivity())
                .setTitle("blah")
                .setPositiveButton(R.string.dialog_button_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(LOG_TAG, "positive button clicked");
                            }
                        }
                )
                .setNegativeButton(R.string.dialog_button_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(LOG_TAG, "negative button clicked");
                            }
                        }
                )
                .create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        Log.d(LOG_TAG, "inflating view");
        View v = inflater.inflate(R.layout.edit_word, container, false);

        edtWord = (EditText) v.findViewById(R.id.edtWord);
        edtTrans = (EditText) v.findViewById(R.id.edtTrans);
        edtWord.setText(oldWord);
        oldStrippedWord = StringUtils.stripFromArticle(this.getActivity(), oldWord);
        String translation = dbHelper.getTranslation(oldStrippedWord);
        Log.d(LOG_TAG, "got word to edit = " + oldStrippedWord + ", trans = " + translation);
        edtTrans.setText(translation);

        btnClearWord = (ImageButton) v.findViewById(R.id.btn_add_word_clear);
        btnClearTrans = (ImageButton) v.findViewById(R.id.btn_add_trans_clear);
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

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        removeActionBarLabelIfNeeded();
        dbHelper = FactoryDbHelper.createDbHelper(this.getActivity(), DBHelper.DB_WORDS);
        oldWord = this.getArguments().getString(WORD_TAG);
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
}
