/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.learnit.LearnIt.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowAllWordsActivity extends FragmentActivity {
    protected static final String LOG_TAG = "my_logs";
    protected DialogFragment frag;
    DBHelper dbHelper;
    Utils utils;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragTemp = new ShowWordsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragTemp)
                .commit();
    }

    public class ShowWordsFragment extends Fragment {
        private final String LOG_TAG = "my_logs";
        ActionMode mActionMode=null;
        MyTask mt;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            utils = new Utils();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.show_all_words, container, false);
            final ListView listView = (ListView) v.findViewById(R.id.list_of_all_words);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    HashMap<String, String> maplist = (HashMap<String, String>)parent.getAdapter().getItem(position);
                    String queryWord = maplist.get("word");
                    Log.d(LOG_TAG, queryWord);
                    String translation = maplist.get("translation");
                    showDialog(queryWord,translation, MyDialogFragment.DIALOG_SHOW_WORD);
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                // Called when the user long-clicks on someView
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                               long id) {
                    if (mActionMode != null) {
                        return false;
                    }
                    ListActionMode mActionModeCallback = new ListActionMode(view);
                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                    view.setSelected(true);
                    return true;
                }
            });
            return v;
        }

        private void updateList(List<Map<String, String>> items)
        {
            SimpleAdapter adapter;
            List<Map<String, String>> strings = items;
            adapter = new SimpleAdapter(this.getActivity(), strings,
                    android.R.layout.simple_list_item_2,
                    new String[] {"word", "translation" },
                    new int[] {android.R.id.text1, android.R.id.text2 });
            ListView list = (ListView) this.getView().findViewById(R.id.list_of_all_words);
            list.setAdapter(adapter);
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
            mt = new MyTask();
            mt.execute();
        }

        class MyTask extends AsyncTask<Void, Void, List<Map<String, String>> > {
            public int action=0;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog(null,null,MyDialogFragment.DIALOG_PROGRESS);
                dbHelper = new DBHelper(getActivity(), DBHelper.DB_WORDS);
            }


            @Override
            protected List<Map<String, String>> doInBackground(Void... word) {
                try {
                    List<Map<String, String>> items = dbHelper.getWords("");
                    return items;
                }
                catch (Exception e)
                {
                    Log.e("error", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> items) {
                super.onPostExecute(items);
                updateList(items);
                dismissDialog();
            }
        }

        void startEditWordActivity(String word)
        {
            Intent intent = new Intent(this.getActivity(), EditWord.class);
            intent.putExtra("word",word);
            startActivity(intent);
            Log.d(LOG_TAG,"start info activity called");
        }

        String stripWord(String word)
        {
            return utils.stripFromArticle(this.getActivity(), word);
        }

        private class ListActionMode implements ActionMode.Callback
        {
            private View v;

            public ListActionMode(View view) {
                v = view;
            }
            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                String queryWord = tv.getText().toString();
                Log.d(LOG_TAG,"item selected = " + queryWord);
                switch (item.getItemId()) {
                    case R.id.context_menu_edit:
                        mode.finish(); // Action picked, so close the CAB
                        startEditWordActivity(queryWord);
                        return true;
                    case R.id.context_menu_delete:
                        if (dbHelper.deleteWord(stripWord(queryWord)))
                        {
                            mt = new MyTask();
                            mt.action=1;
                            mt.execute();
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }
        }
    }
}