package com.learnit.LearnIt.data_types;

import android.os.AsyncTask;

public class MyTask extends AsyncTask {
    public static int GET_WORDS = 0;
    public static int GET_TRANSLATIONS = 1;
    public static int GET_DICT = 2;

    private static MyTask _instance;
    private int _type;

    private MyTask()
    {}

    public MyTask getInstance()
    {
        if (_instance==null)
        {
            _instance = new MyTask();
        }
        return _instance;
    }

    public void setType(int type)
    {
        _type = type;
    }

    @Override
    protected Object doInBackground(Object... params) {
        return null;
    }
}
