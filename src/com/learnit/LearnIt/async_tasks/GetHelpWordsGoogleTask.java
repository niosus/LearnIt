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

package com.learnit.LearnIt.async_tasks;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerHelpWords;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GetHelpWordsGoogleTask extends MySmartAsyncTask<List<String>> {
    final static String GOOGLE_COMPLETE_URL
            = "http://google.com/complete/search?q=%s&output=toolbar&hl=%s";
    String _word;
    private HttpClient client = new DefaultHttpClient();


    public GetHelpWordsGoogleTask(String word) {
        super();
        _word = word;
    }

    public void updateContextAndCallback(Context context,
                                         IWorkerEventListener taskActionCallback) {
        super.updateContextAndCallback(context, taskActionCallback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<String> words) {
        super.onPostExecute(words);
        if (words == null) {
            _taskActionCallback.onFail();
            return;
        }
        if (_taskActionCallback instanceof IWorkerEventListenerHelpWords) {
            ((IWorkerEventListenerHelpWords) _taskActionCallback).onSuccessWords(words);
        } else {
            throw new ClassCastException(
                    _taskActionCallback.getClass().getSimpleName()
                            + " must implement "
                            + IWorkerEventListenerHelpWords.class.getSimpleName()
            );
        }
    }

    @Override
    protected List<String> doInBackground(Object... unused) {
        String newWord = StringUtils.stripFromArticle(_context, _word);
        String fullUrl = null;
        try {
            fullUrl = String.format(
                    GOOGLE_COMPLETE_URL,
                    URLEncoder.encode(newWord, "UTF-8"),
                    Utils.getCurrentLanguages(_context).first);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        if (fullUrl == null) {
            return null;
        }
        Set<String> words = new TreeSet<>();
        HttpUriRequest getRequest = new HttpGet(fullUrl);
        try {
            HttpResponse getResponse = client.execute(getRequest);
            HttpEntity resEntityGet = getResponse.getEntity();

            if (resEntityGet != null) {
                InputStream in = resEntityGet.getContent();
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(in, null);
                    int event = parser.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT) {
                        String name = parser.getName();
                        switch (event) {
                            case XmlPullParser.START_TAG:
                                break;
                            case XmlPullParser.END_TAG:
                                if (name.equals("suggestion")) {
                                    String tempStr = parser.getAttributeValue(null, "data");
                                    String[] strArray = tempStr.split(" ");
                                    if (strArray.length > 0) {
                                        words.add(strArray[0]);
                                    }
                                }
                                break;
                        }
                        event = parser.next();
                    }
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                } finally {
                    in.close();
                }
            }
        } catch (IOException ex) {
            Log.e(Constants.LOG_TAG, ex.getMessage());
        }
        return new ArrayList(words);
    }
}