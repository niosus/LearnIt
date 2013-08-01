/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.fragments.LoadStarDictUiFragment;
import com.learnit.LearnIt.fragments.LoadStarDictWorker;
import com.learnit.LearnIt.fragments.MyProgressDialogFragment;


public class LoadStarDictActivity extends Activity implements LoadStarDictWorker.OnTaskActionListener {
    protected static final String LOG_TAG = "my_logs";
    LoadStarDictUiFragment _uiFragment;
    LoadStarDictWorker _taskFragment;
    MyProgressDialogFragment _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _uiFragment = new LoadStarDictUiFragment();

        FragmentManager fragmentManager = getFragmentManager();

        _taskFragment = (LoadStarDictWorker) fragmentManager
                .findFragmentByTag(LoadStarDictWorker.TAG);
        if (_taskFragment == null)
        {
            _taskFragment = new LoadStarDictWorker();
            fragmentManager.beginTransaction()
                    .add(_taskFragment, LoadStarDictWorker.TAG)
                    .commit();
        }
        fragmentManager.beginTransaction()
                .add(android.R.id.content, _uiFragment)
                .commit();
        addDialogIfNeeded();
    }

    private void addDialogIfNeeded()
    {
        if (!_taskFragment.DONE)
        {
            _progressDialog = (MyProgressDialogFragment) getFragmentManager().findFragmentByTag("my_progress");
            if (_progressDialog == null)
            {
                _progressDialog = new MyProgressDialogFragment();
                _progressDialog.show(getFragmentManager(),"my_progress");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addDialogIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_progressDialog != null)
        {
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        if (_taskFragment.DONE)
        {
            this.finish();
        }
    }

    @Override
    public void onStartLoading() {
        if (_progressDialog != null)
        {
            _progressDialog.setText(this.getString(R.string.dict_sql_progress_found));
            _progressDialog.setIndeterminate(false);
        }
    }

    @Override
    public void onStartSearching() {
        if (_progressDialog != null)
        {
            _progressDialog.setText(this.getString(R.string.dict_sql_progress_searching));
            _progressDialog.setIndeterminate(true);
        }
    }

    @Override
    public void noDictFound() {
        if (_progressDialog != null)
        {
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        _uiFragment.setTitleText(this.getString(R.string.dict_sql_no_dict));
    }

    @Override
    public void onProgressUpdate(int progress) {
        if (_progressDialog != null)
            _progressDialog.setProgress(progress);
    }

    @Override
    public void onDictLoaded(String name) {
        if (_progressDialog != null)
        {
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        _uiFragment.setTitleText(this.getString(R.string.dict_sql_success));
        _uiFragment.setDictInfoText(name);
    }
}

