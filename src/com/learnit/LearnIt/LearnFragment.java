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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.homework, container, false);
        utils = new Utils();
        fetchNewWords();
        return v;
    }

    private void fetchNewWords()
    {
        Random random = new Random();
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
        int correctIdx = random.nextInt(btnIds.length);
        ArrayList<ArticleWordIdStruct> words = dbHelper.getRandomWords(4,null,false);
        setQueryWordTxt(words.get(correctIdx));
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
                for (int i=0; i<4; ++i)
                {
                    ((Button) v.findViewById(btnIds[i])).setText(words.get(i).translation);
                }
                break;
            case Constants.FROM_MY_TO_FOREIGN:
                for (int i=0; i<4; ++i)
                {
                    ((Button) v.findViewById(btnIds[i])).setText(words.get(i).word);
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
        Animation animLeft = AnimationUtils.loadAnimation(this.getActivity(), R.anim.float_in_left);
        Animation animRight = AnimationUtils.loadAnimation(this.getActivity(), R.anim.float_in_right);
        (v.findViewById(R.id.left_top_button)).startAnimation(animLeft);
        (v.findViewById(R.id.right_bottom_button)).startAnimation(animRight);
        (v.findViewById(R.id.left_bottom_button)).startAnimation(animLeft);
        (v.findViewById(R.id.right_top_button)).startAnimation(animRight);
    }

    private void closeButtons()
    {
        Animation animLeft = AnimationUtils.loadAnimation(this.getActivity(), R.anim.float_away_left);
        Animation animRight = AnimationUtils.loadAnimation(this.getActivity(), R.anim.float_away_right);
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        dbHelper = new DBHelper(this.getActivity());

    }
}