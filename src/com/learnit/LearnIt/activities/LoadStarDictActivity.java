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

package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.async_tasks.GetDictTask;
import com.learnit.LearnIt.fragments.LoadStarDictUiFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerGetDict;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class LoadStarDictActivity extends Activity implements
		IWorkerEventListenerGetDict {
    protected static final String LOG_TAG = "my_logs";
    LoadStarDictUiFragment _uiFragment;
	IWorkerJobInput _jobStarter;
	int _backPressedCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    this.setFinishOnTouchOutside(false);
	    _backPressedCounter = 0;
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
		_backPressedCounter++;
		if (_uiFragment.isDictLoaded()) {
			super.onBackPressed();
		} else if (_backPressedCounter > 1) {
			_jobStarter.cancelCurrentTask();
			super.onBackPressed();
		} else {
			Crouton crouton;
			crouton = Crouton.makeText(this, getString(R.string.crouton_back_pressed_once), Style.ALERT);
			crouton.setConfiguration(new Configuration.Builder().setDuration(3000).build());
			crouton.show();
		}
	}

	@Override
    protected void onPause() {
        super.onPause();
        if (!_jobStarter.taskRunning())
        {
	        Crouton.cancelAllCroutons();
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

