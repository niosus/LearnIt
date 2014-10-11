
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

package com.learnit.LearnIt.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.learnit.LearnIt.fragments.PreferencesUiFragment;

public class PrefActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    this.setFinishOnTouchOutside(false);
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) { actionBar.setTitle(""); }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesUiFragment())
                .commit();
    }
}