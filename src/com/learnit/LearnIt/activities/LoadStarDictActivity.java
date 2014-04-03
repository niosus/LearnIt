/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetDictTask;
import com.learnit.LearnIt.fragments.LoadStarDictUiFragment;
import com.learnit.LearnIt.fragments.MyProgressDialogFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.interfaces.IWorkerEventListener;

import java.util.List;
import java.util.Map;


public class LoadStarDictActivity extends Activity implements IWorkerEventListener {
    protected static final String LOG_TAG = "my_logs";
    LoadStarDictUiFragment _uiFragment;
	WorkerFragment _taskFragment;
    MyProgressDialogFragment _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _uiFragment = new LoadStarDictUiFragment();
		Log.d(LOG_TAG, "onCreate LoadStarDictActivity");
        FragmentManager fragmentManager = getFragmentManager();

        _taskFragment = (WorkerFragment) fragmentManager
                .findFragmentByTag(WorkerFragment.TAG);
        if (_taskFragment == null)
        {
            _taskFragment = new WorkerFragment();
	        _taskFragment.addTask(new GetDictTask());
            fragmentManager.beginTransaction()
                    .add(_taskFragment, WorkerFragment.TAG)
                    .commit();
        }
        fragmentManager.beginTransaction()
                .add(android.R.id.content, _uiFragment)
                .commit();
        addDialogIfNeeded();
    }

	@Override
	protected void onResume() {
		super.onResume();
		addDialogIfNeeded();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	private void addDialogIfNeeded()
    {
        if (_taskFragment.taskRunning())
        {
            _progressDialog = (MyProgressDialogFragment) getFragmentManager().findFragmentByTag(MyProgressDialogFragment.TAG);
            if (_progressDialog == null)
            {
                _progressDialog = new MyProgressDialogFragment();
                _progressDialog.show(getFragmentManager(), MyProgressDialogFragment.TAG);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_progressDialog != null)
        {
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        if (!_taskFragment.taskRunning())
        {
            this.finish();
        }
    }

	@Override
	public void onPreExecute() {
		if (_progressDialog != null)
		{
			_progressDialog.setText(this.getString(R.string.dict_sql_progress_searching_indexing));
			_progressDialog.setIndeterminate(false);
		}
	}

	@Override
	public void onFail() {
		if (_progressDialog != null)
		{
			_progressDialog.dismiss();
			_progressDialog = null;
		}
		if (_uiFragment != null)
		{
			_uiFragment.setTitleText(this.getString(R.string.dict_sql_no_dict));
		}
		if (_taskFragment != null)
		{
			_taskFragment.onTaskFinished();
		}
	}

	@Override
	public void onSuccessString(String result) {
		if (_progressDialog != null)
		{
			_progressDialog.dismiss();
			_progressDialog = null;
		}
		if (_uiFragment != null)
		{
			_uiFragment.setTitleText(this.getString(R.string.dict_sql_success));
			_uiFragment.setDictInfoText((String)result);
		}
		if (_taskFragment != null)
		{
			_taskFragment.onTaskFinished();
		}
	}

	@Override
	public void onSuccessCode(Integer errorCode) {

	}

	@Override
	public void onProgressUpdate(Integer... values) {
		if (_progressDialog != null)
		{
			_progressDialog.setProgress(values[0]);
		}
	}

	@Override
	public void onSuccessWords(List<String> result) {

	}

	@Override
	public void onSuccessTranslations(Pair<String, List<String>> result) {

	}

	@Override
	public void onSuccessMyWords(List<Map<String, String>> result) {

	}

	@Override
	public void noTaskSpecified() {
		Log.d(LOG_TAG, "no task, careful");
	}

	@Override
	public void onTaskFinished() {

	}

	@Override
	public boolean taskRunning() {
		return false;
	}
}

