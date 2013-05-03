/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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