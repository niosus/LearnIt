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

package com.learnit.LearnIt.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.learnit.LearnIt.R;

public class FittingTextView extends TextView {

	public FittingTextView(Context context) {
		super(context);
	}

	public FittingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FittingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		this.setTextScaleX(1.0f);
		this.scrollTo(0, 0);
		float widgetWidth = getWidgetWidth();
		Log.d("my_logs", "query word setText " + text);
		String longestWord = getLongestWord(text);
		updateScale(widgetWidth, longestWord);
	}

	private float getWidgetWidth() {
		if (this.getWidth() < 1) {
			this.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
			return this.getMeasuredWidth();
		} else {
			return this.getWidth();
		}
	}

	private String getLongestWord(final CharSequence text) {
		String str = text.toString();
		String[] split = str.split("\\s");
		float maxLength = 0;
		String longestWord = null;
		for (String word: split) {
			Log.d("my_logs", "split on query word setText " + word);
			if (word.length() > maxLength) {
				maxLength = word.length();
				longestWord = word;
			}
		}
		return longestWord;
	}

	private void updateScale(final float widgetWidth, final String longestWord) {
		float margin = getContext().getResources().getDimension(R.dimen.my_query_word_margin);

		Paint mPaint;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(5);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setTextSize(this.getTextSize());
		mPaint.setTypeface(this.getTypeface());
		if (longestWord != null)
		{
			float w = mPaint.measureText(longestWord, 0, longestWord.length());
			float scale = (widgetWidth - margin * 3)/ w;
			if (scale < 1) {this.setTextScaleX(scale);}
			Log.d("my_logs", "current scale is " + this.getTextScaleX());
		}
	}
}
