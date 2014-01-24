package com.learnit.LearnIt.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.LearnIt.R;

public class LoadStarDictUiFragment extends Fragment {
    private TextView _tvTitle;
    private TextView _tvDictName;
    private TextView _tvDictInfo;
    private TextView _tvLoaded;
    private TextView _tvCountdown;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dict_to_sql, container, false);
        _tvTitle = (TextView) v.findViewById(R.id.text_dict_to_sql_title);
//        _tvDictName = (TextView) v.findViewById(R.id.text_dictionary_name);
        _tvDictInfo = (TextView) v.findViewById(R.id.text_dictionary_info);
//        _tvLoaded = (TextView) v.findViewById(R.id.text_loaded);
//        _tvCountdown = (TextView) v.findViewById(R.id.text_countdown);
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
        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("blah", "saving state");
        outState.putString("Title", _tvTitle.getText().toString());
        outState.putString("DictName", _tvDictName.getText().toString());
        outState.putString("DictInfo", _tvDictInfo.getText().toString());
        outState.putString("Loaded", _tvLoaded.getText().toString());
    }

    protected void setTimerText(String text)
    {
//        _tvCountdown.setText(text);
    }

    public void setTitleText(String text)
    {
        if (_tvTitle!=null)
            _tvTitle.setText(text);
    }

    public void setDictNameText(String text)
    {
//        if (_tvDictName!=null)
//            _tvDictName.setText(text);
    }

    public void setDictInfoText(String text)
    {
        if (_tvDictInfo!=null)
            _tvDictInfo.setText(text);
    }

    public void setLoadedText(String text)
    {
//        if (_tvLoaded!=null)
//            _tvLoaded.setText(text);
    }

}
