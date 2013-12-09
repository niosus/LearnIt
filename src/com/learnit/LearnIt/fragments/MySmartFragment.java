/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.LearnIt.interfaces.OnUiAction;
import com.learnit.LearnIt.utils.Utils;
public abstract class MySmartFragment extends Fragment{
    protected static final String LOG_TAG = "my_logs";
	protected View _view;
	public int identifier;
	protected OnUiAction _callback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnUiAction)
		{
			_callback = (OnUiAction) activity;
		}
		else
		{
			throw new ClassCastException(activity.getClass().getName() + " should implement " + OnUiAction.class.getName());
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        Utils.updateCurrentDBName(this.getActivity());
    }

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
}