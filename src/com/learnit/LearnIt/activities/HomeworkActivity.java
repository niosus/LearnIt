/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.learnit.LearnIt.fragments.HomeworkFragment;
import com.learnit.LearnIt.fragments.WorkerFragment;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

public class HomeworkActivity extends Activity {
    int fromLearnToKnow = 0;
    final String LOG_TAG = "my_logs";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fragmentManager = getFragmentManager();

		// add a headless worker fragment to stack if not yet there
		Fragment worker = fragmentManager
				.findFragmentByTag(WorkerFragment.TAG);
		if (worker == null)
		{
			worker = new WorkerFragment();
			fragmentManager.beginTransaction()
					.add(worker, WorkerFragment.TAG)
					.commit();
		}

		// add a ui fragment to stack
		Fragment uiFragment = fragmentManager
				.findFragmentByTag(HomeworkFragment.TAG);
		if (uiFragment == null) {
			if (worker instanceof IWorkerJobInput) {
				uiFragment = new HomeworkFragment((IWorkerJobInput) worker);
				// extras contain words, translations and so on that we need to show
				// the data in the homework fragment. We pass them on to the fragment.
				uiFragment.setArguments(getIntent().getExtras());
				fragmentManager.beginTransaction()
						.add(android.R.id.content, uiFragment, HomeworkFragment.TAG)
						.commit();
			}
		}
	}



//	private void setAllTexts()
//	{
//		Utils.updateCurrentDBName(this);
//		dbHelper = new DBHelper(this, DBHelper.DB_WORDS);
//		Random random = new Random();
//		int randIdx = random.nextInt(btnIds.length);
//		_myBtnOnClick.correct = btnIds[randIdx];
//		TextView queryWordTextView = (TextView) findViewById(R.id.word_to_ask);
//		queryWordTextView.setMovementMethod(new ScrollingMovementMethod());
//		setBtnTexts(randIdx);
//		switch (fromLearnToKnow) {
//			case Constants.FROM_FOREIGN_TO_MY:
//				_sp = PreferenceManager.getDefaultSharedPreferences(this);
//				String learnLang = _sp.getString(getString(R.string.key_language_from), "null");
//				if (null != correctEntry.article) {
//					if ("de".equals(learnLang)) {
//						queryWordTextView.setText(correctEntry.article + " " + StringUtils.capitalize(correctEntry.word));
//					} else {
//						queryWordTextView.setText(correctEntry.article + " " + correctEntry.word);
//					}
//				} else if (null != correctEntry.prefix) {
//					queryWordTextView.setText(correctEntry.prefix + " " + correctEntry.word);
//				} else {
//					queryWordTextView.setText(correctEntry.word);
//				}
//				break;
//			case Constants.FROM_MY_TO_FOREIGN:
//				queryWordTextView.setText(correctEntry.translation);
//				break;
//		}
//	}

    public void stopActivity() {
        finish();
    }

}