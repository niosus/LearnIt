package com.learnit.LearnIt.controllers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.data_types.NotificationBuilder;
import com.learnit.LearnIt.fragments.HomeworkFragment;
import com.learnit.LearnIt.interfaces.ILearnFragmentUpdate;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by igor on 4/2/14.
 */
public class LearnHomeworkTranslationController extends LearnController {
	public static final String LOG_TAG = "my_logs";
	private static final String ALIVE_IDS_TAG = "current_ids";
	private ArrayList<Integer> _ids;
	private ArrayList<String> _words;
	private ArrayList<String> _translations;
	private ArrayList<Integer> _directionsOfTrans;
	private ArrayList<String> _articles;
	private ArrayList<String> _prefixes;
	private int _currentNotificationIndex;
	ArticleWordId correctEntry = null;
	int fromLearnToKnow;
	Context _context;

	public LearnHomeworkTranslationController(ILearnFragmentUpdate target, IWorkerJobInput worker, int[] btnIds) {
		super(target, worker, btnIds);
	}

	public void getEverythingFromExtras(Bundle extras, Context context) {
		if (context == null) { throw new NullPointerException("context is null in LearnHomeworkTranslationController"); }
		_context = context;
		if (extras == null) { throw new NullPointerException("extras are null in LearnHomeworkTranslationController"); }
		_ids = extras.getIntegerArrayList(NotificationBuilder.IDS_TAG);
		_words = extras.getStringArrayList(NotificationBuilder.WORDS_TAG);
		_translations = extras.getStringArrayList(NotificationBuilder.TRANSLATIONS_TAG);
		_directionsOfTrans = extras.getIntegerArrayList(NotificationBuilder.DIRECTIONS_OF_TRANS_TAG);
		_articles = extras.getStringArrayList(NotificationBuilder.ARTICLES_TAG);
		_prefixes = extras.getStringArrayList(NotificationBuilder.PREFIXES_TAG);
		_currentNotificationIndex = extras.getInt(NotificationBuilder.CURRENT_NOTIFICATION_INDEX, -1);
		findNextId();
		fetchRandomWords(_btnIds.length - 1, correctEntry.word);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == _btnIds[_correctAnswerId]) {
			NotificationManager notificationManager =
					(NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(correctEntry.id);
			_fragmentUpdate.updateWordWeight(_numOfWrongAnswers);
			_numOfWrongAnswers = 0;
			updateListOfAliveIds();
			if (findNextId()) {
				_fragmentUpdate.closeWord();
				_fragmentUpdate.closeButtons();
			} else {
				if (_fragmentUpdate instanceof HomeworkFragment) {
					((HomeworkFragment) _fragmentUpdate).stopActivity();
				}
			}
		} else {
			if (v instanceof Button) {
				_numOfWrongAnswers++;
				_fragmentUpdate.shakeView(v);
			}
		}
	}

	private boolean findNextId()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
		String idsOld = sp.getString(ALIVE_IDS_TAG, "");
		int counter=0;
		while (!idsOld.contains(_ids.get(_currentNotificationIndex).toString()))
		{
			_currentNotificationIndex++;
			_currentNotificationIndex%=_ids.size();
			if (counter++==_ids.size())
				return false;
		}
		correctEntry = new ArticleWordId(
				_articles.get(_currentNotificationIndex),
				_prefixes.get(_currentNotificationIndex),
				_words.get(_currentNotificationIndex),
				_translations.get(_currentNotificationIndex),
				_ids.get(_currentNotificationIndex)
		);
		fromLearnToKnow = _directionsOfTrans.get(_currentNotificationIndex);
		Log.d(LOG_TAG, "got intent word=" + correctEntry.word + " id = "
				+ correctEntry.id);
		return true;
	}

	protected void updateListOfAliveIds()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
		Log.d(LOG_TAG, sp.toString());
		SharedPreferences.Editor editor = sp.edit();
		String idsOld = sp.getString(ALIVE_IDS_TAG, "");
		Log.d(LOG_TAG, idsOld);
		String idsNew="";
		for (Integer idInt: _ids)
		{
			if (idInt==correctEntry.id)
				continue;
			if (!idsOld.contains(idInt.toString()))
				continue;
			idsNew+=idInt.toString()+" ";
		}
		editor.putString(ALIVE_IDS_TAG, idsNew);
		Log.d(LOG_TAG, idsNew);
		editor.commit();
	}

	@Override
	public void onSuccessRandomWords(ArrayList<ArticleWordId> articleWordIds) {
		_worker.onTaskFinished();
		if (articleWordIds.size() != _btnIds.length - 1) {
			Log.e("my_logs", "random words number is wrong in LearnHomeworkTranslationController");
			return;
		}
		_fragmentUpdate.updateDirectionOfTranslation();
		Random rand = new Random();
		_correctAnswerId = rand.nextInt(_btnIds.length);
		articleWordIds.add(_correctAnswerId, correctEntry);
		_fragmentUpdate.setQueryWordText(correctEntry);
		_fragmentUpdate.setButtonTexts(articleWordIds);
		_fragmentUpdate.openButtons();
		_fragmentUpdate.openWord();
		_fragmentUpdate.setAll(View.VISIBLE);
	}

	@Override
	public void onAnimationFinished(int id, boolean ignore) {
		if (ignore)
			return;
		switch (id)
		{
			case (R.anim.float_away_down_second_row):
				_fragmentUpdate.setAll(View.INVISIBLE);
				break;
			case (R.anim.close_word):
				_fragmentUpdate.setAll(View.INVISIBLE);
				fetchRandomWords(_btnIds.length - 1, correctEntry.word);
				break;
		}
	}
}
