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

package com.learnit.LearnIt.interfaces;

import android.view.View;

import com.learnit.LearnIt.data_types.ArticleWordId;

import java.util.ArrayList;

/**
 * Created by igor on 4/2/14.
 */
public interface ILearnFragmentUpdate {
	public void setQueryWordText(ArticleWordId struct, int direction);
	public void setQueryWordTextFail();
	public void setButtonTexts(ArrayList<ArticleWordId> words, int direction);
	public void setAll(int visibilityState);
	public void openButtons();
	public void openWord();
	public void closeButtons();
	public void closeWord();
	public void shakeView(View v);
	public void updateWordWeight(int numOfWrongAnswers);
	public void updateDirectionOfTranslation();
}
