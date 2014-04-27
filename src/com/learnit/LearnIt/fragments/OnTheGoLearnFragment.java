/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.controllers.LearnOnTheGoController;
import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.interfaces.IWorkerJobInput;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.MyAnimationHelper;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.views.WordButton;

import java.util.ArrayList;


public class OnTheGoLearnFragment extends LearnFragment {
	public static final String TAG = "homework_frag";

	private int[] _btnIds = {
			R.id.left_top_button,
			R.id.right_top_button,
			R.id.left_bottom_button,
			R.id.right_bottom_button };

	@Override
	protected int[] btnIds() {
		return _btnIds;
	}

	public OnTheGoLearnFragment(IWorkerJobInput worker) {
		super();
		_listener = new LearnOnTheGoController(this, worker, btnIds());
	}

	//	TODO: change the names of the strings to some constants !!! important
	boolean restoreFromPreferences() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String word = sp.getString("word", null);
		String button1Text = sp.getString("button1", null);
		String button2Text = sp.getString("button2", null);
		String button3Text = sp.getString("button3", null);
		String button4Text = sp.getString("button4", null);
		int correctId = sp.getInt("correctId", 0);
		if (word == null
				|| button1Text == null
				|| button2Text == null
				|| button3Text == null
				|| button4Text == null
				|| correctId == 0) { return false; }
		((TextView) v.findViewById(R.id.word_to_ask)).setText(word);
		((TextView) v.findViewById(R.id.left_top_button)).setText(button1Text);
		((TextView) v.findViewById(R.id.right_bottom_button)).setText(button2Text);
		((TextView) v.findViewById(R.id.left_bottom_button)).setText(button3Text);
		((TextView) v.findViewById(R.id.right_top_button)).setText(button4Text);
		_listener.setCorrectWordIdFromPrefs(correctId);
		String[] split = word.split("\n");
		queryWord = split[split.length - 1];
		sp.edit().remove("word")
				.remove("button1")
				.remove("button2")
				.remove("button3")
				.remove("button4")
				.remove("correctId")
				.commit();
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!restoreFromPreferences()) {
			_listener.showNext();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		sp.edit().putString("word", _wordToAsk.getText().toString())
				.putString("button1", ((TextView) v.findViewById(R.id.left_top_button)).getText().toString())
				.putString("button2", ((TextView) v.findViewById(R.id.right_bottom_button)).getText().toString())
				.putString("button3", ((TextView) v.findViewById(R.id.left_bottom_button)).getText().toString())
				.putString("button4", ((TextView) v.findViewById(R.id.right_top_button)).getText().toString())
				.putInt("correctId", _listener.getCorrectWordId())
				.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.homework, container, false);
		updateDirectionOfTranslation();
		_wordToAsk = (TextView) v.findViewById(R.id.word_to_ask);
		_wordToAsk.setMovementMethod(new ScrollingMovementMethod());
		for (int id: _btnIds) {
			(v.findViewById(id)).setOnClickListener(_listener);
		}
		return v;
	}

	@Override
	public void openButtons() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
		View[] viewsBottom = {
				v.findViewById(R.id.second_button_layout),
				v.findViewById(R.id.right_bottom_button),
				v.findViewById(R.id.left_bottom_button)};
		View[] viewsTop = {
				v.findViewById(R.id.first_button_layout),
				v.findViewById(R.id.left_top_button),
				v.findViewById(R.id.right_top_button)};
		animationHelper.invokeForAllViews(viewsBottom, R.anim.float_in_up_first_row, _listener);
		animationHelper.invokeForAllViews(viewsTop, R.anim.float_in_up_second_row, _listener);
	}

	@Override
	public void closeButtons() {
		MyAnimationHelper animationHelper = new MyAnimationHelper(this.getActivity());
		View[] viewsBottom = {
				v.findViewById(R.id.second_button_layout),
				v.findViewById(R.id.right_bottom_button),
				v.findViewById(R.id.left_bottom_button)};
		View[] viewsTop = {
				v.findViewById(R.id.first_button_layout),
				v.findViewById(R.id.left_top_button),
				v.findViewById(R.id.right_top_button)};
		animationHelper.invokeForAllViews(viewsBottom, R.anim.float_away_down_first_row, _listener);
		animationHelper.invokeForAllViews(viewsTop, R.anim.float_away_down_second_row, _listener);
	}

	@Override
	public void setButtonTexts(ArrayList<ArticleWordId> words, int direction) {
		if (direction > 0) { _direction = direction; }
		int showOnButton;
		switch (_direction) {
			case Constants.FROM_FOREIGN_TO_MY:
				showOnButton = WordButton.SHOW_TRANSLATION;
				break;
			case Constants.FROM_MY_TO_FOREIGN:
				showOnButton = WordButton.SHOW_WORD;
				break;
			default:
				showOnButton = 0; //won't show anything on button
		}

		// actually set the texts to the buttons
		for (int i = 0; i < btnIds().length; ++i) {
			WordButton tempButton = (WordButton) v.findViewById(btnIds()[i]);
			if (i < words.size() && showOnButton > 0) {
				tempButton.setEnabled(true);
				tempButton.setText(words.get(i), showOnButton);
			} else {
				tempButton.setEnabled(false);
				tempButton.setText("");
			}
		}
	}

	@Override
	public void setQueryWordText(ArticleWordId struct, int direction) {
		TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
		queryWord = struct.word;
		if (direction > 0) { _direction = direction; }
		switch (_direction) {
			case Constants.FROM_FOREIGN_TO_MY:
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
				String learnLang = sp.getString(getString(R.string.key_language_from), "null");
				if (null != struct.article) {
					// a small hack for German and capitalization of all nouns
					if ("de".equals(learnLang)) {
						queryWordTextView.setText(struct.article + "\n" + StringUtils.capitalize(queryWord));
					} else {
						queryWordTextView.setText(struct.article + "\n" + queryWord);
					}
				} else if (null != struct.prefix) {
					queryWordTextView.setText(struct.prefix + " " + queryWord);
				} else {
					queryWordTextView.setText(queryWord);
				}
				break;
			case Constants.FROM_MY_TO_FOREIGN:
				queryWordTextView.setText(StringUtils.splitOnRegex(struct.translation, ",|\\s"));
				break;
		}
	}

	public void stopActivity() {
		this.getActivity().finish();
	}
}