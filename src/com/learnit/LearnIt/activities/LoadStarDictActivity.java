/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.learnit.LearnIt.async_tasks.GetDictTask;
import com.learnit.LearnIt.fragments.LoadStarDictUiFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerGetDict;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;


public class LoadStarDictActivity extends Activity implements
		IWorkerEventListenerGetDict {
    protected static final String LOG_TAG = "my_logs";
    LoadStarDictUiFragment _uiFragment;
	IWorkerJobInput _jobStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate LoadStarDictActivity");
        FragmentManager fragmentManager = getFragmentManager();

	    // add a ui fragment to stack
	    _uiFragment = (LoadStarDictUiFragment) fragmentManager
			    .findFragmentByTag(LoadStarDictUiFragment.TAG);
	    if (_uiFragment == null) {
		    _uiFragment = new LoadStarDictUiFragment();
		    fragmentManager.beginTransaction()
				    .add(android.R.id.content, _uiFragment, LoadStarDictUiFragment.TAG)
				    .commit();
	    }

//	    add a headless worker fragment to stack if not yet there
        Fragment worker = fragmentManager
                .findFragmentByTag(WorkerFragment.TAG);
        if (worker == null)
        {
            worker = new WorkerFragment();
            fragmentManager.beginTransaction()
		            .add(worker, WorkerFragment.TAG)
                    .commit();
        }

//		set _jobStarter from the worker fragment if possible
//	    throw exception if the fragment is wrong
	    if (worker instanceof IWorkerJobInput) {
		    _jobStarter = (IWorkerJobInput) worker;
	    } else {
		    throw new ClassCastException(worker.getClass().getSimpleName() + "should implement" + IWorkerJobInput.class.getSimpleName());
	    }
    }

	@Override
	protected void onResume() {
		super.onResume();
		if (!_jobStarter.taskRunning() && !_uiFragment.isDictLoaded()) {
			_jobStarter.addTask(new GetDictTask(), this);
		} else {
			_jobStarter.attach(this);
		}
	}

	@Override
	public void onBackPressed() {
		if (_uiFragment.isDictLoaded()) {
			super.onBackPressed();
		} else {
			_jobStarter.cancelCurrentTask();
			super.onBackPressed();
		}
	}

	@Override
    protected void onPause() {
        super.onPause();
        if (!_jobStarter.taskRunning())
        {
            this.finish();
        }
    }

	@Override
	public void onPreExecute() {
	}

	@Override
	public void onFail() {
		if (_uiFragment != null)
		{
			_uiFragment.onFail();
		}
		if (_jobStarter != null)
		{
			_jobStarter.onTaskFinished();
		}
	}

	@Override
	public void onProgressUpdate(Double... values) {
		_uiFragment.setProgress(values[0]);
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

	@Override
	public void onSuccessDictName(String result) {
		Log.e(LOG_TAG, "RESULT from loading dict");
		if (_uiFragment != null)
		{
			_uiFragment.onSuccess(result);
		}
		if (_jobStarter != null)
		{
			_jobStarter.onTaskFinished();
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Utils.removeOldSavedValues(sp, Constants.btnIdsTranslations);
	}
}

