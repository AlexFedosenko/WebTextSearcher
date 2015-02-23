package com.alexander.webtextsearcher.searcher.core;


import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessWebPageTask extends AsyncTask<String,String,String> {

    private static final String LOG_TAG = "ProcessWebPageTask";
    private static final String BODY_START = "<body>";
    private static final String BODY_END = "</body>";

    private static final String URL_TAG = "url";
    private static final String TEXT_TAG = "text";
    private static final String STATUS_TAG = "status";
    private final SearchController mSearchController;

    private boolean allowSearch;

    public ProcessWebPageTask(SearchController controller) {
        mSearchController = controller;
        mThreadControl = new ThreadControl();
        allowSearch = false;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.v(LOG_TAG, "Thread #" + this.toString() + " started");
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(strings[0]);
        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String line;
            while ((line = buffer.readLine()) != null) {
                //Pause work if control is paused.
                mThreadControl.waitIfPaused();
                //Stop work if control is cancelled.
                if (mThreadControl.isCancelled()) {
                    break;
                }
                if (line.trim().equals(BODY_START)) {
                    allowSearch = true;
                }
                if (line.trim().equals(BODY_END)) {
                    allowSearch = false;
                }
                if (allowSearch) {
                    List<String> urls = Utils.findUrls(line);
                    for (String url : urls) {
                        publishProgress(URL_TAG, url);
                    }
                    List<String> foundTexts = Utils.findTargetText(line, mSearchController.getTargetText(), Boolean.parseBoolean(strings[1]));
                    for (String sentence : foundTexts) {
                        publishProgress(TEXT_TAG, sentence);
                    }
                }
            }

        } catch (Exception e) {
            String[] error = e.getClass().toString().split("\\.");
            publishProgress(STATUS_TAG, error[error.length - 1]);
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Thread #" + this.toString() + " finished");
        return strings[0];
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values[0].equals(URL_TAG)) {
            mSearchController.addUrl(values[1]);
        }
        if (values[0].equals(TEXT_TAG)) {
            mSearchController.addFoundText(values[1]);
        }
        if (values[0].equals(STATUS_TAG)) {
            mSearchController.showErrorStatus(values[1]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        mSearchController.removeTask(this, s);
        super.onPostExecute(s);
    }

}
