package com.learnit.LearnIt.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.learnit.LearnIt.data_types.ArticleWordIdStruct;

public class WordButton extends Button {
    public static final int SHOW_WORD = 1;
    public static final int SHOW_TRANSLATION = 2;

    public WordButton(Context context)
    {
        super(context);
    }

    public WordButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WordButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public void setText(ArticleWordIdStruct entry, int type)
    {
        String textToSet;
        switch (type)
        {
            case SHOW_TRANSLATION:
                setText(entry.translation);
                return;
            case SHOW_WORD:
                textToSet=entry.word;
                if (null==entry.article)
                {
                    this.setText(textToSet);
                }
                else if ("".equals(entry.article))
                {
                    this.setText(textToSet);
                }
                else
                {
                    this.setText(entry.article + " " + textToSet);
                }
                return;
            default:
                setText("");
        }
    }
}
