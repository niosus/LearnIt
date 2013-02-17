package com.learnit.LearnIt;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

public class InfoActivity extends FragmentActivity {
    private final String LOG_TAG = "my_logs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        TextView txtVersion = (TextView) findViewById(R.id.instructions_title);
        String currentstr = txtVersion.getText().toString();
        Log.d(LOG_TAG,currentstr);
        txtVersion.setText(String.format(currentstr,getString(R.string.version)));
    }
}
