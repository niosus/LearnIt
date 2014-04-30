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

import android.view.ActionMode;

import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public interface IDictFragmentUpdate {
	public void setListEntries(List<Map<String,String>> words);
	public void startActionMode(ActionMode.Callback callback);
	public void startEditWordActivity(String word);
	public void deleteWord(String word);
	public void setWordClearButtonVisible(boolean state);
	public void setWordText(String word);
}
