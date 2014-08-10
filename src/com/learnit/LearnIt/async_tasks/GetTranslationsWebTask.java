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
import android.util.Pair;

import com.learnit.LearnIt.interfaces.IWorkerEventListener;
import com.learnit.LearnIt.interfaces.IWorkerEventListenerTranslations;
import com.learnit.LearnIt.utils.Constants;
import com.learnit.LearnIt.utils.StringUtils;
import com.learnit.LearnIt.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GetTranslationsWebTask extends MySmartAsyncTask<Pair<String, List<String>>> {
    final static String GOOGLE_COMPLETE_URL
            = "http://glosbe.com/gapi_v0_1/translate?from=%s&dest=%s&format=json&phrase=%s&pretty=true&tm=false";
    final static String ENCODING = "UTF-8";

    private HttpClient client = new DefaultHttpClient();

    private String _word;
	private String _langFrom, _langTo;


	public GetTranslationsWebTask(String word)
	{
		super();
		_word = word;
	}

	public void updateContextAndCallback(Context context,
	                                     IWorkerEventListener taskActionCallback)
	{
		super.updateContextAndCallback(context, taskActionCallback);
		Pair<String, String> langPair = Utils.getCurrentLanguages(context);
		if (langPair == null)
		{
			Log.e(Constants.LOG_TAG, "updateContextAndCallback pair is null!!!!!!!!!!!!!!!!");
		}
		_langFrom = langPair.first.toLowerCase();
		_langTo = langPair.second.toLowerCase();
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Pair<String, List<String>> articleTranslationsListPair) {
		super.onPostExecute(articleTranslationsListPair);
		if (articleTranslationsListPair == null)
		{
			_taskActionCallback.onFail();
			return;
		}
		if (_taskActionCallback instanceof IWorkerEventListenerTranslations) {
			((IWorkerEventListenerTranslations) _taskActionCallback).onSuccessTranslations(articleTranslationsListPair);
		} else {
			throw new ClassCastException(
					_taskActionCallback.getClass().getSimpleName()
							+ " must implement "
							+ IWorkerEventListenerTranslations.class.getSimpleName());
		}
	}

	@Override
	protected Pair<String, List<String>> doInBackground(Object... unused) {
        String newWord = StringUtils.stripFromArticle(_context, _word);
        String fullUrl = null;
        StringBuilder builder = new StringBuilder();
        try {
            fullUrl = String.format(
                    GOOGLE_COMPLETE_URL,
                    _langFrom,
                    _langTo,
                    URLEncoder.encode(newWord, ENCODING));
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                try {
                    JSONObject jsonReader = new JSONObject(builder.toString());
                    JSONArray tuc = jsonReader.getJSONArray("tuc");
                    for (int i = 0; i < tuc.length(); i++) {
                        JSONObject obj = tuc.getJSONObject(i);
                        words.add(obj.getJSONObject("phrase").getString("text"));
                    }
                }
                catch (JSONException ex)
                {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Log.e(Constants.LOG_TAG, ex.getMessage());
        }
        return new Pair<String, List<String>>(null, new ArrayList(words));
	}
}