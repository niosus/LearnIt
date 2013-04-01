/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class LearnFragment extends Fragment {

    View v;
    String queryWord = null;
    int numOfWrongAnswers=0;
    int direction = 0;
    Utils utils;
    final String LOG_TAG = "my_logs";
    DBHelper dbHelper;
    int[] btnIds = {R.id.left_top_button,
            R.id.right_top_button,
            R.id.left_bottom_button,
            R.id.right_bottom_button};

    public LearnFragment() {
    }

    @Override
    public void onResume()
    {
        super.onResume();
        utils = new Utils();
        Pair<String,String> langPair = utils.getCurrentLanguages(this.getActivity());
        Log.d(LOG_TAG, "onResume learn fragment: from - " + langPair.first + " to " + langPair.second);
        dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
        fetchNewWords();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser == true) {
            dbHelper = new DBHelper(this.getActivity(), DBHelper.DB_WORDS);
            playOpenAnimation();
            openButtons();
            v.findViewById(R.id.left_top_button).setVisibility(View.VISIBLE);
            v.findViewById(R.id.right_bottom_button).setVisibility(View.VISIBLE);
            v.findViewById(R.id.left_bottom_button).setVisibility(View.VISIBLE);
            v.findViewById(R.id.right_top_button).setVisibility(View.VISIBLE);
            v.findViewById(R.id.word_to_ask).setVisibility(View.VISIBLE);
            MainActivity.hideSoftKeyboard(this.getActivity());
        }
        else if (isVisibleToUser == false) {
            if (null!=v)
            {
                v.findViewById(R.id.left_top_button).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.right_bottom_button).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.left_bottom_button).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.right_top_button).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.word_to_ask).setVisibility(View.INVISIBLE);
            }
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.homework, container, false);
        return v;
    }

    private void fetchNewWords()
    {
        Random random = new Random();
        Log.d(LOG_TAG,"DB+WORDS=" + DBHelper.DB_WORDS);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String strDirection = sp.getString(getString(R.string.key_direction_of_trans),null);
        if (null!=strDirection)
        {
            direction = Integer.parseInt(strDirection);
            if (direction== Constants.MIXED)
            {
                direction = random.nextInt(2)+1;
            }
        }
        ArrayList<ArticleWordIdStruct> words = dbHelper.getRandomWords(btnIds.length,null,false);
        int correctIdx=0;
        if (words.size()==0)
        {
            TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
            queryWordTextView.setText(getString(R.string.learn_no_words));
        }
        else
        {
            correctIdx = random.nextInt(words.size());
            setQueryWordTxt(words.get(correctIdx));
        }

        MyButtonOnClick myButtonOnClick = new MyButtonOnClick();
        myButtonOnClick.correct=btnIds[correctIdx];
        (v.findViewById(R.id.left_top_button))
                .setOnClickListener(myButtonOnClick);
        (v.findViewById(R.id.right_bottom_button))
                .setOnClickListener(myButtonOnClick);
        (v.findViewById(R.id.left_bottom_button))
                .setOnClickListener(myButtonOnClick);
        (v.findViewById(R.id.right_top_button))
                .setOnClickListener(myButtonOnClick);
        setBtnTexts(words);
        v.findViewById(R.id.left_top_button).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.right_bottom_button).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.left_bottom_button).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.right_top_button).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.word_to_ask).setVisibility(View.INVISIBLE);
    }

    private void setQueryWordTxt(ArticleWordIdStruct struct)
    {
        TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
        queryWord=struct.word;
        switch (direction)
        {
            case Constants.FROM_FOREIGN_TO_MY:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                String learnLang = sp.getString(getString(R.string.key_language_from),"null");
                if (null!=struct.article)
                {
                    if ("de".equals(learnLang))
                    {
                        queryWordTextView.setText(struct.article + " " + utils.capitalize(struct.word));
                    }
                    else
                    {
                        queryWordTextView.setText(struct.article + " " + struct.word);
                    }
                }
                else if (null!=struct.prefix)
                {
                    queryWordTextView.setText(struct.prefix + " " + struct.word);
                }
                else
                {
                    queryWordTextView.setText(struct.word);
                }
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                queryWordTextView.setText(struct.translation);
                break;
        }

    }

    private void setBtnTexts(ArrayList<ArticleWordIdStruct> words)
    {
        switch (direction)
        {
            case Constants.FROM_FOREIGN_TO_MY:
                for (int i=0; i<btnIds.length; ++i)
                {
                    if (i>=words.size() || words.size()==0)
                    {
                        ((Button) v.findViewById(btnIds[i])).setText("");
                        (v.findViewById(btnIds[i])).setEnabled(false);
                    }
                    else
                    {
                        ((Button) v.findViewById(btnIds[i])).setText(words.get(i).translation);
                        (v.findViewById(btnIds[i])).setEnabled(true);
                    }
                }
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                for (int i=0; i<btnIds.length; ++i)
                {
                    if (i>=words.size() || words.size()==0)
                    {
                        ((Button) v.findViewById(btnIds[i])).setText("");
                        (v.findViewById(btnIds[i])).setEnabled(false);
                    }
                    else
                    {
                        ((Button) v.findViewById(btnIds[i])).setText(words.get(i).word);
                        (v.findViewById(btnIds[i])).setEnabled(true);
                    }
                }
                break;
        }

    }

    private void playCloseAnimation()
    {
        Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.close_word);
        TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
        queryWordTextView.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fetchNewWords();
                playOpenAnimation();
                openButtons();
                v.findViewById(R.id.left_top_button).setVisibility(View.VISIBLE);
                v.findViewById(R.id.right_bottom_button).setVisibility(View.VISIBLE);
                v.findViewById(R.id.left_bottom_button).setVisibility(View.VISIBLE);
                v.findViewById(R.id.right_top_button).setVisibility(View.VISIBLE);
                v.findViewById(R.id.word_to_ask).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private void playOpenAnimation()
    {
        Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.open_word);
        TextView queryWordTextView = (TextView) v.findViewById(R.id.word_to_ask);
        queryWordTextView.startAnimation(anim);
    }

    private void openButtons()
    {
        Animation animLeft = AnimationUtils.loadAnimation(this.getActivity(), R.anim.open_word);
        Animation animRight = AnimationUtils.loadAnimation(this.getActivity(), R.anim.open_word);
        (v.findViewById(R.id.left_top_button)).startAnimation(animLeft);
        (v.findViewById(R.id.right_bottom_button)).startAnimation(animRight);
        (v.findViewById(R.id.left_bottom_button)).startAnimation(animLeft);
        (v.findViewById(R.id.right_top_button)).startAnimation(animRight);
    }

    private void closeButtons()
    {
        Animation animLeft = AnimationUtils.loadAnimation(this.getActivity(), R.anim.close_word);
        Animation animRight = AnimationUtils.loadAnimation(this.getActivity(), R.anim.close_word);
        (v.findViewById(R.id.left_top_button)).startAnimation(animLeft);
        (v.findViewById(R.id.right_bottom_button)).startAnimation(animRight);
        (v.findViewById(R.id.left_bottom_button)).startAnimation(animLeft);
        (v.findViewById(R.id.right_top_button)).startAnimation(animRight);
    }

    private class MyButtonOnClick implements View.OnClickListener
    {
        public int correct = 0;
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (correct==id)
            {
                updateWordWeight();
                numOfWrongAnswers=0;
                playCloseAnimation();
                closeButtons();
            }
            else
            {
                numOfWrongAnswers++;
                showDialogWrong();
            }
        }
    }

    private void updateWordWeight()
    {
        Log.d(LOG_TAG, "word to be updated " + queryWord);
        switch (numOfWrongAnswers)
        {
            case 0:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_CORRECT_BUTTON);
                break;
            case 1:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_ONE_WRONG);
                break;
            case 2:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_TWO_WRONG);
                break;
            case 3:
                dbHelper.updateWordWeight(queryWord.toLowerCase(),DBHelper.WEIGHT_THREE_WRONG);
                break;
        }
    }

    private void showDialogWrong()
    {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(MyDialogFragment.ID_TAG, MyDialogFragment.DIALOG_WRONG_GUESS);
        frag.setArguments(args);
        frag.show(getFragmentManager(), "wrong_guess");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}