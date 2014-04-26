/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.os.Bundle;
import android.view.View;

import com.learnit.LearnIt.controllers.LearnHomeworkTranslationController;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;


public class HomeworkFragment extends LearnFragment {
	public static final String TAG = "homework_frag";

	public HomeworkFragment(IWorkerJobInput worker) {
		super(worker);
		_listener = new LearnHomeworkTranslationController(this, worker, btnIds);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle extras = getArguments();
		if (_listener instanceof LearnHomeworkTranslationController) {
			((LearnHomeworkTranslationController) _listener).getEverythingFromExtras(extras, this.getActivity());
		}
	}

	public void stopActivity() {
		this.getActivity().finish();
	}
}