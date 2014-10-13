package com.learnit.LearnIt.activities;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.learnit.LearnIt.R;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

public class IntroActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) { actionBar.hide(); }

        setContentView(R.layout.activity_intro);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 1) {
                    // if we are on the second screen - after pushing button
                    // change its caption to 'finish'
                    mNextButton.setText(R.string.intro_button_finish);
                }
                if (mViewPager.getCurrentItem() == 2) {
                    // if we are on the third screen - after pushing the button
                    // we need to exit as intro is over
                    finishActivity();
                }
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });
    }

    private void finishActivity() {
        this.finish();
    }
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return Intro1.newInstance();
                case 1:
                    return Intro2.newInstance();
                case 2:
                    return Intro3.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }
    }

    /**
     * An intro fragment having only a logo and a small welcome text
     */
    public static class Intro1 extends Fragment {

        public static Intro1 newInstance() {
            return new Intro1();
        }

        public Intro1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_intro1, container, false);
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Intro2 extends Fragment {

        private Spinner mSpinnerLearn;
        private Spinner mSpinnerKnow;

        public static Intro2 newInstance() {
            return new Intro2();
        }

        public Intro2() {
        }

        private int getCurrentLangPos(
                final String currentVal,
                final int idValues) {
            String[] langsVals = getResources().getStringArray(idValues);
            for (int i = 0; i < langsVals.length; ++i) {
                if (langsVals[i].equals(currentVal)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_intro2, container, false);

            String languageLearn=PreferenceManager
                    .getDefaultSharedPreferences(this.getActivity())
                    .getString(getString(R.string.key_language_from), "");
            String languageKnow=PreferenceManager
                    .getDefaultSharedPreferences(this.getActivity())
                    .getString(getString(R.string.key_language_to), "");
            int langLearnPos = getCurrentLangPos(
                    languageLearn,
                    R.array.values_languages_from);
            int langKnowPos = getCurrentLangPos(
                    languageKnow,
                    R.array.values_languages_to);
            Log.d("my_logs", "language learn is currently " + languageLearn);
            Log.d("my_logs", "language know is currently " + languageKnow);
            AdapterView.OnItemSelectedListener listener
                    = new MyOnItemSelectedListener(this.getActivity());
            mSpinnerLearn = (Spinner) rootView.findViewById(R.id.spinner_lang_learn);
            mSpinnerKnow = (Spinner) rootView.findViewById(R.id.spinner_lang_know);
            mSpinnerLearn.setSelection(langLearnPos);
            mSpinnerKnow.setSelection(langKnowPos);

            mSpinnerLearn.setOnItemSelectedListener(listener);
            mSpinnerKnow.setOnItemSelectedListener(listener);
            return rootView;
        }

        private static class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

            private Context mContext;

            MyOnItemSelectedListener(Context context) {
                mContext = context;
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.spinner_lang_know:
                        String[] langsKnowValues =
                                mContext.getResources()
                                        .getStringArray(R.array.values_languages_to);
                        PreferenceManager
                                .getDefaultSharedPreferences(mContext)
                                .edit()
                                .putString(
                                        mContext.getString(R.string.key_language_to),
                                        langsKnowValues[position])
                                .apply();
                        break;
                    case R.id.spinner_lang_learn:
                        String[] langsLearnValues =
                                mContext.getResources()
                                        .getStringArray(R.array.values_languages_from);
                        PreferenceManager
                                .getDefaultSharedPreferences(mContext)
                                .edit()
                                .putString(
                                        mContext.getString(R.string.key_language_from),
                                        langsLearnValues[position])
                                .apply();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Intro3 extends Fragment {
        private Button mButton;

        public static Intro3 newInstance() {
            return new Intro3();
        }

        public Intro3() {
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        private boolean needToShowButton() {
            Pair<String, String> langs = Utils.getCurrentLanguages(getActivity());
            String key = langs.first + "-" + langs.second;
            Log.d(Constants.LOG_TAG, "searching for a dict app in market: " + key);
            return Constants.existingDictionaries.containsKey(key);
        }

        public void showMarketForDict() {
            Pair<String, String> langs = Utils.getCurrentLanguages(getActivity());
            String key = langs.first + "-" + langs.second;
            Log.d(Constants.LOG_TAG, "searching for a dict app in market: " + key);
            if (Constants.existingDictionaries.containsKey(key)) {
                // means that in the market there is a correct dict.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.existingDictionaries.get(key))));
            }
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                if (needToShowButton()) {
                    mButton.setVisibility(View.VISIBLE);
                } else {
                    mButton.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_intro3, container, false);
            mButton = (Button) rootView.findViewById(R.id.btn_go_to_market);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMarketForDict();
                }
            });
            return rootView;
        }
    }

}
