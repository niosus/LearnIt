/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */
package com.learnit.LearnIt;

/**
 * This class is used to store entries in .idx file.
 * @author kien
 */
public class WordEntry {

    /** lower case of str_word. */
    private String strLwrWord;

    /** Word. */
    private String strWord;

    /** position of meaning of this word in ".dict" file. */
    private long longOffset;

    /** length of the meaning of this word in ".dict" file. */
    private long longSize;

    /**
     * Set the value for longSize.
     * @param longSize the longSize to set
     */
    public void setLongSize(long longSize) {
        this.longSize = longSize;
    }

    /**
     * Get value of longSize.
     * @return the longSize
     */
    public long getLongSize() {
        return longSize;
    }

    /**
     * Set the value for longOffset.
     * @param longOffset the longOffset to set
     */
    public void setLongOffset(long longOffset) {
        this.longOffset = longOffset;
    }

    /**
     * Get value of longOffset.
     * @return the longOffset
     */
    public long getLongOffset() {
        return longOffset;
    }

    /**
     * Set the value for strWord.
     * @param strWord the strWord to set
     */
    public void setStrWord(String strWord) {
        this.strWord = strWord;
    }

    /**
     * Get value of strWord.
     * @return the strWord
     */
    public String getStrWord() {
        return strWord;
    }

    /**
     * Set the value for strLwrWord.
     * @param strLwrWord the strLwrWord to set
     */
    public void setStrLwrWord(String strLwrWord) {
        this.strLwrWord = strLwrWord;
    }

    /**
     * Get value of strLwrWord.
     * @return the strLwrWord
     */
    public String getStrLwrWord() {
        return strLwrWord;
    }
}