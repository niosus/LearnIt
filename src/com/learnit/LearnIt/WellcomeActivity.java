package com.learnit.LearnIt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

public class WellcomeActivity extends FragmentActivity {
    private final String LOG_TAG = "my_logs";
    Button btnSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wellcome);
        btnSettings = (Button) findViewById(R.id.btn_go_to_settings);
        btnSettings.setOnClickListener(onClickListener);
    }
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLanguage = sp.getString(getString(R.string.key_language_from), "NONE");
        Log.d(LOG_TAG,"selected language = " + selectedLanguage);
        Resources res = getResources();
        String[] languages = res.getStringArray(R.array.values_languages_from);
        String allLanguages = Arrays.toString(languages);
        Log.d(LOG_TAG,"possible languages = " + allLanguages);
        if (allLanguages.contains(selectedLanguage))
        {
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
            if (view.getId()==btnSettings.getId())
            {
                startSettingsActivity();
            }
        }
    };

    @Override
    public void onBackPressed() {
    }
}
