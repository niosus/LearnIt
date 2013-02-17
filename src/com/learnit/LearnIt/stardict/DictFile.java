/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package com.learnit.LearnIt.stardict;

import java.io.*;

/**
 * @author kien This class is used to read .dict file.
 */
public class DictFile {

    /**
     * String that get file name.
     */
    private String strFileName;

    /**
     * Constructor.
     * @param fileName get fileName and assign it to strFileName.
     */
    public DictFile(String fileName) {
        strFileName = fileName;
    }
    /**
     * Get Word meaning by its offset and its meaning size.
     * @param offset offset that is get in .idx file.
     * @param size size that is get in .idx file
     * @return meaning of word data
     */
    public String getWordData(long offset, long size) {
        if (!((new java.io.File(strFileName)).exists())) {
            return "File: " + strFileName + " does not exist";
        }
        String strMeaning = "not found";
        DataInputStream dt = null;
        try {
            dt = new DataInputStream(new BufferedInputStream(new FileInputStream(strFileName)));
            dt.skip(offset);
            byte[] bt = new byte[(int) size];
            dt.read(bt, 0, (int) size);
            strMeaning = new String(bt, "UTF8");
        } catch (Exception ex) {
        } finally {
            if (dt != null) {
                try {
                    dt.close();
                } catch (Exception ex) {
                }
            }
        }
        return strMeaning;
    }
    /**
     * Add data to .dict file.
     * @param strMeaning meaning of a paticular word.
     * @return size of strMeaning.
     */
    public long addData(String strMeaning) {
        DataOutputStream dt = null;
        long fileSize = -1;
        try {
            java.io.File f = new java.io.File(strFileName);
            fileSize = f.length();
            dt = new DataOutputStream(new FileOutputStream(strFileName, true));
            dt.write(strMeaning.getBytes("UTF8"));
        } catch (Exception ex) {
//            LOG.error("Read file name '" + strFileName + "'", ex);
        } finally {
            if (dt != null) {
                try {
                    dt.close();
                } catch (Exception ex) {
//                    LOG.warn("Closing DataOutputStream", ex);
                }
            }
        }
        return fileSize;
    }
}