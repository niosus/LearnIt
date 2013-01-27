/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 1/27/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowAllWordsActivity extends FragmentActivity {
    protected static final String LOG_TAG = "my_logs";
    protected DialogFragment frag;
    DBHelper dbHelper;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragTemp = new ShowWordsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragTemp)
                .commit();
    }

    public class ShowWordsFragment extends Fragment {
        private final String LOG_TAG = "my_logs";
        MyTask mt;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mt = new MyTask();
            mt.execute();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.show_all_words, container, false);
            final ListView listView = (ListView) v.findViewById(R.id.list_of_all_words);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    String queryWord = ((TextView) view).getText().toString();
                    String tempStrippedWord = stripWordFromArticle(queryWord);
                    Log.d(LOG_TAG, queryWord);
                    String translation = getTranslation(tempStrippedWord);
                    showDialog(queryWord,translation, MyDialogFragment.DIALOG_SHOW_WORD);
                }
            });

            return v;
        }

        private String getTranslation(String word) {
            if (word != null && !word.isEmpty()) {
                Log.d(LOG_TAG, String.format("search word-%s", word));
                return dbHelper.getTranslation(word);
            }
            return null;
        }


        private String stripWordFromArticle(String str)
        {
            String[] tempArray = str.split(" ");
            Log.d(LOG_TAG, "str = " + str + ", array length = " + tempArray.length);
            switch (tempArray.length)
            {
                case 1:
                    return str;
                case 2:
                    return tempArray[1];
                default:
                    return null;
            }
        }

        private void updateList(ArrayList<String> items)
        {
            ArrayAdapter<String> adapter;
            ArrayList<String> strings = items;
            adapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_list_item_1, strings);
            ((ListView) this.getView().findViewById(R.id.list_of_all_words))
                    .setAdapter(adapter);
        }

        public void showDialog(String queryWord, String translation, int dialogType)
        {
            frag = new MyDialogFragment();
            Bundle args = new Bundle();
            args.putInt(MyDialogFragment.ID_TAG, dialogType);
            args.putString(MyDialogFragment.WORD_TAG, queryWord);
            args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "show_word_fragment_dialog");
        }

        private void dismissDialog()
        {
            frag.dismiss();
        }

        @Override
        public void onResume()
        {
            super.onResume();
        }

        class MyTask extends AsyncTask<Void, Void, ArrayList<String> > {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog(null,null,MyDialogFragment.DIALOG_PROGRESS);
                dbHelper = new DBHelper(getActivity());
            }


            @Override
            protected ArrayList<String> doInBackground(Void... word) {
                try {
                    ArrayList<String> items = dbHelper.getAllWords();
                    return items;
                }
                catch (Exception e)
                {
                    Log.e("error", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<String> items) {
                super.onPostExecute(items);
                updateList(items);
                dismissDialog();
            }
        }
    }
}