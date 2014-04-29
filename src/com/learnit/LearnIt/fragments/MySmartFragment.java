/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Utils;

public abstract class MySmartFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
	protected View _v;
	public int identifier;

    @Override
    public void onResume() {
        super.onResume();
        Utils.updateCurrentDBName(this.getActivity());
    }

	public void attachWorker(IWorkerJobInput worker) {
		// do nothing here, should be caught by children
		// bad design?
	}

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
}