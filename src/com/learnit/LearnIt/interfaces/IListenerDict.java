package com.learnit.LearnIt.interfaces;

import android.text.TextWatcher;
import android.view.ActionMode;
import android.widget.AdapterView;

/**
 * Created by igor on 4/2/14.
 */
public interface IListenerDict extends
		IListener,
		TextWatcher,
		AdapterView.OnItemLongClickListener,
		ActionMode.Callback{
}
