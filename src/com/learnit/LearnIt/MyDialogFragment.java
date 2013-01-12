package com.learnit.LearnIt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    public static final String ID_TAG = "id";
    public static final String WORD_TAG = "word";
    public static final String TRANSLATION_TAG = "translation";

    public static final int DIALOG_SHOW_WORD = 0;
    public static final int DIALOG_EMPTY = 1;
    public static final int DIALOG_ADDED = 2;
    public static final int DIALOG_WORD_UPDATED = 3;
    public static final int DIALOG_WORD_EXISTS = 4;
    public static final int DIALOG_WRONG_FORMAT = 5;
    public static final int DIALOG_WRONG_ARTICLE = 6;
    public static final int DIALOG_WRONG_GUESS = 7;
    public static final int DIALOG_WORD_DELETED = 8;
    public static final int DIALOG_UPDATE_WORD = 9;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int id = getArguments().getInt(ID_TAG);
        String word = "";
        switch (id)
        {
            case DIALOG_SHOW_WORD:
                word = getArguments().getString(WORD_TAG);
                String translation = getArguments().getString(TRANSLATION_TAG);
                builder.setTitle(word)
                        .setMessage(translation)
                        .setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_EMPTY:
                builder.setMessage(R.string.dialog_empty_text).setTitle(
                        R.string.dialog_empty_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                builder.setIcon(R.drawable.ic_action_alert);
                return builder.create();
            case DIALOG_ADDED:
                builder.setTitle(R.string.dialog_added_title)
                        .setMessage(R.string.dialog_added_text)
                        .setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_WORD_EXISTS:
                builder.setMessage(R.string.dialog_exists_text).setTitle(
                        R.string.dialog_exists_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_WORD_UPDATED:
                builder.setMessage(R.string.dialog_updated_text).setTitle(
                        R.string.dialog_updated_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_WRONG_ARTICLE:
                builder.setMessage(R.string.dialog_article_text).setTitle(
                        R.string.dialog_article_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_WRONG_FORMAT:
                builder.setMessage(R.string.dialog_format_text).setTitle(
                        R.string.dialog_format_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_WRONG_GUESS:
                builder.setMessage(R.string.dialog_wrong_guess_message).setTitle(
                        R.string.dialog_wrong_guess_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
            case DIALOG_WORD_DELETED:
                word = getArguments().getString(WORD_TAG);
                builder.setMessage(String.format(this.getString(R.string.dialog_word_deleted_message), word)).setTitle(
                        R.string.dialog_word_deleted_title);
                builder.setNeutralButton(R.string.ok, myDialogClickListener);
                return builder.create();
        }
        return null;
    }

    OnClickListener myDialogClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
        }
    };
}