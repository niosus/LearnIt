package com.learnit.LearnIt.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.LearnIt.R;

public class Dict2SqlFragment extends Fragment{
    private TextView _tvTitle;
    private TextView _tvDictName;
    private TextView _tvDictInfo;
    private TextView _tvLoaded;
    private TextView _tvCountdown;
    private MyProgressDialog _progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        _progressDialog = new MyProgressDialog();
        _progressDialog.show(getFragmentManager(),"my_progress");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dict_to_sql, container, false);
        _tvTitle = (TextView) v.findViewById(R.id.text_dict_to_sql_title);
        _tvTitle.setText(this.getString(R.string.dict_sql_no_dict));
        _tvDictName = (TextView) v.findViewById(R.id.text_dictionary_name);
        _tvDictInfo = (TextView) v.findViewById(R.id.text_dictionary_info);
        _tvLoaded = (TextView) v.findViewById(R.id.text_loaded);
        _tvCountdown = (TextView) v.findViewById(R.id.text_countdown);
        return v;
    }

    public void setStateSearching() {
        _progressDialog.setText(this.getString(R.string.dict_sql_progress_searching));
        _progressDialog.setIndeterminate(true);
    }

    public void setStateLoading() {
        _progressDialog.setText(this.getString(R.string.dict_sql_progress_found));
        _progressDialog.setIndeterminate(false);
    }

    public void noDictFound() {
        _progressDialog.dismiss();
        _tvTitle.setText(this.getString(R.string.dict_sql_no_dict));
    }

    public void onProgressUpdate(int progress) {
        _progressDialog.setProgress(progress);
    }

    public void onDictLoaded(String name) {
        _progressDialog.dismiss();
        _tvTitle.setText(this.getString(R.string.dict_sql_success));
        _tvDictInfo.setText(this.getString(R.string.dict_sql_version));
    }
}
