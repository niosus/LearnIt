package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.learnit.LearnIt.R;

public class MyProgressDialog extends DialogFragment {
    ProgressDialog _dialog;
    Context _context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _context = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        _dialog = new ProgressDialog(_context);
        _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _dialog.setProgressNumberFormat("");
        _dialog.setMessage(getString(R.string.dict_sql_progress_searching));
        _dialog.setIndeterminate(false);
        return _dialog;
    }

    public void setProgress(int i)
    {
        if (_dialog == null)
            return;
        _dialog.setProgress(i);
    }

    public void setText(String text)
    {
        if (_dialog == null)
            return;
        _dialog.setMessage(text);
    }

    public void setIndeterminate(boolean bool)
    {
        if (_dialog == null)
            return;
        _dialog.setIndeterminate(bool);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (_dialog == null)
            return;
        _dialog.dismiss();
    }
}
