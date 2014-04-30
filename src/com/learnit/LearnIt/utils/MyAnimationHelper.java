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

package com.learnit.LearnIt.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 2/16/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyAnimationHelper {
	private OnAnimationActionListener _callback;
	private Context _context;
	public interface OnAnimationActionListener {
		public void onAnimationFinished(int id, boolean ignore);
	}

	public MyAnimationHelper(Context context)
	{
		_context = context;
	}

	private Animation getAnimByType(int id)
	{
		return AnimationUtils.loadAnimation(_context, id);
	}

	public <T> void invokeForView(View v, int animationId, T callback)
	{
		try {
			_callback = (OnAnimationActionListener) callback;
		} catch (ClassCastException e) {
			throw new ClassCastException(callback.getClass().getSimpleName() + " must implement OnAnimationActionListener");
		}
		Animation anim = getAnimByType(animationId);
		MyAnimationListener animListener = new MyAnimationListener(animationId);
		anim.setAnimationListener(animListener);
		v.startAnimation(anim);
	}


	public <T> void invokeForAllViews(View[] views, int animationId, T callback)
	{
		try {
			_callback = (OnAnimationActionListener) callback;
		} catch (ClassCastException e) {
			throw new ClassCastException(callback.getClass().getSimpleName() + " must implement OnAnimationActionListener");
		}
		Animation animWithListener = getAnimByType(animationId);
		MyAnimationListener animListener = new MyAnimationListener(animationId, false);
		animWithListener.setAnimationListener(animListener);
		for (View v:views)
		{
			v.startAnimation(animWithListener);
		}

	}

	public class MyAnimationListener implements Animation.AnimationListener
	{
		private int _animationId;
		private boolean _ignore;

		public MyAnimationListener(int id, boolean throwCallback)
		{
			_animationId = id;
			_ignore = throwCallback;
		}

		public MyAnimationListener(int id)
		{
			_animationId = id;
			_ignore = false;
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			_callback.onAnimationFinished(_animationId, _ignore);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}
}


