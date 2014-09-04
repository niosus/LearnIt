/*
 * Copyright (C) 2014  Igor Bogoslavskyi
 * This file is part of LearnIt.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.learnit.LearnIt.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.learnit.LearnIt.data_types.ArticleWordId;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

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

    public void setText(ArticleWordId entry, int type)
    {
        String textToSet;
        switch (type)
        {
            case SHOW_TRANSLATION:
                setText(StringUtils.splitOnRegex(entry.translation, ","));
                return;
            case SHOW_WORD:
                textToSet=StringUtils.splitOnRegex(entry.word, ",");
                if (null==entry.article) {
                    this.setText(textToSet);
                } else if ("".equals(entry.article)) {
                    this.setText(textToSet);
                } else {
                    if (Utils.getCurrentLanguages(this.getContext()).first.equals("de")) {
                        textToSet = StringUtils.capitalize(textToSet);
                    }
                    this.setText(entry.article + "\n" + textToSet);
                }
                return;
            default:
                setText("");
        }
    }
}
