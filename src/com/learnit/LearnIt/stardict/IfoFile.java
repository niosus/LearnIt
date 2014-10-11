
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

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


/**
 * This class is used for reading .ifo file.
 *
 * @author kien
 */
public class IfoFile {

    /**
     * constant of 500.
     */
    private final int fixHundred = 500;

    /**
     * path to the ".ifo" file.
     */
    private String strFileName;

    /**
     * number of entries stored in ".idx" file.
     */
    private long longWordCount = 0;

    /**
     * the type of file.
     */
    private String strSameTypeSequence = "";

    /**
     * size of ".idx" file.
     */
    private long longIdxFileSize = 0;

    /**
     * decide if ".ifo" file is loaded.
     */
    private boolean boolIsLoaded = false;

    /**
     * version f dictionary.
     */
    private String strVersion = "";

    /**
     * name of dictionary.
     */
    private String strBookname = "";

    /**
     * author of dictionary.
     */
    private String strAuthor = "";

    /**
     * dict url.
     */
    private String strWebsite = "";

    /**
     * Description of book.
     */
    private String strDescription = "";

    /**
     * date.
     */
    private String strDate = "";

    /**
     * Constructor.
     *
     * @param fileName file name to the path of ifo file.
     */
    public IfoFile(String fileName) {
        strFileName = fileName;
        load();
    }

    /**
     * Set the value for longWordCount.
     *
     * @param longWordCount the longWordCount to set
     * @return longWordCount
     */
    public long setLongWordCount(long longWordCount) {
        this.longWordCount = longWordCount;
        return longWordCount;
    }

    /**
     * Set the value for longIdxFileSize.
     *
     * @param longIdxFileSize the longIdxFileSize to set
     * @return longIdxFileSize
     */
    public long setLongIdxFileSize(long longIdxFileSize) {
        this.longIdxFileSize = longIdxFileSize;
        return longIdxFileSize;
    }

    /**
     * Get value of longIdxFileSize.
     *
     * @return the longIdxFileSize
     */
    public long getLongIdxFileSize() {
        return longIdxFileSize;
    }

    /**
     * Get value of longWordCount.
     *
     * @return the longWordCount
     */
    public long getLongWordCount() {
        return longWordCount;
    }

    /**
     * Set the value for boolIsLoaded.
     *
     * @param boolIsLoaded the boolIsLoaded to set
     */
    public void setBoolIsLoaded(boolean boolIsLoaded) {
        this.boolIsLoaded = boolIsLoaded;
    }

    /**
     * Get value of boolIsLoaded.
     *
     * @return the boolIsLoaded
     */
    public boolean isBoolIsLoaded() {
        return boolIsLoaded;
    }

    /**
     * Set the value for strBookname.
     *
     * @param strBookname the strBookname to set
     */
    public void setStrBookname(String strBookname) {
        this.strBookname = strBookname;
    }

    /**
     * Get value of strBookname.
     *
     * @return the strBookname
     */
    public String getStrBookname() {
        return strBookname;
    }

    /**
     * Set the value for strVersion.
     *
     * @param strVersion the strVersion to set
     */
    public void setStrVersion(String strVersion) {
        this.strVersion = strVersion;
    }

    /**
     * Get value of strVersion.
     *
     * @return the strVersion
     */
    public String getStrVersion() {
        return strVersion;
    }

    /**
     * Load .idx .
     */
    public void load() {
        // check if the above properties are loaded
        if (isBoolIsLoaded()) {
            return;
        }
        // load the properties
        try {
            // initiate a file reader
            DataInputStream dt = new DataInputStream(new BufferedInputStream(new FileInputStream(strFileName)));
            byte[] bt = new byte[fixHundred];

            dt.read(bt); // read entire info file
            dt.close();
            String strInput = new String(bt, "UTF8"); // convert to utf8 string
            setStrVersion(getStringForKey("version=", strInput)); // get version

            // get number of entries
            if ((setLongWordCount(getLongForKey("wordcount=", strInput))) < 0) {
                return;
            }

            // get size of ".idx" file
            if ((setLongIdxFileSize(getLongForKey("idxfilesize=", strInput))) < 0) {
                return;
            }

            strSameTypeSequence = getStringForKey("sametypesequence=", strInput);
            setStrBookname(getStringForKey("bookname=", strInput));
            if (getStrBookname() == null) {
                return;
            }

            strAuthor = getStringForKey("author=", strInput);
            strWebsite = getStringForKey("website=", strInput);
            strDescription = getStringForKey("description=", strInput);
            strDate = getStringForKey("date=", strInput);
            // make sure that ifo file is loaded successfully
            setBoolIsLoaded(getLongWordCount() > 0);
        } catch (Exception ex) {
//            LOG.error("Loading file '" + strFileName + "'", ex);
            Log.d("my_logs", "Loading file '" + strFileName + "'" + " " + ex);
            ex.printStackTrace();
        }
    }

    /**
     * load the properties again.
     */
    public void reload() {
        setBoolIsLoaded(false);
        load();
    }

    /**
     * find a long number follows the key in a string.
     *
     * @param strKey the string key
     * @param str    string
     * @return long
     */
    long getLongForKey(String strKey, String str) {
        try {
            return Long.parseLong(getStringForKey(strKey, str).trim());
        } catch (Exception ex) {
            // LOG.error("getLongForKey", ex);
            return 0;
        }
    }

    /**
     * find a string follows the key in a string.
     *
     * @param strKey string key
     * @param str    string
     * @return string
     */
    String getStringForKey(String strKey, String str) {
        int keyLen = strKey.length();
        int startPos = str.indexOf(strKey) + keyLen;
        if (startPos < keyLen) {
            return null;
        }

        char[] strStr = str.toCharArray();
        int endPos = startPos - 1;

        while ((endPos < strStr.length - 1) && (strStr[++endPos] != '\n') && (strStr[endPos] != '\0')) {
            // LOG.debug("Keep continue");
        }
        return new String(strStr, startPos, endPos - startPos);
    }

    /**
     * write info file according to a specific file name.
     *
     * @param fileName path to .ifo file
     * @return true if success
     */
    boolean write(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter opw = new OutputStreamWriter(fos, "UTF8");
            opw.write("StarDict's dict ifo file\n");
            opw.write("version=" + getStrVersion() + "\n");
            opw.write("wordcount=" + getLongWordCount() + "\n");
            opw.write("idxfilesize=" + getLongIdxFileSize() + "\n");
            opw.write("bookname=" + getStrBookname() + "\n");
            opw.write("author=" + strAuthor + "\n");
            opw.write("website=" + strWebsite + "\n");
            opw.write("description=" + strDescription + "\n");
            opw.write("date=" + strDate + "\n");
            opw.write("sametypesequence=" + strSameTypeSequence + "\n");
            opw.flush();
            opw.close();
            fos.close();
        } catch (Exception ex) {
            // LOG.error("write", ex);
            return false;
        }
        return true;
    }

    /**
     * return result of write(fileName).
     *
     * @return true if write success
     */
    boolean write() {
        return write(strFileName);
    }
}