
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
     *
     * @param fileName get fileName and assign it to strFileName.
     */
    public DictFile(String fileName) {
        strFileName = fileName;
    }

    /**
     * Get Word meaning by its offset and its meaning size.
     *
     * @param offset offset that is get in .idx file.
     * @param size   size that is get in .idx file
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
     *
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