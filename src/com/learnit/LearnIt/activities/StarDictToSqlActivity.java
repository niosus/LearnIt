/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.fragments.Dict2SqlFragment;
import com.learnit.LearnIt.fragments.MyProgressDialogFragment;
import com.learnit.LearnIt.fragments.TaskContainerFragment;


public class StarDictToSqlActivity extends Activity implements TaskContainerFragment.OnTaskActionListener {
    protected static final String LOG_TAG = "my_logs";
    Dict2SqlFragment _uiFragment;
    TaskContainerFragment _taskFragment;
    MyProgressDialogFragment _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _uiFragment = new Dict2SqlFragment();

        FragmentManager fragmentManager = getFragmentManager();

        _taskFragment = (TaskContainerFragment) fragmentManager
                .findFragmentByTag(TaskContainerFragment.TAG);
        if (_taskFragment == null)
        {
            _taskFragment = new TaskContainerFragment();
            fragmentManager.beginTransaction()
                    .add(_taskFragment, TaskContainerFragment.TAG)
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
        Log.e(LOG_TAG, "activity paused");
    }

    @Override
    public void onStartLoading() {
        _progressDialog.setText(this.getString(R.string.dict_sql_progress_found));
        _progressDialog.setIndeterminate(false);
    }

    @Override
    public void onStartSearching() {
        _progressDialog.setText(this.getString(R.string.dict_sql_progress_searching));
        _progressDialog.setIndeterminate(true);
    }

    @Override
    public void noDictFound() {
        _progressDialog.dismiss();
        _uiFragment.setTitleText(this.getString(R.string.dict_sql_no_dict));
    }

    @Override
    public void onProgressUpdate(int progress) {
        if (_progressDialog != null)
            _progressDialog.setProgress(progress);
    }

    @Override
    public void onDictLoaded(String name) {
        _progressDialog.dismiss();
        _uiFragment.setTitleText(this.getString(R.string.dict_sql_success));
        _uiFragment.setDictInfoText(name);
    }
}

