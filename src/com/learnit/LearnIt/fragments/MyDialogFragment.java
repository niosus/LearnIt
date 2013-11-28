/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.DBHelper;

public class MyDialogFragment extends DialogFragment {
    public static final String ID_TAG = "id";
    public static final String WORD_TAG = "word";
    public static final String TRANSLATION_TAG = "translation";
    public final String LOG_TAG = "my_logs";

    public static final int DIALOG_SHOW_WORD = 666;
    public static final int DIALOG_EMPTY = 667;
    public static final int DIALOG_ADDED = 668;
    public static final int DIALOG_WORD_UPDATED = 669;
    public static final int DIALOG_WORD_EXISTS = 670;
    public static final int DIALOG_WRONG_FORMAT = 671;
    public static final int DIALOG_WRONG_ARTICLE = 672;
    public static final int DIALOG_WRONG_GUESS = 673;
    public static final int DIALOG_WORD_DELETED = 674;
    public static final int DIALOG_PROGRESS = 675;

	private int _type;

	public MyDialogFragment(String word, String trans, int type) {
		super();
		Bundle args = new Bundle();
		args.putString(WORD_TAG, word);
		args.putString(TRANSLATION_TAG, trans);
		args.putInt(ID_TAG, type);
		setArguments(args);
	}

	public MyDialogFragment(int type) {
		super();
		Bundle args = new Bundle();
		args.putInt(ID_TAG, type);
		setArguments(args);
	}

	public MyDialogFragment() {
		super();
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int id = getArguments().getInt(ID_TAG);
        String word;
        switch (id) {
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
                builder.setIcon(R.drawable.ic_action_alerts_and_states_warning);
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
            case DIALOG_PROGRESS:
                ProgressDialog progDialog = new ProgressDialog(getActivity());
                progDialog.setMessage(this.getString(R.string.dialog_progress_message));
                progDialog.setTitle(R.string.dialog_progress_title);
                progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progDialog.setOnCancelListener(myOnCancelListener);
                return progDialog;
        }
        return null;
    }

    DialogInterface.OnCancelListener myOnCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
        }
    };

    OnClickListener myDialogClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
        }
    };

	public void show(FragmentManager fragmentManager, String exitCodeStr) {

		int exitCode = Integer.parseInt(exitCodeStr);
		Bundle args = this.getArguments();
		if (args == null) args = new Bundle();
		switch (exitCode) {
			case DIALOG_SHOW_WORD:
				super.show(fragmentManager, "word_showing");
				break;
			case DBHelper.EXIT_CODE_OK:
				args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_ADDED);
				this.setArguments(args);
				super.show(fragmentManager, "word_added");
				break;
			case DBHelper.EXIT_CODE_WORD_UPDATED:
				args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_UPDATED);
				this.setArguments(args);
				super.show(fragmentManager, "word_updated");
				break;
			case DBHelper.EXIT_CODE_EMPTY_INPUT:
				args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_EMPTY);
				this.setArguments(args);
				super.show(fragmentManager, "word_empty");
				break;
			case DBHelper.EXIT_CODE_WORD_ALREADY_IN_DB:
				args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WORD_EXISTS);
				this.setArguments(args);
				super.show(fragmentManager, "word_exists");
				break;
			case DBHelper.EXIT_CODE_WRONG_ARTICLE:
				args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_ARTICLE);
				this.setArguments(args);
				super.show(fragmentManager, "wrong_article");
				break;
			case DBHelper.EXIT_CODE_WRONG_FORMAT:
				args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_FORMAT);
				this.setArguments(args);
				super.show(fragmentManager, "wrong_format");
				break;
		}
	}
}