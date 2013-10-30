package com.learnit.LearnIt.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
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

	public <T> void invokeForView(View v, int animationId, T fragment)
	{
		try {
			_callback = (OnAnimationActionListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + " must implement OnAnimationActionListener");
		}
		Animation anim = getAnimByType(animationId);
		MyAnimationListener animListener = new MyAnimationListener(animationId);
		anim.setAnimationListener(animListener);
		v.startAnimation(anim);
	}


	public <T> void invokeForAllViews(View[] views, int animationId, T fragment)
	{
		try {
			_callback = (OnAnimationActionListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + " must implement OnAnimationActionListener");
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


