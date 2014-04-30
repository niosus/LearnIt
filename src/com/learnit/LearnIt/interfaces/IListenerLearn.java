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

/**
 * Created by igor on 4/2/14.
 */
public interface IListenerLearn extends
		View.OnClickListener {
	public void fetchRandomWords(int numOfWords, ArticleWordId omitWord);
	public void setCorrectWordIdFromPrefs(int num);
	public int getCorrectWordId();
	public void showNext();
}
