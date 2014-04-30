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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.learnit.LearnIt.R;

import java.util.Arrays;

public class WelcomeActivity extends FragmentActivity {
    private final String LOG_TAG = "my_logs";
    Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        btnSettings = (Button) findViewById(R.id.btn_go_to_settings);
        btnSettings.setOnClickListener(onClickListener);
        TextView txtVersion = (TextView) findViewById(R.id.instructions_title);
        String currentstr = txtVersion.getText().toString();
        Log.d(LOG_TAG, currentstr);
        txtVersion.setText(String.format(currentstr, getString(R.string.version)));
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLanguage = sp.getString(getString(R.string.key_language_from), "NONE");
        Log.d(LOG_TAG, "selected language = " + selectedLanguage);
        Resources res = getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        Log.d(LOG_TAG, "possible languages = " + allLanguages);
        if (allLanguages.contains(selectedLanguage)) {
            this.finish();
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "start activity called");
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnSettings.getId()) {
                startSettingsActivity();
            }
        }
    };

    @Override
    public void onBackPressed() {
    }
}
