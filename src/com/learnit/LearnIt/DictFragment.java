package com.learnit.LearnIt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

public class DictFragment extends Fragment {
    protected static final String LOG_TAG = "my_logs";
    private DBHelper dbHelper;
    private EditText edtWord;


    View v;

    public DictFragment() {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item selection
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View v = info.targetView;
        String queryWord = ((TextView) v).getText().toString();
        Log.d(LOG_TAG,"item selected = " + queryWord);
        switch (item.getItemId()) {
            case R.id.context_menu_delete:
                if (dbHelper.deleteWord(queryWord))
                {
                    showDialog(queryWord,null,MyDialogFragment.DIALOG_WORD_DELETED);
                    edtWord.setText("");
                }
                return true;
            case R.id.context_menu_edit:
                return true;
            default:
                Log.d(LOG_TAG,"none selected");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dbHelper = new DBHelper(this.getActivity());
    }

    public void getWordsByPattern(String word) {
        ArrayAdapter<String> adapter;
        ArrayList<String> strings = new ArrayList<String>();
        if (word != null && !word.isEmpty()) {
            Log.d(LOG_TAG, String.format("search word by mask-%s", word));
            strings=dbHelper.getWords(word);
        }
        adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, strings);
        ((ListView) this.getView().findViewById(R.id.list_of_words))
                .setAdapter(adapter);
    }

    public String getTranslations(String word) {
        ArrayList<String> strings = new ArrayList<String>();
        if (word != null && !word.isEmpty()) {
            Log.d(LOG_TAG, String.format("search word-%s", word));
            strings=dbHelper.getTranslations(word);
        }
        return strings.get(0);
    }

    public void showDialog(String queryWord, String translation, int dialogType)
    {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(MyDialogFragment.ID_TAG, dialogType);
        args.putString(MyDialogFragment.WORD_TAG, queryWord);
        args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
        frag.setArguments(args);
        frag.show(getFragmentManager(), "show_word_fragment_dialog");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dict_fragment, container, false);

        edtWord = (EditText) v.findViewById(R.id.edv_search_word);
        edtWord.clearFocus();
        edtWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getWordsByPattern(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });
        final ListView listView = (ListView) v.findViewById(R.id.list_of_words);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String queryWord = ((TextView) view).getText().toString();
                Log.d(LOG_TAG, queryWord);
                edtWord.setText(queryWord);
                String translation = getTranslations(queryWord);
                showDialog(queryWord,translation, MyDialogFragment.DIALOG_SHOW_WORD);
            }
        });

        return v;
    }
}