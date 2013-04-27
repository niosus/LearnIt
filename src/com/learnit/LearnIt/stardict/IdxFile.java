
/**
 * Licensed to Open-Ones Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.learnit.LearnIt.stardict;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class is used for reading .idx file.
 * @author kien
 */
public class IdxFile {
    /** constant of 0x000000FF. */
    private final int byteFirst = 0x000000FF;

    /** constant of 0x0000FF00. */
    private final int secondByte = 0x0000FF00;

    /** constant of 0x00FF0000. */
    private final int thirdByte = 0x00FF0000;

    /** constant of 0xFF000000. */
    private final int fourthByte = 0xFF000000;

    /** constant of 0xFFFFFFFFL. */
    private final long fixthByte = 0xFFFFFFFFL;

    /** constant of number 3. */
    private final int noThree = 3;

    /** constant of number 8. */
    private final int noEight = 8;

    /** constant of number 9. */
    private final int noNine = 9;

    /** constant of number 16. */
    private final int noSixteen = 16;

    /** constant of number 24. */
    private final int noTwentyFour = 24;

    /** constant of number 4. */
    private final int aByte = 4;

    /** path to the ".idx" file. */
    private String strFileName;

    /** decide if the properties are loaded. */
    private boolean boolIsLoaded = false;

    /** number of word. */
    private long longWordCount;

    /** File size. */
    private long longIdxFileSize;

    /** store the list of entries. */
    private List<WordEntry> entryList;

    /**
     * constructor.
     * @param fileName path to .idx file.
     * @param wordCount number of word.
     * @param fileSize the file size.
     */
    public IdxFile(String fileName, long wordCount, long fileSize) {
        longWordCount = wordCount;
        longIdxFileSize = fileSize;
        strFileName = fileName;
        load();
    }

    /**
     * accessor of longIdxFileSize.
     * @return longIdxFileSize
     */
    public long getLongIdxFileSize() {
        return longIdxFileSize;
    }

    /**
     * accessor of boolIsLoaded.
     * @return boolIsLoaded
     */
    public boolean isLoaded() {
        return boolIsLoaded;
    }

    /**
     * accessor of longWordCount.
     * @return longWordCount
     */
    public long getLongWordCount() {
        return longWordCount;
    }

    /**
     * accessor of entryList.
     * @return entryList
     */
    public List<WordEntry> getEntryList() {
        return entryList;
    }

    /**
     * accessor of strFileName.
     * @return strFileName
     */
    public String getStrFileName() {
        return strFileName;
    }

    /**
     * load properties.
     */
    public void load() {
        if (boolIsLoaded || (!(new java.io.File(strFileName)).exists())) {
            return;
        }
        try {
            DataInputStream dt = new DataInputStream(new BufferedInputStream(new FileInputStream(strFileName)));
            byte[] bt = new byte[(int) longIdxFileSize];
            dt.read(bt);
            dt.close();
            entryList = new ArrayList<WordEntry>();
            int startPos; // start position of entry
            int endPos = 0; // end position of entry
            WordEntry tempEntry = null;
            for (long i = 0; i < longWordCount; i++) {
                tempEntry = new WordEntry();
                // read the word
                startPos = endPos;
                while (bt[endPos] != '\0') {
                    endPos++;
                }
                tempEntry.setStrWord(new String(bt, startPos, endPos - startPos, "UTF8"));
                tempEntry.setStrLwrWord(tempEntry.getStrWord().toLowerCase());
                // read the offset of the meaning (in .dict file)
                ++endPos;
                tempEntry.setLongOffset(readAnInt32(bt, endPos));
                // read the size of the meaning (in .dict file)
                endPos += aByte;
                tempEntry.setLongSize(readAnInt32(bt, endPos));
                endPos += aByte;
                entryList.add(tempEntry);
            }
            boolIsLoaded = true;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    /**
     * reload .idx file.
     */
    public void reload() {
        boolIsLoaded = false;
        load();
    }

    /**
     * convert 4 char array to an integer.
     * @param str array of byte that is read from .idx file.
     * @param beginPos the position of a word.
     * @return a long.
     */
    private long readAnInt32(byte[] str, int beginPos) {
        int firstByte = (byteFirst & ((int) str[beginPos]));
        int secondByte = (byteFirst & ((int) str[beginPos + 1]));
        int thirdByte = (byteFirst & ((int) str[beginPos + 2]));
        int fourthByte = (byteFirst & ((int) str[beginPos + noThree]));

        return ((long) (firstByte << noTwentyFour | secondByte << noSixteen | thirdByte << noEight | fourthByte))
                & fixthByte;
    }

    /**
     * convert an integer to a char array.
     * @param val an integer
     * @return a char array
     */
    private byte[] convertAnInt32(int val) {
        byte[] str = new byte[aByte];
        str[0] = (byte) ((val & fourthByte) >> noTwentyFour);
        str[1] = (byte) ((val & thirdByte) >> noSixteen);
        str[2] = (byte) ((val & secondByte) >> noEight);
        str[noThree] = (byte) ((val & byteFirst));
        return str;
    }

    /**
     * return the index of a word in entry list.
     * @param word the chosen word
     * @return index of this word
     */
    public long findIndexForWord(String word) {
        if (!boolIsLoaded) {
            return longWordCount;
        }
        long first = 0;
        long last = (int) longWordCount - 1;
        long mid;
        String lwrWord = word.toLowerCase();
        // use binary search
        do {
            mid = (first + last) / 2;
            int cmp = lwrWord.compareTo(((WordEntry) entryList.get((int) mid)).getStrLwrWord());
            if (cmp == 0) {
                return mid; // return index if found
            }
            if (cmp > 0) {
                first = mid + 1;
            } else {
                last = mid - 1;
            }
        } while (first <= last);
        // if not found
        /*
         * if (first < longWordCount) { while (first < longWordCount) { if (((WordEntry) entryList.get( (int)
         * first)).getStrLwrWord().compareTo(lwrWord) > 0) { break; } else { first++; } } }
         */
        first = -1;
        return first;
    }

    /**
     * Write to an .idx file.
     * @param fileName path to an .idx file
     * @return true if write success
     */
    public boolean write(String fileName) {
        try {
            DataOutputStream dt = new DataOutputStream(new FileOutputStream(fileName));
            // dt.write(firstBytes,0,Constants.byteFirst_POS_INDEX_FILE);
            WordEntry tempEntry = null;
            for (int i = 0; i < (int) longWordCount; i++) {
                tempEntry = entryList.get(i);
                dt.write(tempEntry.getStrWord().getBytes("UTF8"));
                dt.write('\0');
                dt.write(convertAnInt32((int) tempEntry.getLongOffset()));
                dt.write(convertAnInt32((int) tempEntry.getLongSize()));
            }
            dt.flush();
            dt.close();
            return true;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return false;
    }

    /**
     * return the result of the write(filename)function.
     * @return true if write success.
     */
    public boolean write() {
        return write(strFileName);
    }

    /**
     * add word, offset, size to the entryList.
     * @param word the chosen word.
     * @param offset the size of .dict file.
     * @param size the size of word meaning.
     * @param addPos position of added word
     * @return true if success.
     */
    public boolean addEntry(String word, long offset, long size, int addPos) {
        WordEntry etr = new WordEntry();
        etr.setStrWord(word);
        etr.setStrLwrWord(word.toLowerCase());
        etr.setLongOffset(offset);
        etr.setLongSize(size);
        if (addPos == -1) {
            addPos = (int) findIndexForWord(etr.getStrLwrWord());
        }
        if (addPos == longWordCount) {
            entryList.add(etr);
            this.longWordCount++;
            longIdxFileSize += (noNine + word.length());
            return true;
        } else if (etr.getStrLwrWord().compareTo(((WordEntry) this.entryList.get(addPos)).getStrLwrWord()) != 0) {
            entryList.add(addPos, etr);
            this.longWordCount++;
            longIdxFileSize += (noNine + word.length());
            return true;
        }
        return false;
    }

    /**
     * remove a word from entryList.
     * @param word the chosen word.
     * @return true if remove success.
     */
    public boolean removeEntry(String word) {
        String strLwrWord = word.toLowerCase();
        int pos = (int) findIndexForWord(strLwrWord);
        if (pos == longWordCount) {
            return false;
        } else if (strLwrWord.compareTo(((WordEntry) entryList.get(pos)).getStrLwrWord()) != 0) {
            return false;
        } else {
            this.longWordCount--;
            longIdxFileSize -= (noNine + word.length());
            entryList.remove(pos);
            return true;
        }
    }
}