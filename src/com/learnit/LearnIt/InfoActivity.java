package com.learnit.LearnIt;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class InfoActivity extends FragmentActivity {
    private final String LOG_TAG = "my_logs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
    }
}
