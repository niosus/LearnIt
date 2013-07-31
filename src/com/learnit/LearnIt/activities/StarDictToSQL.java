/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.os.Bundle;
import com.learnit.LearnIt.fragments.Dict2SqlFragment;
import com.learnit.LearnIt.fragments.TaskContainerFragment;


public class StarDictToSQL extends Activity implements TaskContainerFragment.OnTaskActionListener {
    protected static final String LOG_TAG = "my_logs";
    Dict2SqlFragment _uiFragment;
    TaskContainerFragment _taskFragment;

    @Override
    protected void onResume() {
        super.onResume();
        _uiFragment = new Dict2SqlFragment();
        _taskFragment = new TaskContainerFragment();
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, _uiFragment)
                .commit();
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, _taskFragment)
                .commit();
        _taskFragment.executeTask(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStartLoading() {
        _uiFragment.setStateLoading();
    }

    @Override
    public void onStartSearching() {
        _uiFragment.setStateSearching();
    }

    @Override
    public void noDictFound() {
        _uiFragment.noDictFound();
    }

    @Override
    public void onProgressUpdate(int progress) {
        _uiFragment.onProgressUpdate(progress);
    }

    @Override
    public void onDictLoaded(String name) {
        _uiFragment.onDictLoaded(name);
    }
}

