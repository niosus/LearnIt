package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.utils.Constants;

public class MyProgressDialogFragment extends DialogFragment {
    ProgressDialog _dialog;
    Context _context;

	public static final String TAG = "my_progress";


	public void setContext(Context context)
	{
		_context = context;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _context = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
	    Log.e(Constants.LOG_TAG, "onStop progress");
	    _dialog = null;
    }

    private Dialog createDialog()
    {
	    Log.e(Constants.LOG_TAG, "createProgressDialog");
        _dialog = new ProgressDialog(_context);
        _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _dialog.setProgressNumberFormat("");
        _dialog.setMessage(getString(R.string.dict_sql_progress_searching_indexing));
        _dialog.setIndeterminate(false);
        _dialog.setCancelable(true);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        keyEvent.getAction() == KeyEvent.ACTION_UP &&
                        !keyEvent.isCanceled()) {
                    return true;
                }
                return false;
            }
        });
        return _dialog;
    }

    public void setProgress(int i)
    {
        if (_dialog == null)
        {
	        Log.e(Constants.LOG_TAG, "_dialog is null in setProgress");
	        return;
        }
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
        if (_dialog == null){ return; }
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
