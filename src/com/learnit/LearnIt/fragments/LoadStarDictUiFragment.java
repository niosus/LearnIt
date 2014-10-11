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

package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.learnit.LearnIt.R;

public class LoadStarDictUiFragment extends Fragment {
	public final static String TAG = "ui_load_dict";

    private TextView _tvTitle;
	private TextView _tvDictInfo;
	private TextView _tvMayClose;
    CircularProgressButton _circularProgressButton;
	boolean dictLoaded = false;

	public boolean isDictLoaded() {
		return dictLoaded;
	}

    @Override
    public void onAttach(Activity activity) {
	    super.onAttach(activity);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dict_to_sql, container, false);
        _tvTitle = (TextView) v.findViewById(R.id.text_dict_to_sql_title);
        _tvDictInfo = (TextView) v.findViewById(R.id.text_dictionary_info);
	    _tvMayClose = (TextView) v.findViewById(R.id.text_dict_to_sql_can_close_this);
	    _tvDictInfo.setVisibility(View.INVISIBLE);
	    _tvMayClose.setVisibility(View.INVISIBLE);
        String title = null;
        String dictInfo = null;
        if (savedInstanceState!=null)
        {
            title = savedInstanceState.getString("Title");
            dictInfo = savedInstanceState.getString("DictInfo");
        }
        if (title!=null)
        {
            _tvTitle.setText(title);
            _tvDictInfo.setText(dictInfo);
        }

        int SOME_VAL = 50;
        _circularProgressButton = (CircularProgressButton) v.findViewById(R.id.circularProgressButton);
        _circularProgressButton.setIndeterminateProgressMode(true);
        _circularProgressButton.setProgress(SOME_VAL);

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setRetainInstance(true);
    }

    private void setTitleText(String text)
    {
        if (_tvTitle!=null)
            _tvTitle.setText(text);
    }

    private void setDictInfoText(String text)
    {
        if (_tvDictInfo!=null) {
	        _tvDictInfo.setText(text);
        }
    }

	public void onSuccess(String dictInfoText) {
		if (isAdded()) {
			setDictInfoText(dictInfoText);
			setTitleText(this.getString(R.string.dict_sql_success));
			_tvDictInfo.setVisibility(View.VISIBLE);
			_tvMayClose.setVisibility(View.VISIBLE);
			dictLoaded = true;
		}
	}

	public void onFail() {
		if (isAdded()) {
			setTitleText(this.getString(R.string.dict_sql_no_dict));
            _circularProgressButton.setProgress(CircularProgressButton.ERROR_STATE_PROGRESS);
        }
	}

	public void setProgress(Double i) {
        if (_circularProgressButton == null)
        {
            return;
        }
        int intProgress = (int) Math.floor(i);
        if (intProgress < 1) { return; }
		if (_circularProgressButton.isIndeterminateProgressMode()) {
            _circularProgressButton.setIndeterminateProgressMode(false);
        }
        _circularProgressButton.setProgress((int) Math.floor(i));
	}
}
