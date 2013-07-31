package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.learnit.LearnIt.R;

public class MyProgressDialog extends DialogFragment {
    ProgressDialog _dialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return _dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _dialog = new ProgressDialog(activity);
        _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _dialog.setProgressNumberFormat("");
        _dialog.setMessage(getString(R.string.dict_sql_progress_searching));
        _dialog.setIndeterminate(false);
    }

    public void setProgress(int i)
    {
        _dialog.setProgress(i);
    }

    public void setText(String text)
    {
        _dialog.setMessage(text);
    }

    public void setIndeterminate(boolean bool)
    {
        _dialog.setIndeterminate(bool);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        _dialog.dismiss();
    }
}
