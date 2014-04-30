
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

package com.learnit.LearnIt.stardict;

/**
 * This class is used to store word and its index.
 *
 * @author kien
 */
public class Word {

    /**
     * Word.
     */
    private String strWord = "";

    /**
     * index.
     */
    private int index = -1;

    /**
     * Set the value for index.
     *
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get value of index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the value for strWord.
     *
     * @param strWord the strWord to set
     */
    public void setStrWord(String strWord) {
        this.strWord = strWord;
    }

    /**
     * Get value of strWord.
     *
     * @return the strWord
     */
    public String getStrWord() {
        return strWord;
    }
}