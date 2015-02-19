package com.alexander.webtextsearcher.searcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.core.SearchController;

public class ProgressFragment extends Fragment {

    private EditText vEdtUrl;
    private EditText vEdtTarget;
    private EditText vEdtThreads;
    private EditText vEdtUrls;

    private SearchController mSearchController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.progress_fragment, container, false);

        vEdtUrl = (EditText)rootView.findViewById(R.id.edt_url);
        // test
        vEdtUrl.setText("http://habrahabr.ru/post/81294/");
        if (!mSearchController.getUrl().isEmpty()) {
            vEdtUrl.setText(mSearchController.getUrl());
        }
        vEdtTarget = (EditText)rootView.findViewById(R.id.edt_target);
        if (!mSearchController.getTargetText().isEmpty()) {
            vEdtTarget.setText(mSearchController.getTargetText());
        }
        vEdtThreads = (EditText)rootView.findViewById(R.id.edt_threadAmount);
        if (mSearchController.getThreadAmount() != null) {
            vEdtThreads.setText(String.valueOf(mSearchController.getThreadAmount()));
        }
        vEdtUrls = (EditText)rootView.findViewById(R.id.edt_urlsAmount);
        if (mSearchController.getUrlAmount() != null) {
            vEdtUrls.setText(String.valueOf(mSearchController.getUrlAmount()));
        }

        return rootView;
    }

    public void setSearchController(SearchController searchController) {
        mSearchController = searchController;
    }

    public String getUrl() {
        return vEdtUrl.getText().toString();
    }

    public String getTargetText() {
        return vEdtTarget.getText().toString();
    }

    private String getThreads() {
        return vEdtThreads.getText().toString();
    }

    private String getUrls() {
        return vEdtUrls.getText().toString();
    }

    public Integer getThreadAmount() {
        return getThreads().isEmpty() ? null : Integer.parseInt(getThreads());
    }

    public Integer getUrlAmount() {
        return getUrls().isEmpty() ? null : Integer.parseInt(getUrls());
    }

    @Override
    public void onPause() {
        mSearchController.setUrl(getUrl());
        mSearchController.setTargetText(getTargetText());
        mSearchController.setThreadAmount(getThreadAmount());
        mSearchController.setUrlAmount(getThreadAmount());
        super.onPause();
    }
}
