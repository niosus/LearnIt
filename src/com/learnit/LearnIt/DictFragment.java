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
    private ImageButton btnClear;


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
                showDialog(queryWord,null,MyDialogFragment.DIALOG_EDIT_WORD);
                edtWord.setText("");
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

    public String getTranslation(String word) {
        if (word != null && !word.isEmpty()) {
            Log.d(LOG_TAG, String.format("search word-%s", word));
            return dbHelper.getTranslation(word);
        }
        return null;
    }

    public void showDialog(String queryWord, String translation, int dialogType)
    {
        if (dialogType == MyDialogFragment.DIALOG_EDIT_WORD)
        {
            MyCustomEditDialog frag = new MyCustomEditDialog();
            Bundle args = new Bundle();
            args.putInt(MyDialogFragment.ID_TAG, dialogType);
            args.putString(MyDialogFragment.WORD_TAG, queryWord);
            args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "show_edit_word_fragment_dialog");
        }
        else
        {
            MyDialogFragment frag = new MyDialogFragment();
            Bundle args = new Bundle();
            args.putInt(MyDialogFragment.ID_TAG, dialogType);
            args.putString(MyDialogFragment.WORD_TAG, queryWord);
            args.putString(MyDialogFragment.TRANSLATION_TAG, translation);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "show_word_fragment_dialog");
        }
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
                if (s.toString()!="" && s.toString()!=null)
                {
                    btnClear.setVisibility(View.VISIBLE);
                }
                else
                {
                    btnClear.setVisibility(View.INVISIBLE);
                }
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
        btnClear = (ImageButton) v.findViewById(R.id.btn_search_clear);
        MyBtnTouchListener touchListener = new MyBtnTouchListener();
        btnClear.setOnClickListener(touchListener);
        btnClear.setVisibility(View.INVISIBLE);
        final ListView listView = (ListView) v.findViewById(R.id.list_of_words);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String queryWord = ((TextView) view).getText().toString();
                Log.d(LOG_TAG, queryWord);
                edtWord.setText(queryWord);
                String translation = getTranslation(queryWord);
                showDialog(queryWord,translation, MyDialogFragment.DIALOG_SHOW_WORD);
            }
        });

        return v;
    }

    private class MyBtnTouchListener implements View.OnClickListener
    {
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_search_clear:
                    edtWord.setText("");
                    btnClear.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    }
}