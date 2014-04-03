package com.learnit.LearnIt.interfaces;

import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.AdapterView;

/**
 * Created by igor on 4/2/14.
 */
public interface IListenerAddWords extends
		IListener,
		TextWatcher,
		MenuItem.OnMenuItemClickListener,
		AdapterView.OnItemClickListener {
}
