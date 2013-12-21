package com.learnit.LearnIt.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by igor on 12/15/13.
 */
public class MyWordTextView extends TextView {

	public MyWordTextView(Context context) {
		super(context);
		this.setText("test");
	}

	public MyWordTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setText("test");
	}

	public MyWordTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setText("test");
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		this.scrollTo(0, 0);
		Log.d("my_logs", "query word setText " + text);
		String str = text.toString();
		String[] split = str.split("\\s");
		int maxLength = 0;
		int margin = 20;
		for (String word: split) {
			Log.d("my_logs", "split on query word setText " + word);
			if (word.length() > maxLength) {
				maxLength = word.length();
			}
		}
		float sizeX = 100000;
		float sizeDefault = 60;
		if (maxLength > 0)
		{
			sizeX = (this.getWidth() - 2 * margin)/ maxLength;
		}
		this.setTextSize(Math.min(sizeX, sizeDefault));
	}
}
