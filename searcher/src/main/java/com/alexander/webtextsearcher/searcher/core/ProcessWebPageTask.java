package com.alexander.webtextsearcher.searcher.core;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessWebPageTask extends AsyncTask<String,String,String> {

    private static final String URL_TAG = "url";
    private static final String TEXT_TAG = "text";
    private final SearchController mSearchController;

    public ProcessWebPageTask(SearchController controller) {
        mSearchController = controller;
    }

    @Override
    protected String doInBackground(String... strings) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(strings[0]);
        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String line;
            while ((line = buffer.readLine()) != null) {
                List<String> urls = Utils.findUrls(line);
                for (String url : urls) {
                    publishProgress(URL_TAG, url);
                }
                List<String> foundTexts = Utils.findTargetText(line, mSearchController.getTargetText());
                for (String sentence : foundTexts) {
                    publishProgress(TEXT_TAG, sentence);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values[0].equals(URL_TAG)) {
            mSearchController.addUrl(values[1]);
        }
        if (values[0].equals(TEXT_TAG)) {
            mSearchController.addFoundText(values[1]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        mSearchController.removeTask(this);
        super.onPostExecute(s);
    }

}
