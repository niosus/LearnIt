package com.learnit.LearnIt;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import com.learnit.LearnIt.utils.Constants;

public class InfoActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        TextView txtVersion = (TextView) findViewById(R.id.instructions_title);
        String currentStr = txtVersion.getText().toString();
        Log.d(Constants.LOG_TAG,currentStr);
        txtVersion.setText(String.format(currentStr,getString(R.string.version)));
    }
}
